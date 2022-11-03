package com.ljj.gulimall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.ljj.common.constant.ProductConstant;
import com.ljj.gulimall.product.dao.AttrAttrgroupRelationDao;
import com.ljj.gulimall.product.dao.AttrGroupDao;
import com.ljj.gulimall.product.dao.CategoryDao;
import com.ljj.gulimall.product.entity.AttrAttrgroupRelationEntity;
import com.ljj.gulimall.product.entity.AttrGroupEntity;
import com.ljj.gulimall.product.entity.CategoryEntity;
import com.ljj.gulimall.product.service.CategoryService;
import com.ljj.gulimall.product.vo.AttrGroupRelationVo;
import com.ljj.gulimall.product.vo.AttrRespVo;
import com.ljj.gulimall.product.vo.AttrVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ljj.common.utils.PageUtils;
import com.ljj.common.utils.Query;

import com.ljj.gulimall.product.dao.AttrDao;
import com.ljj.gulimall.product.entity.AttrEntity;
import com.ljj.gulimall.product.service.AttrService;


@Service("attrService")
public class AttrServiceImpl extends ServiceImpl<AttrDao, AttrEntity> implements AttrService {

    @Autowired
    private AttrAttrgroupRelationDao relationDao;

    @Autowired
    private AttrGroupDao attrGroupDao;

    @Autowired
    private CategoryDao categoryDao;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private AttrDao attrDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                new QueryWrapper<AttrEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void save(AttrVo attr) {
        AttrEntity attrEntity = new AttrEntity();
        BeanUtils.copyProperties(attr,attrEntity);
        //保存基本数据
        this.save(attrEntity);
        //保存关联关系
        if (attr.getAttrType() == ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode()){
            AttrAttrgroupRelationEntity relationEntity = new AttrAttrgroupRelationEntity();
            relationEntity.setAttrGroupId(attr.getAttrGroupId());
            relationEntity.setAttrId(attrEntity.getAttrId());
            relationDao.insert(relationEntity);
        }

    }

    @Override
    public PageUtils queryBaseAttrPage(Map<String, Object> params, Long catelogId, String type) {
        QueryWrapper<AttrEntity> queryWrapper = new QueryWrapper<AttrEntity>().
                eq("attr_type","base".equalsIgnoreCase(type)?
                        ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode():ProductConstant.AttrEnum.ATTR_TYPE_SALE.getCode());
        if (catelogId != 0){
            queryWrapper.eq("catelog_id",catelogId);
        }
        String key = (String) params.get("key");
        if (!StringUtils.isEmpty(key)){
            queryWrapper.and((wrapper)->{
                wrapper.eq("attr_id",key).or().eq("attr_name",key);
            });
        }
        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                queryWrapper
        );
        PageUtils pageUtils = new PageUtils(page);
        List<AttrEntity> records = page.getRecords();


