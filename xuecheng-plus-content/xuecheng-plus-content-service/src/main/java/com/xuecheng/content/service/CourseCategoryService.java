package com.xuecheng.content.service;

import com.xuecheng.content.model.dto.CourseCategoryTreeDto;

import java.util.List;

public interface CourseCategoryService {
    /**
     * 课程分类树型结构查询
     * @param id
     * @return
     */
    public List<CourseCategoryTreeDto> queryTreeNodes(String id);
}
