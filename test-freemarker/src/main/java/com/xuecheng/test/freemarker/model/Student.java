package com.xuecheng.test.freemarker.model;

import lombok.Data;
import lombok.ToString;

import java.util.Date;
import java.util.List;

/**
 * @author hftang
 * @date 2019-03-04 13:28
 * @desc
 */
@ToString
@Data
public class Student {
    private String name;
    private int age;
    private Date birthday;
    private Float money;
    private List<Student> friends;
    private Student bestFriend;
}
