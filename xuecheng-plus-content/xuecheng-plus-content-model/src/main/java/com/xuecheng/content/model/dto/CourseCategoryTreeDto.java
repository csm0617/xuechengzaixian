package com.xuecheng.content.model.dto;

import com.xuecheng.content.model.po.CourseCategory;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
//implements Serializable序列化用于网络传输
public class CourseCategoryTreeDto extends CourseCategory implements Serializable {
    //树形嵌套结构
    private List<CourseCategoryTreeDto> childrenTreeNodes ;

}
