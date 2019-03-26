package com.xuecheng.manage_media_process;

import com.xuecheng.framework.utils.Mp4VideoUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Administrator
 * @version 1.0
 * @create 2018-07-12 9:11
 **/
@SpringBootTest
@RunWith(SpringRunner.class)
public class TestProcessBuilder {

    @Test
    public void testProcessBuilder() throws IOException {

        //创建ProcessBuilder对象
        ProcessBuilder processBuilder = new ProcessBuilder();
        //设置执行的第三方程序(命令)
//        processBuilder.command("ping","127.0.0.1");
        processBuilder.command("ipconfig");
//        processBuilder.command("java","-jar","f:/xc-service-manage-course.jar");
        //将标准输入流和错误输入流合并，通过标准输入流读取信息就可以拿到第三方程序输出的错误信息、正常信息
        processBuilder.redirectErrorStream(true);

        //启动一个进程
        Process process = processBuilder.start();
        //由于前边将错误和正常信息合并在输入流，只读取输入流
        InputStream inputStream = process.getInputStream();
        //将字节流转成字符流
        InputStreamReader reader = new InputStreamReader(inputStream, "gbk");
        //字符缓冲区
        char[] chars = new char[1024];
        int len = -1;
        while ((len = reader.read(chars)) != -1) {
            String string = new String(chars, 0, len);
            System.out.println(string);
        }

        inputStream.close();
        reader.close();

    }

    //测试使用工具类将avi转成mp4
    @Test
    public void testProcessMp4() {
        //String ffmpeg_path, String video_path, String mp4_name, String mp4folder_path
        //ffmpeg的路径
        String ffmpeg_path = "D:\\Program Files\\ffmpeg-20180227-fa0c9d6-win64-static\\bin\\ffmpeg.exe";
        //video_path视频地址
        String video_path = "E:\\ffmpeg_test\\1.avi";
        //mp4_name mp4文件名称
        String mp4_name = "1.mp4";
        //mp4folder_path mp4文件目录路径
        String mp4folder_path = "E:/ffmpeg_test/";
        Mp4VideoUtil mp4VideoUtil = new Mp4VideoUtil(ffmpeg_path, video_path, mp4_name, mp4folder_path);
        //开始编码,如果成功返回success，否则返回输出的日志
        String result = mp4VideoUtil.generateMp4();
        System.out.println(result);
    }

    @Test
    public void testProcessBuilder02() {

        ProcessBuilder processBuilder = new ProcessBuilder();

        //设置第三的命令
        processBuilder.command("ping", "127.0.0.1");
        //将标准输入流 和 错误流 合并
        processBuilder.redirectErrorStream(true);
        //通过标准的输入流 来拿到正确 错误的输入信息了

        try {
            //先启动一个进程
            Process process = processBuilder.start();
            InputStream inputStream = process.getInputStream();
            //转成字符流

            InputStreamReader reader = new InputStreamReader(inputStream, "gbk");

            //读流
            int len = -1;
            char[] chars = new char[1024];
            while ((len = reader.read(chars)) != -1) {

                //拿
                String s = new String(chars, 0, len);
                System.out.println(s);

            }
            inputStream.close();
            reader.close();

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    @Test
    public void testFFmpeg02() {


        ProcessBuilder processBuilder = new ProcessBuilder();

        //设置第三的命令
//            processBuilder.command("ping","127.0.0.1");
        List<String> command = new ArrayList<>();
        command.add("D:\\tools\\ffmpeg\\ffmpeg-20180227-fa0c9d6-win64-static\\bin\\ffmpeg.exe");
        command.add("-i");
        command.add("D:\\develop\\lucene.avi");
        command.add("-y");
        command.add("-c:v");
        command.add("libx264");
        command.add("-s");
        command.add("1280*720");
        command.add("-pix_fmt");
        command.add("yuv420p");
        command.add("-b:a");
        command.add("63k");
        command.add("-b:v");
        command.add("753k");
        command.add("-r");
        command.add("18");
        command.add("D:\\heima29\\test\\test.mp4");
        processBuilder.command(command);

        //将标准输入流 和 错误流 合并
        processBuilder.redirectErrorStream(true);
        //通过标准的输入流 来拿到正确 错误的输入信息了

        try {
            //先启动一个进程
            Process process = processBuilder.start();
            InputStream inputStream = process.getInputStream();
            //转成字符流

            InputStreamReader reader = new InputStreamReader(inputStream, "gbk");

            //读流
            int len = -1;
            char[] chars = new char[1024];
            while ((len = reader.read(chars)) != -1) {

                //拿
                String s = new String(chars, 0, len);
                System.out.println(s);

            }
            inputStream.close();
            reader.close();

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    @Test
    public void test_videoToMp4(){

        //把avi 转成MP4

        String ffmpegpath="D:\\tools\\ffmpeg\\ffmpeg-20180227-fa0c9d6-win64-static\\bin\\ffmpeg.exe";
        String videpath="D:\\develop\\lucene.avi";//视频的路径
        String mp4Name="1.mp4";
        String mp4folder_path="D:\\heima29\\test\\";

        Mp4VideoUtil mp4VideoUtil = new Mp4VideoUtil(ffmpegpath,videpath,mp4Name,mp4folder_path);
        String result = mp4VideoUtil.generateMp4();
        System.out.println(result);


    }

}
