package com.xuecheng.manage_course.dao;

import com.xuecheng.manage_course.entity.ReceiverShare;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author hftang
 * @date 2020-07-30 13:36
 * @desc
 */
@Repository
@Mapper
public interface ReceiverShareMapper {

    int insertReceiverShare(ReceiverShare receiverShare);

    //根据领取者的id
    List<ReceiverShare> findReceiverShareById(String id);

    //根据分享者的id
    List<ReceiverShare> findReceiverShareByShareId(String shareId);


}
