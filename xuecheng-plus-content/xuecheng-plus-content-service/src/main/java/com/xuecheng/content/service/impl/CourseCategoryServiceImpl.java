package com.xuecheng.content.service.impl;

import com.xuecheng.content.mapper.CourseCategoryMapper;
import com.xuecheng.content.model.dto.CourseCategoryTreeDto;
import com.xuecheng.content.service.CourseCategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CourseCategoryServiceImpl implements CourseCategoryService {

    @Autowired
    CourseCategoryMapper courseCategoryMapper;

    @Override
    public List<CourseCategoryTreeDto> queryTreeNodes(String id) {
        //调用mapper递归查询出分类信息
        List<CourseCategoryTreeDto> courseCategoryTreeDtos = courseCategoryMapper.selectTreeNodes(id);

        //！！！找到每个节点的子结点，最终封装成List<CourseCategoryTreeDto>


        //先将List转成map，key是结点的的id，value就是CourseCategoryTreeDto对象，目的是为了方便从map中获取结点
        Map<String, CourseCategoryTreeDto> mapTemp = courseCategoryTreeDtos.stream()
                //过滤掉传进来为1的根节点
                .filter(
                        item -> !id.equals(item.getId())
                )
                .collect(
                        Collectors.toMap(
                                //id作为map的kye
                                key -> key.getId(),
                                //本身CourseCategoryTreeDto作为value
                                value -> value,
                                //如果两个value相同选择第一个key
                                (key1, key2) -> key2)
                );

        //定义一个List作为最终返回的list
        List<CourseCategoryTreeDto> courseCategoryTreeList = new ArrayList<>();

        //从头遍历List<CourseCategoryTreeDto> ,一边遍历一边找子节点放在父节点的childrenTreeNodes
        courseCategoryTreeDtos.stream().forEach(item -> {

            //如果父节点是根节点也就是1-1 ，1-2 ，1-3等等
            if (item.getParentid().equals(id)) {
                //就把它加入到树的一级目录上
                courseCategoryTreeList.add(item);
            }

            //如果找到了节点的父节点
            CourseCategoryTreeDto courseCategoryParent = mapTemp.get(item.getParentid());
            //先判断父节点是不是空（目的是排除id为1的根节点，根节点的courseCategoryParent必定为空）
            if (courseCategoryParent!=null){

                //courseCategoryParent是一级目录或者是二三四级...目录都有可能，递归调用
                if (courseCategoryParent.getChildrenTreeNodes()==null){
                    //如果该父节点的ChildrenTreeNodes属性为空就要New一个集合，因为要向集合中加中放入它的子节点
                    courseCategoryParent.setChildrenTreeNodes(new ArrayList<CourseCategoryTreeDto>());
                }
                //new 了以后肯定不空了
                //加入到每个节点的子节点放在父节点的childrenTreeNodes属性中
                courseCategoryParent.getChildrenTreeNodes().add(item);
            }


        });

        return courseCategoryTreeList;
    }
}
