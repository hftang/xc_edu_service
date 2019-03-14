package com.xuecheng.framework.domain.course.ext;

import com.xuecheng.framework.domain.course.CourseBase;
import com.xuecheng.framework.domain.course.CourseMarket;
import com.xuecheng.framework.domain.course.CoursePic;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author hftang
 * @date 2019-03-12 16:36
 * @desc
 */

@Data
@NoArgsConstructor
@ToString
public class CourseView implements Serializable {

    CourseBase courseBase;
    CourseMarket courseMarket;
    CoursePic coursePic;
    TeachplanNode teachplanNode;
}
