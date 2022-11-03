package com.ljj.gulimall.coupon.service.impl;

import com.ljj.common.to.MemberPrice;
import com.ljj.common.to.SkureductionTo;
import com.ljj.gulimall.coupon.entity.MemberPriceEntity;
import com.ljj.gulimall.coupon.entity.SkuLadderEntity;
import com.ljj.gulimall.coupon.service.MemberPriceService;
import com.ljj.gulimall.coupon.service.SkuLadderService;
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

import com.ljj.gulimall.coupon.dao.SkuFullReductionDao;
import com.ljj.gulimall.coupon.entity.SkuFullReductionEntity;
import com.ljj.gulimall.coupon.service.SkuFullReductionService;


@Service("skuFullReductionService")
public class SkuFullReductionServiceImpl extends ServiceImpl<SkuFullReductionDao, SkuFullReductionEntity> implements SkuFullReductionService {

    @Autowired
    SkuLadderService skuLadderService;

    @Autowired
    MemberPriceService memberPriceService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuFullReductionEntity> page = this.page(
                new Query<SkuFullReductionEntity>().getPage(params),
                new QueryWrapper<SkuFullReductionEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveSkuReduction(SkureductionTo skureductionTo) {
        //ladder
        SkuLadderEntity skuLadder = new SkuLadderEntity();
        skuLadder.setSkuId(skureductionTo.getSkuId());
        skuLadder.setFullCount(skureductionTo.getFullCount());
        skuLadder.setDiscount(skureductionTo.getDiscount());
        skuLadderService.save(skuLadder);


        // full_reduction
        SkuFullReductionEntity skuFullReduction = new SkuFullReductionEntity();
        BeanUtils.copyProperties(skureductionTo,skuFullReduction);
        this.save(skuFullReduction);

        //member_price


        List<MemberPrice> memberPrices = skureductionTo.getMemberPrice();

        List<MemberPriceEntity> collect = memberPrices.stream().map(item -> {
            MemberPriceEntity memberPriceEntity = new MemberPriceEntity();
            memberPriceEntity.setSkuId(skureductionTo.getSkuId());
            memberPriceEntity.setMemberLevelId(item.getId());
            memberPriceEntity.setMemberLevelName(item.getName());
            memberPriceEntity.setMemberPrice(item.getPrice());
            memberPriceEntity.setAddOther(1);

            return memberPriceEntity;
        }).collect(Collectors.toList());
        memberPriceService.saveBatch(collect);
    }


}