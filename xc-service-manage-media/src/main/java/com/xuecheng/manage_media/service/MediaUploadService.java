package com.xuecheng.manage_media.service;

import com.alibaba.fastjson.JSON;
import com.xuecheng.framework.domain.media.MediaFile;
import com.xuecheng.framework.domain.media.response.CheckChunkResult;
import com.xuecheng.framework.domain.media.response.MediaCode;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_media.config.RabbitMQConfig;
import com.xuecheng.manage_media.dao.MediaFileRepository;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.*;

/**
 * @author hftang
 * @date 2019-03-25 15:21
 * @desc
 */
@Service
public class MediaUploadService {

    @Autowired
    MediaFileRepository mediaFileRepository;

    @Value("${xc-service-manage-media.upload-location}")
    String upload_location;

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Value("${xc-service-manage-media.mq.routingkey-media-video}")
    String routingkey;


    /***
     * 文件目录规则：
     *
     *  md5的第一位是一级目录
     *  MD5的第二位是erjimul
     *  MD5全名是第三层
     *  文件名称 md5+扩展名
     *
     * @param fileMd5
     * @param fileName
     * @param fileSize
     * @param mimetype
     * @param fileExt
     * @return
     */
    public ResponseResult regist(String fileMd5, String fileName, Long fileSize, String mimetype, String fileExt) {

        //1检查文件在磁盘上是否存在

        //文件目录所在的路径
        String fieldFoldPath = getFieldFoldPath(fileMd5);

        //文件的路径
        String filePath = getFilePath(fileMd5, fileExt);
        //检查文件是否存在
        File file = new File(filePath);
        boolean exists = file.exists();


        //2 检查文件信息在 mongodb中是否存在
        Optional<MediaFile> optional = mediaFileRepository.findById(fileMd5);
        if (optional.isPresent() && exists) {
            //文件已存在
            ExceptionCast.cast(MediaCode.UPLOAD_FILE_REGISTER_EXIST);
        }

        //文件不存在  文件所在的目录是否存在 不存在 创建
        File fileFolder = new File(fieldFoldPath);
        if (!fileFolder.exists()) {
            fileFolder.mkdirs();
        }

        return new ResponseResult(CommonCode.SUCCESS);
    }

    //文件的路径
    private String getFilePath(String fileMd5, String fileExt) {


        return upload_location + fileMd5.substring(0, 1) + "/" + fileMd5.substring(1, 2) + "/" + fileMd5 + "/" + fileMd5 + "." + fileExt;
    }

    //文件目录的路径
    private String getFieldFoldPath(String fileMd5) {


        return upload_location + fileMd5.substring(0, 1) + "/" + fileMd5.substring(1, 2) + "/" + fileMd5 + "/";
    }

    //分块的检查
    public CheckChunkResult checkchunk(String fileMd5, Integer chunk, Integer chunkSize) {

        //检查分块文件是否存在

        //得到分块文件所在目录
        String chunkFilePath = getChunkFilePath(fileMd5);
        //块文件
        File chunkfile = new File(chunkFilePath + chunk);
        if (chunkfile.exists()) {
            //文件块存在
            return new CheckChunkResult(CommonCode.SUCCESS, true);
        } else {
            //文件块不存在
            return new CheckChunkResult(CommonCode.SUCCESS, false);
        }
    }

    //得到块文件的目录；
    private String getChunkFilePath(String fileMd5) {

        return upload_location + fileMd5.substring(0, 1) + "/" + fileMd5.substring(1, 2) + "/" + fileMd5 + "/chunk/";
    }

