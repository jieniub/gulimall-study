package com.ljj.gulimall.product.service.impl;

import com.ljj.gulimall.product.dao.AttrAttrgroupRelationDao;
import com.ljj.gulimall.product.dao.AttrDao;
import com.ljj.gulimall.product.entity.AttrAttrgroupRelationEntity;
import com.ljj.gulimall.product.entity.AttrEntity;
import com.ljj.gulimall.product.service.AttrService;
import com.ljj.gulimall.product.vo.AttrGroupRelationVo;
import com.ljj.gulimall.product.vo.AttrGroupWithAttrVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ljj.common.utils.PageUtils;
import com.ljj.common.utils.Query;

import com.ljj.gulimall.product.dao.AttrGroupDao;
import com.ljj.gulimall.product.entity.AttrGroupEntity;
import com.ljj.gulimall.product.service.AttrGroupService;


@Service("attrGroupService")
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupDao, AttrGroupEntity> implements AttrGroupService {

    @Autowired
    private AttrAttrgroupRelationDao relationDao;

    @Autowired
    private AttrService attrService;


    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrGroupEntity> page = this.page(
                new Query<AttrGroupEntity>().getPage(params),
                new QueryWrapper<AttrGroupEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params, Long catelogId) {

        String key = (String) params.get("key");
        QueryWrapper<AttrGroupEntity> wrapper = new QueryWrapper<AttrGroupEntity>();

        if (!StringUtils.isEmpty(key)){
            wrapper.and((obj)->{
                obj.eq("attr_group_id",key).or().like("attr_group_name",key);
            });
        }

        if (catelogId == 0){
            IPage<AttrGroupEntity> page = this.page(new Query<AttrGroupEntity>().getPage(params),
                    wrapper);
            return new PageUtils(page);
        }else{
            wrapper.eq("catelog_id",catelogId);
            IPage<AttrGroupEntity> page = this.page(new Query<AttrGroupEntity>().getPage(params),
                    wrapper);
            return new PageUtils(page);
        }

    }

    @Override
    public List<AttrGroupWithAttrVo> getAttrGroupWithattr(Long catelogId) {
        List<AttrGroupEntity> attrGroupEntityList = this.list(new QueryWrapper<AttrGroupEntity>()
                .eq("catelog_id", catelogId));

        List<AttrGroupWithAttrVo> collect = attrGroupEntityList.stream().map((item) -> {
            AttrGroupWithAttrVo relationVo = new AttrGroupWithAttrVo();
            BeanUtils.copyProperties(item, relationVo);
            List<AttrEntity> attrs = attrService.getRelationAttr(item.getAttrGroupId());
            relationVo.setAttrs(attrs);
            return relationVo;
        }).collect(Collectors.toList());

        return collect;
    }

}