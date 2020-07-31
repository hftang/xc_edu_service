package com.xuecheng.manage_course.dao;

import com.xuecheng.manage_course.ManageCourseApplication;
import com.xuecheng.manage_course.entity.Receiver;
import com.xuecheng.manage_course.entity.ReceiverShare;
import com.xuecheng.manage_course.entity.Share;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * @author hftang
 * @date 2020-07-30 9:51
 * @desc
 */
@SpringBootTest(classes = ManageCourseApplication.class)
@RunWith(SpringRunner.class)
public class TestReceiver {
    @Autowired
    ReceiverMapper receiverMapper;
    @Autowired
    ReceiverShareMapper receiverShareMapper;
    @Autowired
    ShareMapper shareMapper;


    /**
     * 领取者 成功领取了
     * 知道 领取者的Id  分享者的ID
     * 查看领取者，领取的次数
     * 查看分享者，分享的次数
     * 给分享者加100 给 领取者加100
     *
     */

    @Test
    public void test001(){
        List<ReceiverShare> receiverShareList = this.receiverShareMapper.findReceiverShareById("001");
        receiverShareList.forEach(item-> System.out.println(item));

    }
    @Test
    public void test002(){
        List<ReceiverShare> shares = this.receiverShareMapper.findReceiverShareByShareId("003");
        shares.forEach(item-> System.out.println(item));
    }






    @Test
    public void test01() {

        Receiver receiver = new Receiver();
        receiver.setId(null);
        receiver.setReceiver_header("header---11");
        receiver.setReceiver_id("002");
        receiver.setScore(200);
        receiver.setRevciver_name("hftang");


        int i = this.receiverMapper.addReceiver(receiver);
        Integer id = receiver.getId();
        System.out.println("id：：" + id);
    }

    @Test
    public void test02() {
        ReceiverShare receiverShare = new ReceiverShare();
        receiverShare.setReceiver_id("001");
        receiverShare.setShare_id("002");
        int i = this.receiverShareMapper.insertReceiverShare(receiverShare);
        System.out.println(i);
    }

    @Test
    public void test03() {
        Share shareBean = new Share();
        shareBean.setId(null);
        shareBean.setShare_name("share_name");
        shareBean.setShare_header("http://www.header.com");
        shareBean.setShare_id("id_001");
        shareBean.setScore(100);

        int i = this.shareMapper.insertShareBean(shareBean);
        System.out.println(";;;->" + i);
    }

}