    //上传分块
    public ResponseResult uploadChunk(MultipartFile file, String fileMd5, Integer chunk) {
        //检查一下分块的目录  如果不存在自动创建一下

        //得到分块目录
        String chunkFilePath = this.getChunkFilePath(fileMd5);

        File chunkFileFolder = new File(chunkFilePath);

        if (!chunkFileFolder.exists()) {
            //不存在
            chunkFileFolder.mkdirs();
        }
        //把文件传到分块目录下
        //得到上传文件的 输入流
        InputStream inputStream = null;
        FileOutputStream fileOutputStream = null;
        try {
            inputStream = file.getInputStream();
            fileOutputStream = new FileOutputStream(new File(chunkFilePath + chunk));
            IOUtils.copy(inputStream, fileOutputStream);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                inputStream.close();
                fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new ResponseResult(CommonCode.SUCCESS);
    }

    //合并分块
    public ResponseResult mergechunks(String fileMd5, String fileName, Long fileSize, String mimetype, String fileExt) {

        //1、合并所有的分块.
        //分块文件的所属目录
        String chunkFilePath = this.getChunkFilePath(fileMd5);
        File file = new File(chunkFilePath);
        File[] fileList = file.listFiles();

        //创建一个合并文件
        String filePath = this.getFilePath(fileMd5, fileExt);
        File mergeFile = new File(filePath);
        //合并文件
        List<File> files = Arrays.asList(fileList);
        File file_merge = mergeFile(files, mergeFile);
        if (file_merge == null) {
            //合并文件失败
            ExceptionCast.cast(MediaCode.MERGE_FILE_FAIL);
        }

        //2、校验文件的md5值 跟前段传过来的md5是一致

        boolean isCheck = checkFileMd5(fileMd5, file_merge);

        //3、将文件信息写入mongodb
        if (!isCheck) {
            ExceptionCast.cast(MediaCode.MERGE_FILE_CHECKFAIL);
        }

        MediaFile mediaFile = new MediaFile();
        mediaFile.setFileId(fileMd5);
        mediaFile.setFileOriginalName(fileName);
        mediaFile.setFileName(fileMd5 + "." + fileExt);
        //保存文件的相对路径
        String filerelativepath = fileMd5.substring(0, 1) + "/" + fileMd5.substring(1, 2) + "/" + fileMd5 + "/";
        mediaFile.setFilePath(filerelativepath);

        mediaFile.setFileSize(fileSize);
        mediaFile.setUploadTime(new Date());
        mediaFile.setMimeType(mimetype);
        mediaFile.setFileType(fileExt);
        //上传成功状态
        mediaFile.setFileStatus("301002");
        mediaFileRepository.save(mediaFile);
        //向mq发送消息

        sendProcessVideoMsg(mediaFile.getFileId());


        return new ResponseResult(CommonCode.SUCCESS);
    }

    //发送mq消息

    /**
     * 媒资文件id
     *
     * @param mediaId
     * @return
     */
    public ResponseResult sendProcessVideoMsg(String mediaId) {

        //mediaId对不对 要查询一下数据库
        Optional<MediaFile> optional = mediaFileRepository.findById(mediaId);
        if (!optional.isPresent()) {
            //非法不存的
            ExceptionCast.cast(CommonCode.FAIL);
        }

        //构造消息的内容
        Map<String, String> msg = new HashMap<>();
        msg.put("mediaId", mediaId);
        String jsonString = JSON.toJSONString(msg);

        try {
            //向mq发送消息
            /**
             * 路由
             * 路由key
             * 消息体
             */
            rabbitTemplate.convertAndSend(RabbitMQConfig.EX_MEDIA_PROCESSTASK,routingkey,jsonString);

        } catch (Exception e) {
           e.printStackTrace();
           //发送消息失败
            return new ResponseResult(CommonCode.FAIL);
        }


        return new ResponseResult(CommonCode.SUCCESS);
    }

    //校验合成文件的md5值
    private boolean checkFileMd5(String fileMd5, File file_merge) {

        //得到md5
        try {
            FileInputStream fileInputStream = new FileInputStream(file_merge);
            String md5Hex = DigestUtils.md5Hex(fileInputStream);

            //和传入的md5比较
            if (fileMd5.equalsIgnoreCase(md5Hex)) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private File mergeFile(List<File> fileList, File mergeFile) {
        try {

            //合并的文件存在 就删除
            if (mergeFile.exists()) {
                mergeFile.delete();
            } else {
                mergeFile.createNewFile();
            }
            //对块文件进行排序
            Collections.sort(fileList, new Comparator<File>() {
                @Override
                public int compare(File o1, File o2) {
                    if (Integer.parseInt(o1.getName()) > Integer.parseInt(o2.getName())) {
                        return 1;
                    }
                    return -1;
                }
            });

            //写文件的对象
            RandomAccessFile raf_write = new RandomAccessFile(mergeFile, "rw");
            byte[] b = new byte[1024];
            for (File file : fileList) {
                //读对象
                RandomAccessFile raf_read = new RandomAccessFile(file, "rw");
                int len = -1;
                while ((len = raf_read.read(b)) != -1) {
                    raf_write.write(b, 0, len);
                }
                raf_read.close();
            }
            raf_write.close();

            return mergeFile;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }
}
