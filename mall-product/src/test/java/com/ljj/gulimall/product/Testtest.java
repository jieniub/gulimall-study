package com.ljj.gulimall.product;


import com.ljj.gulimall.product.entity.BrandEntity;
import com.ljj.gulimall.product.service.BrandService;
import com.ljj.gulimall.product.service.CategoryService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


@SpringBootTest
@RunWith(SpringRunner.class)
public class Testtest {

    @Autowired
    BrandService brandService;

    @Autowired
    CategoryService categoryService;

    @Test
    public void testfindParent(){
        Long[] catelogPath = categoryService.getCatelogPath(225L);
        for (Long i:catelogPath){
            System.out.println(i);
        }
    }

    @Test
    public void contextLoads(){
        BrandEntity brandEntity = new BrandEntity();
//        brandEntity.setName("华为");
//        brandService.save(brandEntity);
//        System.out.println("保存成功");
        brandEntity.setBrandId(1L);
        brandEntity.setDescript("华为");
        brandService.updateById(brandEntity);
    }
}