        List<AttrRespVo> respVos = records.stream().map((attrEntity) -> {
            AttrRespVo attrRespVo = new AttrRespVo();
            BeanUtils.copyProperties(attrEntity, attrRespVo);
            //设置分类和分组的名字
            //查出组id
            if ("base".equalsIgnoreCase(type)){//判断是否是基础属性
                AttrAttrgroupRelationEntity relationEntity = relationDao.selectOne(
                        new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attrEntity.getAttrId()));
                if (relationEntity != null) {
                    AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(relationEntity.getAttrGroupId());
                    attrRespVo.setGroupName(attrGroupEntity.getAttrGroupName());
                }
            }

            CategoryEntity categoryEntity = categoryDao.selectById(attrEntity.getCatelogId());
            if (categoryEntity != null) attrRespVo.setCatelogName(categoryEntity.getName());

//            String attrGroupName = attrGroupDao.selectById(attrEntity.getAttrId()).getAttrGroupName();
//            if (attrGroupName != ""){
//                attrRespVo.setGroupName(attrGroupName);
//            }
//            String catelogName = categoryDao.selectById(attrEntity.getAttrId()).getName();
//            if (catelogName != ""){
//                attrRespVo.setCatelogName(catelogName);
//            }
            return attrRespVo;
        }).collect(Collectors.toList());

        pageUtils.setList(respVos);
        return pageUtils;

    }

    @Override
    public AttrRespVo getAttrInfo(Long attrId) {
        AttrRespVo attrRespVo = new AttrRespVo();
        AttrEntity attr = this.getById(attrId);
        BeanUtils.copyProperties(attr,attrRespVo);

        //设置分组信息
        if(attr.getAttrType() == ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode()){
            AttrAttrgroupRelationEntity relationEntity = relationDao.selectOne(
                    new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attr.getAttrId()));
            if (relationEntity != null){
                attrRespVo.setAttrGroupId(relationEntity.getAttrGroupId());
                AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(relationEntity.getAttrGroupId());
                if(attrGroupEntity != null){
                    attrRespVo.setGroupName(attrGroupEntity.getAttrGroupName());
                }
            }
        }

        //设置分类信息
        Long[] catelogPath = categoryService.getCatelogPath(attrRespVo.getCatelogId());
        attrRespVo.setCatelogPath(catelogPath);

        CategoryEntity categoryEntity = categoryDao.selectById(attrRespVo.getCatelogId());
        if (categoryEntity!= null){
            attrRespVo.setCatelogName(categoryEntity.getName());
        }
        return attrRespVo;

    }

    @Override
    public void updateAttr(AttrRespVo attr) {
        AttrEntity attrEntity = new AttrEntity();
        BeanUtils.copyProperties(attr,attrEntity);
        this.updateById(attrEntity);

        //修改关联分组
        if (attr.getAttrType() == ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode()){
            AttrAttrgroupRelationEntity relationEntity = new AttrAttrgroupRelationEntity();
            relationEntity.setAttrGroupId(attr.getAttrGroupId());
            relationEntity.setAttrId(attr.getAttrId());
            Integer count = relationDao.selectCount(
                    new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id",attr.getAttrId()));
            if (count > 0){
                relationDao.update(relationEntity,
                        new UpdateWrapper<AttrAttrgroupRelationEntity>().eq("attr_id",attr.getAttrId()));
            }else{
                relationDao.insert(relationEntity);
            }
        }

    }

    @Override
    public List<AttrEntity> getRelationAttr(Long attrgroupId) {
        List<AttrAttrgroupRelationEntity> list = relationDao.selectList(new QueryWrapper<AttrAttrgroupRelationEntity>()
                .eq("attr_group_id",attrgroupId));

        List<Long> collect = list.stream().map((attr) -> {
            return attr.getAttrId();
        }).collect(Collectors.toList());
        if (collect == null || collect.size() == 0){
            return null;
        }
        Collection<AttrEntity> attrGroupEntities = this.listByIds(collect);
        return (List<AttrEntity>) attrGroupEntities;
    }

    @Override
    public void deleteRelation(AttrGroupRelationVo[] vos) {
        List<AttrAttrgroupRelationEntity> collect = Arrays.asList(vos).stream().map((temp) -> {
            AttrAttrgroupRelationEntity relationEntity = new AttrAttrgroupRelationEntity();
            BeanUtils.copyProperties(temp, relationEntity);
            return relationEntity;
        }).collect(Collectors.toList());

        relationDao.deleteBatchRelations(collect);

    }

    @Override
    public PageUtils attrGroupNoRelation(Map<String, Object> params, Long attrgroupId) {
        /** 查出分组对应的分类id */
        AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(attrgroupId);
        Long catelogId = attrGroupEntity.getCatelogId();


        /** 获取该分类下所有分组 */
        List<AttrGroupEntity> catelog_id = attrGroupDao.selectList(new QueryWrapper<AttrGroupEntity>().eq("catelog_id", catelogId));
        List<Long> collect = catelog_id.stream().map((temp) -> {
            return temp.getAttrGroupId();
        }).collect(Collectors.toList());



        /** 查出这些分组所有关联属性 */
//        todo 效率太低
        List<Long> collect1 = new ArrayList<>();
//        for (Long i : collect){
//            List<AttrAttrgroupRelationEntity> attr_group_id = relationDao.selectList(
//                    new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_group_id", i));
//            List<Long> temp = attr_group_id.stream().map((item) -> {
//                return item.getAttrId();
//            }).collect(Collectors.toList());
//            collect1.addAll(temp);
//        }
        collect1 = relationDao.getRelationByListOfGroupId(collect);



        /** 从当前分类所有被关联的属性从中移除 */
        QueryWrapper<AttrEntity> wrapper = new QueryWrapper<AttrEntity>().eq(
                "catelog_id", catelogId).eq("attr_type",ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode());
        if(collect1 != null && collect1.size() != 0){
            wrapper.notIn("attr_id", collect1);
        }
        String key = (String) params.get("key");
        if (!StringUtils.isEmpty(key)){
            wrapper.and((w)->{
                w.eq("attr_id",key).or().like("attr_name",key);
            });
        }

        IPage<AttrEntity> page = this.page(new Query<AttrEntity>().getPage(params), wrapper);
        return new PageUtils(page);

    }
}