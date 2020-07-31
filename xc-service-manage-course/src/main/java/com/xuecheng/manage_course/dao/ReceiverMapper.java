package com.xuecheng.manage_course.dao;

import com.xuecheng.manage_course.entity.Receiver;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @author hftang
 * @date 2020-07-30 9:12
 * @desc 接受者的mapper
 */
@Repository
@Mapper
public interface ReceiverMapper {

    //添加接受者
     int addReceiver(Receiver receiver);
     //根据领取者的id 查询他所有的领取者的分享者




}
