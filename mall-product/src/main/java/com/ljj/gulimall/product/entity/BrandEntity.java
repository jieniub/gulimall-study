package com.ljj.gulimall.product.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;

import com.ljj.common.valid.AddGroup;
import com.ljj.common.valid.ListValue;
import com.ljj.common.valid.UpdateGroup;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.*;
import javax.ws.rs.FormParam;

/**
 * 品牌
 * 
 * @author ljj
 * @email 1376058561@qq.com
 * @date 2022-10-16 13:26:53
 */
@Data
@TableName("pms_brand")
public class BrandEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 品牌id
	 */

	@Null(message = "新增brandId应为空",groups = AddGroup.class)
	@NotNull(message = "修改brandId不能为空",groups = UpdateGroup.class)
	@TableId
	private Long brandId;
	/**
	 * 品牌名
	 */
	@NotBlank(message="品牌名不能为空",groups = AddGroup.class)
	@NotBlank(message="品牌名不能为空",groups = UpdateGroup.class)
	private String name;
	/**
	 * 品牌logo地址
	 */
	@NotBlank(groups = AddGroup.class)
	@URL(message = "logo必须为一个合法url",groups = AddGroup.class)
	@URL(message = "logo必须为一个合法url",groups = UpdateGroup.class)
	private String logo;
	/**
	 * 介绍
	 */
	private String descript;
	/**
	 * 显示状态[0-不显示；1-显示]
	 */

	@ListValue(vals = {1,0},groups = {AddGroup.class,UpdateGroup.class})
	private Integer showStatus;
	/**
	 * 检索首字母
	 */

	@Pattern(regexp = "^[a-zA-Z]$",message="检索首字母必须是一个字母",groups = {UpdateGroup.class,AddGroup.class})
	private String firstLetter;
	/**
	 * 排序
	 */
	@NotNull(message = "排序不能为空",groups = AddGroup.class)
	@Min(value = 0, message = "排序必须为大于等于零的整数",groups = {AddGroup.class,UpdateGroup.class})
	private Integer sort;

}
