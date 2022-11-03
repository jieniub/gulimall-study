package com.ljj.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ljj.common.utils.PageUtils;
import com.ljj.gulimall.product.entity.SpuInfoEntity;
import com.ljj.gulimall.product.vo.thirdvo.SpuSaveVo;

import java.util.Map;

/**
 * spu信息
 *
 * @author ljj
 * @email 1376058561@qq.com
 * @date 2022-10-16 13:26:53
 */
public interface SpuInfoService extends IService<SpuInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveInfo(SpuSaveVo vo);
}

