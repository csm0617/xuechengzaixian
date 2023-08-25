package com.xuecheng.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.content.mapper.CourseBaseMapper;
import com.xuecheng.content.mapper.CourseCategoryMapper;
import com.xuecheng.content.mapper.CourseMarketMapper;
import com.xuecheng.content.model.dto.AddCourseDto;
import com.xuecheng.content.model.dto.CourseBaseInfoDto;
import com.xuecheng.content.model.dto.QueryCourseParamsDto;
import com.xuecheng.content.model.po.CourseBase;
import com.xuecheng.content.model.po.CourseMarket;
import com.xuecheng.content.service.CourseBaseInfoService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CourseBaseInfoServiceImpl implements CourseBaseInfoService {

    @Autowired
    private CourseBaseMapper courseBaseMapper;

    @Autowired
    private CourseMarketMapper courseMarketMapper;

    @Autowired
    private CourseCategoryMapper categoryMapper;

    @Override
    public PageResult<CourseBase> queryCourseBaseList(PageParams pageParams, QueryCourseParamsDto queryCourseParamsDto) {

        //构造条件构造器
        LambdaQueryWrapper<CourseBase> queryWrapper = new LambdaQueryWrapper<>();
        //拼装查询条件
        queryWrapper.like(StringUtils.isNotEmpty(queryCourseParamsDto.getCourseName()), CourseBase::getName, queryCourseParamsDto.getCourseName());
        queryWrapper.eq(StringUtils.isNotEmpty(queryCourseParamsDto.getAuditStatus()), CourseBase::getAuditStatus, queryCourseParamsDto.getAuditStatus());
        queryWrapper.eq(StringUtils.isNotEmpty(queryCourseParamsDto.getPublishStatus()), CourseBase::getStatus, queryCourseParamsDto.getPublishStatus());
        Page<CourseBase> page = new Page<>(pageParams.getPageNo(), pageParams.getPageSize());

        Page<CourseBase> courseBasePage = courseBaseMapper.selectPage(page, queryWrapper);

        //todo 其实这里没必要这样的，这样自己搞一个PageResult类还不如直接返回mybatisPlus的page对象，这老师傻逼。
        List<CourseBase> records = courseBasePage.getRecords();
        long total = courseBasePage.getTotal();
        PageResult<CourseBase> courseBasePageResult = new PageResult<>(records, total, pageParams.getPageNo(), pageParams.getPageSize());
        return courseBasePageResult;
    }

    //凡是涉及到增删改的操作都要加上事务！！！
    @Transactional
    @Override
    public CourseBaseInfoDto creatCourseBase(Long companyId, AddCourseDto dto) {

        //1.传过来的参数要进行合法性校验，一般都在Controller层


        //2.业务逻辑性处理

        //2.1向课程基本信息表course_base写入数据
        CourseBase courseBaseNew = new CourseBase();
        BeanUtils.copyProperties(dto, courseBaseNew);
        courseBaseNew.setCompanyId(companyId);
        courseBaseNew.setCreateDate(LocalDateTime.now());
        //审核状态默认为未提交
        courseBaseNew.setAuditStatus("202002");
        //发布状态为未发布
        courseBaseNew.setStatus("203001");
        //插入数据库
        int insert = courseBaseMapper.insert(courseBaseNew);
        if (insert<=0){
            throw new RuntimeException("添加课程失败");
        }



        //2.2向课程营销信息course_market中写入数据
        CourseMarket courseMarketNew = new CourseMarket();
        //将页面输入的数据拷贝到courseMarketNew
        BeanUtils.copyProperties(dto,courseMarketNew);
        //课程id
        Long courseId = courseBaseNew.getId();
        courseMarketNew.setId(courseId);
        //保存营销信息
        int saveCourseMarket = saveCourseMarket(courseMarketNew);
        if (insert<=0){
            throw new RuntimeException("添加课程失败");
        }
        //从数据库中查询详细的课程信息，包括两部分
        CourseBaseInfoDto courseBaseInfo = getCourseBaseInfo(courseId);
        return courseBaseInfo;
    }

    //存在收费和不收费两种情况，所以单独写一个方法保存营销信息，逻辑：存在则更新，不存在则添加
    private int saveCourseMarket(CourseMarket courseMarketNew){

        //参数合法性校验
        String charge = courseMarketNew.getCharge();
        if (StringUtils.isEmpty(charge)){
            throw new RuntimeException("收费规则为空");
        }
        //如果课程收费，但是价格没有填写也需要抛出异常
        if (charge.equals("201001")){
            if (courseMarketNew.getPrice()==null||courseMarketNew.getPrice().floatValue()<=0){
                throw new RuntimeException("课程的价格不能为空并且必须大于0");
            }
        }
        //从数据库中查出营销信息，存在则更新，不存在则添加
        Long id = courseMarketNew.getId();
        CourseMarket courseMarket = courseMarketMapper.selectById(id);
        if (courseMarket==null){
            //没有这条数据就直接插入数据库
            int insert = courseMarketMapper.insert(courseMarketNew);
            return insert;
        }else {
            //将courseMarketNew拷贝到courseMarket
            BeanUtils.copyProperties(courseMarketNew,courseMarket);
            courseMarket.setId(courseMarketNew.getId());
            //更新数据库
            int update = courseMarketMapper.updateById(courseMarket);
            return update;
        }

    }
    //查询课程信息
    public CourseBaseInfoDto getCourseBaseInfo(long courseId){
        //从课程基本信息表中查询
        CourseBase courseBase = courseBaseMapper.selectById(courseId);
        if (courseBase==null){
            return null;
        }
        //从课程营销表中查询
        CourseMarket courseMarket = courseMarketMapper.selectById(courseId);


        //组装到一起
        CourseBaseInfoDto courseBaseInfoDto = new CourseBaseInfoDto();
        BeanUtils.copyProperties(courseBase,courseBaseInfoDto);
        BeanUtils.copyProperties(courseMarket,courseBaseInfoDto);
        //categoryMapper查询到课程分类名称
        String MtName = categoryMapper.selectById(courseBase.getMt()).getName();
        String StName = categoryMapper.selectById(courseBase.getSt()).getName();
        //课程分类名称设置到courseBaseInfoDto中
        courseBaseInfoDto.setMtName(MtName);
        courseBaseInfoDto.setStName(StName);
        return courseBaseInfoDto;

    }




}
