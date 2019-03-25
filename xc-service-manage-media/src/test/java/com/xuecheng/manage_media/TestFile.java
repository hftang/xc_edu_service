package com.xuecheng.manage_media;

import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author hftang
 * @date 2019-03-25 9:51
 * @desc
 */
public class TestFile {

    //测试文件的分块

    @Test
    public void testChunk() {

        String source = "D:\\heima29\\lucene.avi";
        //源文件
        File sourceFile = new File(source);
        //块文件的目录
        String chunkFileFolder = "D:\\heima29\\chunk\\";

        //先定义块文件的大小
        long chunkFileSize = 1 * 1024 * 1024;
        //源文件能分成多少块
        long chunkFileNum = (long) Math.ceil(sourceFile.length() * 1.0f / chunkFileSize);

        //读文件的类

        try {
            RandomAccessFile raf_read = new RandomAccessFile(sourceFile, "r");
            byte[] b = new byte[1024];
            for (int i = 0; i < chunkFileNum; i++) {
                //块文件
                File chunkFile = new File(chunkFileFolder + i);

                //向块文件写
                RandomAccessFile raf_write=new RandomAccessFile(chunkFile,"rw");

                int len = -1;
                while ((len = raf_read.read(b)) != -1) {

                    raf_write.write(b,0,len);
                    //开始写下一块
                    if(chunkFile.length()>=chunkFileSize){
                        break;
                    }
                }

                raf_write.close();

            }

            raf_read.close();

        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    //测试文件的合并

    @Test
    public void testMergeFile(){
        //块文件的目录
        String chunkFilePath="D:\\heima29\\chunk\\";
        //块文件目录的对象
        File chunkFileFolder = new File(chunkFilePath);
        //块文件列表
        File[] files = chunkFileFolder.listFiles();

        //块文件排序 名称的升序
        List<File> fileList = Arrays.asList(files);

        Collections.sort(fileList, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {

                if(Integer.parseInt(o1.getName())>Integer.parseInt(o2.getName())){
                    return 1;
                }

                return -1;
            }
        });


        //合并的文件
        String source = "D:\\heima29\\lucene_merge.avi";
        //源文件
        File mergeFile = new File(source);

        try {
            //创建文件
            boolean newFile = mergeFile.createNewFile();
            //创建写对象
            RandomAccessFile raf_write = new RandomAccessFile(mergeFile, "rw");
            byte[] b=new byte[1024];
            for (File file : fileList) {
                //创建读块文件的对象
                RandomAccessFile raf_read = new RandomAccessFile(file, "rw");
                int len=-1;
                while ((len=raf_read.read(b))!=-1){
                    raf_write.write(b,0,len);
                }
                raf_read.close();
            }
            raf_write.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }


}
