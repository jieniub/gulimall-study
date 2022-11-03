package com.ljj.gulimall.product.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.ljj.gulimall.product.entity.AttrEntity;
import com.ljj.gulimall.product.service.AttrAttrgroupRelationService;
import com.ljj.gulimall.product.service.AttrService;
import com.ljj.gulimall.product.service.CategoryService;
import com.ljj.gulimall.product.vo.AttrGroupRelationVo;
import com.ljj.gulimall.product.vo.AttrGroupWithAttrVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.ljj.gulimall.product.entity.AttrGroupEntity;
import com.ljj.gulimall.product.service.AttrGroupService;
import com.ljj.common.utils.PageUtils;
import com.ljj.common.utils.R;



/**
 * 属性分组
 *
 * @author ljj
 * @email 1376058561@qq.com
 * @date 2022-10-16 13:26:53
 */
@RestController
@RequestMapping("product/attrgroup")
public class AttrGroupController {
    @Autowired
    private AttrGroupService attrGroupService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private AttrService attrService;

    @Autowired
    private AttrAttrgroupRelationService relationService;

    /**
     * 列表
     */
    @RequestMapping("/list/{catelogId}")
    public R list(@RequestParam Map<String, Object> params,@PathVariable("catelogId") Long cetalogId){
//        PageUtils page = attrGroupService.queryPage(params);

        PageUtils page = attrGroupService.queryPage(params, cetalogId);
        return R.ok().put("page", page);
    }

    /**
     * 分组和属性的关联
     */
    @GetMapping("{attrgroupId}/attr/relation")
    public R getRelation(@PathVariable("attrgroupId") Long attrgroupId){
        List<AttrEntity> attrs =  attrService.getRelationAttr(attrgroupId);

        return R.ok().put("data",attrs);
    }

    /**
     * 获取与当前分组没有关系的属性
     */

    @GetMapping("{attrgroupId}/noattr/relation")
    public R attrGroupNoRelation(@RequestParam Map<String ,Object> params,
                                         @PathVariable("attrgroupId") Long attrgroupId){
        PageUtils page = attrService.attrGroupNoRelation(params,attrgroupId);

        return R.ok().put("page",page);
    }

    /**
     * 新镇分组与属性关联
     */
    @PostMapping("/attr/relation")
    public R saveAttrrelation(@RequestBody List<AttrGroupRelationVo> vos){
        relationService.saveBatch(vos);

        return R.ok();
    }

    /**
     * 删除分组属性关系
     */
    @PostMapping("/attr/relation/delete")
    public R deleteRelation(@RequestBody AttrGroupRelationVo[] vos){
        attrService.deleteRelation(vos);

        return R.ok();
    }

    /**
     * 信息
     */
    @RequestMapping("/info/{attrGroupId}")
    public R info(@PathVariable("attrGroupId") Long attrGroupId){
		AttrGroupEntity attrGroup = attrGroupService.getById(attrGroupId);

        Long catelogId = attrGroup.getCatelogId();
        Long[] path = categoryService.getCatelogPath(catelogId);


        attrGroup.setCatelogPath(path);

        return R.ok().put("attrGroup", attrGroup);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody AttrGroupEntity attrGroup){
		attrGroupService.save(attrGroup);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody AttrGroupEntity attrGroup){
		attrGroupService.updateById(attrGroup);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] attrGroupIds){
		attrGroupService.removeByIds(Arrays.asList(attrGroupIds));

        return R.ok();
    }

    /**
     * 更具分类Id获取所有分组记关联属性
     */
    @GetMapping("/{catelogId}/withattr")
    public R getGroupattrbyCatelogId(@PathVariable("catelogId") Long catelogId){
        List<AttrGroupWithAttrVo> AttrGroupWithAttrVo = attrGroupService.getAttrGroupWithattr(catelogId);
        return R.ok().put("data",AttrGroupWithAttrVo);
    }
}
