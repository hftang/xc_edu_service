package com.xuecheng.manage_course.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @author hftang
 * @date 2020-07-30 9:04
 * @desc 分享者表
 */
@Data
public class Share implements Serializable {

    private Integer id;
    private String share_id;
    private String share_header;
    private String share_name;
    private Integer score;

}
