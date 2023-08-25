package com.xuecheng.content.api;

import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.content.model.dto.AddCourseDto;
import com.xuecheng.content.model.dto.CourseBaseInfoDto;
import com.xuecheng.content.model.dto.QueryCourseParamsDto;
import com.xuecheng.content.model.po.CourseBase;
import com.xuecheng.content.service.CourseBaseInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Api(value = "课程信息编辑接口", tags = "课程信息编辑接口")
@RestController
public class CourseBaseInfoController {

    //  POST /content/course/list?pageNo=2&pageSize=1
    //{
    //  "auditStatus": "202002",
    //  "courseName": "",
    //  "publishStatus":""
    //}
    @Autowired
    private CourseBaseInfoService courseBaseInfoService;

    /**
     * 建议是把pageNo和pageSize写在路径变量里/list/pageNo/2/pageSize/1
     * 返回的是R<T>类再嵌套 PageResult
     * 因为有多个查询条件所以建议@RequestBody来接收前端传来的json数据并封装成对象
     *
     * @param pageParams
     * @param queryCourseParamsDto
     * @return
     */


    @ApiOperation("课程查询接口")
    @PostMapping("/course/list")
    //对于条件查询这种一般都设置成@RequestBody(required = false)，可以不强制传入
    public PageResult<CourseBase> list(PageParams pageParams, @RequestBody(required = false) QueryCourseParamsDto queryCourseParamsDto) {

        return courseBaseInfoService.queryCourseBaseList(pageParams, queryCourseParamsDto);

    }


    @PostMapping("/course")
    public CourseBaseInfoDto creatCourseBase(@RequestBody AddCourseDto addCourseDto){

        //获取到用户所属机构id
        Long companyId =1232141425L;

        CourseBaseInfoDto courseBaseInfoDto = courseBaseInfoService.creatCourseBase(companyId, addCourseDto);

        return courseBaseInfoDto;

    }

}
