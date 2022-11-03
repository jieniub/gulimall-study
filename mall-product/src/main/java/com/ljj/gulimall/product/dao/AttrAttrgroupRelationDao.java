package com.ljj.gulimall.product.dao;

import com.ljj.gulimall.product.entity.AttrAttrgroupRelationEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 属性&属性分组关联
 * 
 * @author ljj
 * @email 1376058561@qq.com
 * @date 2022-10-16 13:26:53
 */
@Mapper
public interface AttrAttrgroupRelationDao extends BaseMapper<AttrAttrgroupRelationEntity> {

    void deleteBatchRelations(@Param("collect") List<AttrAttrgroupRelationEntity> collect);

    List<Long> getRelationByListOfGroupId(@Param("groupIds") List<Long> list);
}
