package com.ljj.gulimall.product.service.impl;

import com.ljj.common.to.SkureductionTo;
import com.ljj.common.to.SpuBoundTo;
import com.ljj.common.utils.R;
import com.ljj.gulimall.product.entity.*;
import com.ljj.gulimall.product.feign.CouponFeignService;
import com.ljj.gulimall.product.service.*;
import com.ljj.gulimall.product.vo.thirdvo.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ljj.common.utils.PageUtils;
import com.ljj.common.utils.Query;

import com.ljj.gulimall.product.dao.SpuInfoDao;
import org.springframework.transaction.annotation.Transactional;


@Service("spuInfoService")
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {

    @Autowired
    SpuInfoDescService spuInfoDescService;
    @Autowired
    SpuImagesService spuImagesService;

    @Autowired
    ProductAttrValueService productAttrValueService;

    @Autowired
    AttrService attrService;

    @Autowired
    SkuInfoService skuInfoService;

    @Autowired
    SkuImagesService skuImagesService;

    @Autowired
    SkuSaleAttrValueService skuSaleAttrValueService;

    @Autowired
    CouponFeignService couponFeignService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                new QueryWrapper<SpuInfoEntity>()
        );

        return new PageUtils(page);
    }

    @Transactional
    @Override
    public void saveInfo(SpuSaveVo vo) {
        /** 1、保存spu基本信息 pms_spu_info */
        SpuInfoEntity infoEntity = new SpuInfoEntity();
        BeanUtils.copyProperties(vo,infoEntity);
        infoEntity.setCreateTime(new Date());
        infoEntity.setUpdateTime(new Date());
        this.savebaseSpuInfo(infoEntity);
        /** 2、保存spu描述图片 pms_spu_info_desc */
        List<String> decript = vo.getDecript();
        SpuInfoDescEntity descEntity = new SpuInfoDescEntity();
        String join = String.join(",", decript);
        descEntity.setDecript(join);
        descEntity.setSpuId(infoEntity.getId());

        spuInfoDescService.saveSpuInfoDesc(descEntity);


        /** 3、保存spu的图片集 pms_spu_images */
        List<String> images = vo.getImages();
        spuImagesService.saveImages(infoEntity.getId(),images);

        /** 4、保存spu的规格参数 pms_product_attr_value */

        List<BaseAttrs> baseAttrs = vo.getBaseAttrs();

        List<ProductAttrValueEntity> collect = baseAttrs.stream().map((item) -> {
            ProductAttrValueEntity productAttrValueEntity = new ProductAttrValueEntity();

            productAttrValueEntity.setAttrId(item.getAttrId());

            String name = attrService.getById(item.getAttrId()).getAttrName();
            productAttrValueEntity.setAttrName(name);
            productAttrValueEntity.setAttrValue(item.getAttrValues());
            productAttrValueEntity.setQuickShow(item.getShowDesc());
            productAttrValueEntity.setSpuId(infoEntity.getId());

            return productAttrValueEntity;
        }).collect(Collectors.toList());

        productAttrValueService.saveProductAttr(collect);

        /** 5、保存spu的积分信息 gulimall_sms->sms_spu_bounds */
        //todo 远程调用sms

        Bounds bounds = vo.getBounds();
        SpuBoundTo spuBoundTo = new SpuBoundTo();
        BeanUtils.copyProperties(bounds,spuBoundTo);
        spuBoundTo.setSpuId(infoEntity.getId());

        R r = couponFeignService.save(spuBoundTo);
        if (r.getCode()!=0){
            log.error("远程保存积分信息失败");
        }


        List<Skus> skus = vo.getSkus();
        if (skus != null && skus.size() != 0){
            skus.forEach(item->{
                String defaultImg = "";

                for (Images img : item.getImages()){
                    if (img.getDefaultImg() == 1) defaultImg = img.getImgUrl();
                }

                SkuInfoEntity skuInfoEntity = new SkuInfoEntity();
                BeanUtils.copyProperties(item,skuInfoEntity);
                skuInfoEntity.setBrandId(infoEntity.getBrandId());
                skuInfoEntity.setCatalogId(infoEntity.getCatalogId());
                skuInfoEntity.setSaleCount(0L);
                skuInfoEntity.setSpuId(infoEntity.getId());
                skuInfoEntity.setSkuDefaultImg(defaultImg);

                /** sku的基本信息：pms_sku_info */
                skuInfoService.saveSkuInfo(skuInfoEntity);

                Long skuId = skuInfoEntity.getSkuId();

                List<SkuImagesEntity> imagesEntities = item.getImages().stream().map(img->{
                   SkuImagesEntity skuImagesEntity = new SkuImagesEntity();
                   skuImagesEntity.setSkuId(skuId);
                   skuImagesEntity.setImgUrl(img.getImgUrl());
                   skuImagesEntity.setDefaultImg(img.getDefaultImg());
                   return skuImagesEntity;
                }).collect(Collectors.toList());

                /** sku的图片信息：pms_sku_images */
                skuImagesService.saveBatch(imagesEntities);


                /** sku的销售属性信息：pms_sku_sale_attr_value */
                List<Attr> attr = item.getAttr();
                List<SkuSaleAttrValueEntity> skuSaleAttrValueEntities = attr.stream().map(a -> {
                    SkuSaleAttrValueEntity skuSaleAttrValueEntity = new SkuSaleAttrValueEntity();
                    BeanUtils.copyProperties(a, skuSaleAttrValueEntity);
                    skuSaleAttrValueEntity.setSkuId(skuId);

                    return skuSaleAttrValueEntity;
                }).collect(Collectors.toList());
                skuSaleAttrValueService.saveBatch(skuSaleAttrValueEntities);

                /** sku的优惠、满减等信息：gulimall_sms->sms_sku_ladder\sms_sku_full_reduction\sms_member_price */
                //todo 远程调用
                SkureductionTo skureductionTo = new SkureductionTo();
                BeanUtils.copyProperties(item,skureductionTo);
                skureductionTo.setSkuId(skuId);

                R r1 = couponFeignService.saveSkuReduction(skureductionTo);
                if (r1.getCode()!=0){
                    log.error("远程保存sku优惠信息失败");
                }

            });
        }

    }

    private void savebaseSpuInfo(SpuInfoEntity infoEntity) {
        this.baseMapper.insert(infoEntity);
    }

}