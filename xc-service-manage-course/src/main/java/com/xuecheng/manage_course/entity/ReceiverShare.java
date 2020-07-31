package com.xuecheng.manage_course.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author hftang
 * @date 2020-07-30 9:25
 * @desc 中间表
 */
@Data
public class ReceiverShare implements Serializable {
    private Integer id;
    private String share_id;
    private String receiver_id;

    private List<Receiver> receiverList = new ArrayList<>();
    private List<Share> shareList = new ArrayList<>();
}
