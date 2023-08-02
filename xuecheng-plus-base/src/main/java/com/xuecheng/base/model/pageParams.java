package com.xuecheng.base.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 分页查询参数
 * !!!通用的分页查询参数应该放在基础模块部分或者通用模块
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class pageParams {

    //当前页码
    private Long pageNo=1L;
    //每页显示记录数
    private Long pageSize=30L;
}
