package com.xuecheng.filesystem.service;

import com.alibaba.fastjson.JSON;
import com.xuecheng.filesystem.dao.FileSystemRepository;
import com.xuecheng.framework.domain.filesystem.FileSystem;
import com.xuecheng.framework.domain.filesystem.response.FileSystemCode;
import com.xuecheng.framework.domain.filesystem.response.UploadFileResult;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import org.apache.commons.lang3.StringUtils;
import org.csource.fastdfs.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * @author hftang
 * @date 2019-03-11 14:39
 * @desc
 */

@Service
public class FileSystemService {

//    注入
    @Value("${xuecheng.fastdfs.tracker_servers}")
    String tracker_servers;
    @Value("${xuecheng.fastdfs.connect_timeout_in_seconds}")
    int connect_timeout_in_seconds;
    @Value("${xuecheng.fastdfs.network_timeout_in_seconds}")
    int network_timeout_in_seconds;
    @Value("${xuecheng.fastdfs.charset}")
    String charset;
    @Autowired
    FileSystemRepository fileSystemRepository;


    public UploadFileResult upload(MultipartFile multipartFile,
                                   String filetag,
                                   String businesskey,
                                   String metadata){
        //1 上传到 fastdfs 中
        String fileId=fastFS_upload(multipartFile);
        if(StringUtils.isEmpty(fileId)){
            ExceptionCast.cast(FileSystemCode.FS_UPLOADFILE_SERVERFAIL);
        }
        //2 将返回的 id 和 后面的三个信息 保存到 mongodb 中
        FileSystem fileSystem = new FileSystem();
        fileSystem.setFileId(fileId);
        fileSystem.setFilePath(fileId);
        fileSystem.setFiletag(filetag);
        fileSystem.setBusinesskey(businesskey);
        fileSystem.setFileName(multipartFile.getOriginalFilename());
        fileSystem.setFileType(multipartFile.getContentType());

        if(StringUtils.isNotEmpty(metadata)){
            //将这个转换成map
           try {
               Map map = JSON.parseObject(metadata, Map.class);
               fileSystem.setMetadata(map);
           }catch (Exception e){
               e.printStackTrace();
           }
        }

        try {
            fileSystemRepository.save(fileSystem);
        }catch (Exception e){
            e.printStackTrace();
        }

        return new UploadFileResult(CommonCode.SUCCESS,fileSystem);
    }

    /**
     * 上传文件到fastDFS
     * @param multipartFile
     * @return
     */
    private String fastFS_upload(MultipartFile multipartFile) {

        if(multipartFile==null){
            ExceptionCast.cast(FileSystemCode.FS_UPLOADFILE_FILEISNULL);
        }

        //初始化fastdfs
        initFdfsConfig();

        //创建trackerclient
        TrackerClient trackerClient = new TrackerClient();
        try {
            TrackerServer trackerServer = trackerClient.getConnection();
            //得到storage服务
            StorageServer storeStorage = trackerClient.getStoreStorage(trackerServer);

            StorageClient1 storageClient1=new StorageClient1(trackerServer,storeStorage);
            byte[] bytes = multipartFile.getBytes();
            //得到文件原始名称
            String originalFilename = multipartFile.getOriginalFilename();
            String ext_name = originalFilename.substring(originalFilename.lastIndexOf(".")+1);

            //上传文件
            String fileId = storageClient1.upload_file1(bytes, ext_name, null);

            return fileId;


        } catch (Exception e) {
            e.printStackTrace();
        }


        return null;
    }

    private void initFdfsConfig() {

        try {
            ClientGlobal.initByTrackers(tracker_servers);
            ClientGlobal.setG_charset(charset);
            ClientGlobal.setG_connect_timeout(connect_timeout_in_seconds);
            ClientGlobal.setG_network_timeout(network_timeout_in_seconds);

        } catch (Exception e) {
            e.printStackTrace();
            ExceptionCast.cast(FileSystemCode.FS_INITCLIENTGLOBAL_FAIL);
        }
    }
}
