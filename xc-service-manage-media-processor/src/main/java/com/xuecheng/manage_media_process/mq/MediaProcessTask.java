package com.xuecheng.manage_media_process.mq;

import com.alibaba.fastjson.JSON;
import com.xuecheng.framework.domain.media.MediaFile;
import com.xuecheng.framework.domain.media.MediaFileProcess_m3u8;
import com.xuecheng.framework.utils.HlsVideoUtil;
import com.xuecheng.framework.utils.Mp4VideoUtil;
import com.xuecheng.manage_media_process.dao.MediaFileRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author hftang
 * @date 2019-03-26 11:04
 * @desc 媒资视频处理 监听类
 */
@Component
public class MediaProcessTask {

    @Value("${xc-service-manage-media.ffmpeg-path}")
    String ffmpeg_path;
    @Value("${xc-service-manage-media.video-location}")
    String video_location;


    @Autowired
    MediaFileRepository mediaFileRepository;


    //接受视频处理消息 进行视频处理 配置上容器工厂 这样消费端就可以同时消费了
    @RabbitListener(queues = "${xc-service-manage-media.mq.queue-media-video-processor}",containerFactory = "customContainerFactory")
    public void receiveMediaProcessTask(String msg) {

        MediaFile mediaFile = new MediaFile();

        //1解析消息内容 得到mediaId
        Map map = JSON.parseObject(msg, Map.class);
        String mediaId = (String) map.get("mediaId");
        //2拿着mediaId从数据查询文件的信息
        Optional<MediaFile> optional = mediaFileRepository.findById(mediaId);
        if (!optional.isPresent()) {
            return;
        }
        MediaFile mediaFile1 = optional.get();
        String fileType = mediaFile1.getFileType();

        if (!"avi".equalsIgnoreCase(fileType)) {
            mediaFile1.setProcessStatus("303004");//无需处理
            mediaFileRepository.save(mediaFile1);
            return;
        } else {
            //更新状态在处理中
            mediaFile1.setProcessStatus("303001");//处理中
        }

        //3使用工具类将avi文件转成 mp4


        //要处理视频文件路径
        String video_path = video_location + mediaFile1.getFilePath() + mediaFile1.getFileName();
        //生成的MP4名字
        String mp4_name = mediaFile1.getFileId() + ".mp4";
        //MP4的目录
        String mp4Folder_path = video_location + mediaFile1.getFilePath();
        Mp4VideoUtil mp4VideoUtil = new Mp4VideoUtil(ffmpeg_path, video_path, mp4_name, mp4Folder_path);

        String result = mp4VideoUtil.generateMp4();

        if (result == null || !result.equalsIgnoreCase("success")) {
            //处理失败
            mediaFile1.setProcessStatus("303003");
            //处理失败原因
            MediaFileProcess_m3u8 mediaFileProcess_m3u8 = new MediaFileProcess_m3u8();
            mediaFileProcess_m3u8.setErrormsg(result);
            mediaFile1.setMediaFileProcess_m3u8(mediaFileProcess_m3u8);

            mediaFileRepository.save(mediaFile1);
            return;
        }

        //4将MP4 生成 m3u8和ts 然后存入数据库
        String mp4_video_path = video_location + mediaFile1.getFilePath() + mp4_name;
        //m3u8文件名称
        String m3u8FileName = mediaFile1.getFileId() + ".m3u8";
        //m3u8文件的目录
        String m3u8filePath = video_location + mediaFile1.getFilePath() + "hls/";

        HlsVideoUtil hlsVideoUtil = new HlsVideoUtil(ffmpeg_path, mp4_video_path, m3u8FileName, m3u8filePath);
        //ts 和 m3u8文件
        String m3u8_ts = hlsVideoUtil.generateM3u8();
        if (m3u8_ts == null || !m3u8_ts.equalsIgnoreCase("success")) {
            //处理失败
            mediaFile1.setProcessStatus("303003");
            //定义处理失败的对象
            MediaFileProcess_m3u8 mediaFileProcess_m3u8 = new MediaFileProcess_m3u8();
            mediaFileProcess_m3u8.setErrormsg(m3u8_ts);

            mediaFile1.setMediaFileProcess_m3u8(mediaFileProcess_m3u8);
            mediaFileRepository.save(mediaFile1);
        }

        //处理成功
        mediaFile1.setProcessStatus("303002");
        //记录ts列表
        List<String> ts_list = hlsVideoUtil.get_ts_list();
        MediaFileProcess_m3u8 mediaFileProcess_m3u8 = new MediaFileProcess_m3u8();
        mediaFileProcess_m3u8.setTslist(ts_list);
        mediaFile1.setMediaFileProcess_m3u8(mediaFileProcess_m3u8);
        //保存文件url
        String fileUrl = mediaFile1.getFilePath() + "hls/" + m3u8FileName;

        mediaFile1.setFileUrl(fileUrl);

        mediaFileRepository.save(mediaFile1);


    }


}
