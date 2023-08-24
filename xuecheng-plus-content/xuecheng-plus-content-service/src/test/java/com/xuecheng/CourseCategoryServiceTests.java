package com.xuecheng;

import com.xuecheng.content.model.dto.CourseCategoryTreeDto;
import com.xuecheng.content.service.CourseCategoryService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@Slf4j
@SpringBootTest
public class CourseCategoryServiceTests {
    @Autowired
    CourseCategoryService courseCategoryService;

    @Test
    public void testCourseCategoryMapperTests(){

        List<CourseCategoryTreeDto> courseCategoryTreeDtoList = courseCategoryService.queryTreeNodes("1");

        log.info("课程分级目录{}",courseCategoryTreeDtoList);

    }

}
