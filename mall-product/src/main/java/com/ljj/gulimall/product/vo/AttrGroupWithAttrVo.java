package com.ljj.gulimall.product.vo;

import com.ljj.gulimall.product.entity.AttrEntity;
import com.ljj.gulimall.product.entity.AttrGroupEntity;
import lombok.Data;

import java.util.List;

public class AttrGroupWithAttrVo extends AttrGroupEntity {
    private List<AttrEntity> attrs;


    public List<AttrEntity> getAttrs() {
        return attrs;
    }

    public void setAttrs(List<AttrEntity> attrs) {
        this.attrs = attrs;
    }
}
