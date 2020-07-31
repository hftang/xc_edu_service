package com.xuecheng.manage_course.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @author hftang
 * @date 2020-07-30 9:07
 * @desc 领取者表
 */
@Data
public class Receiver implements Serializable {

    private Integer id;
    private String receiver_id;
    private String receiver_header;
    private String revciver_name;
    private Integer score;


}
