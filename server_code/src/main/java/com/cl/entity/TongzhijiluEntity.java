package com.cl.entity;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.lang.reflect.InvocationTargetException;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.beanutils.BeanUtils;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.enums.FieldFill;
import com.baomidou.mybatisplus.enums.IdType;

/**
 * 通知记录
 * 数据库通用操作实体类（普通增删改查）
 * @author 
 * @email 
 * @date 2025-03-27 15:44:15
 */
@TableName("tongzhijilu")
public class TongzhijiluEntity<T> implements Serializable {
	private static final long serialVersionUID = 1L;

	public TongzhijiluEntity() {
		
	}
	
	public TongzhijiluEntity(T t) {
		try {
			BeanUtils.copyProperties(this, t);
		} catch (IllegalAccessException | InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 主键id
	 */
	@TableId(type = IdType.AUTO)
	private Long id;
	
	/**
	 * 通知编号
	 */
	private String tongzhibianhao;
	
	/**
	 * 预约编号
	 */
	private String yuyuebianhao;
	
	/**
	 * 医生账号
	 */
	private String yishengzhanghao;
	
	/**
	 * 用户账号
	 */
	private String zhanghao;
	
	/**
	 * 通知类型
	 */
	private String tongzhitype;
	
	/**
	 * 接收渠道
	 */
	private String jieshouqudao;
	
	/**
	 * 接收地址
	 */
	private String jieshoudizhi;
	
	/**
	 * 发送状态
	 */
	private String fasongzhuangtai;
	
	/**
	 * 发送时间
	 */
	@JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat 		
	private Date fasongshijian;
	
	/**
	 * 重试次数
	 */
	private Integer chongshicishu;
	
	/**
	 * 失败原因
	 */
	private String shibaiyuanyin;
	
	/**
	 * 处理状态
	 */
	private String chulizhuangtai;
	
	/**
	 * 处理时间
	 */
	@JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat 		
	private Date chulishijian;
	
	/**
	 * 处理人
	 */
	private String chuliren;
	
	/**
	 * 备注信息
	 */
	private String beizhixinxi;

	@JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat
	private Date addtime;

	public Date getAddtime() {
		return addtime;
	}
	public void setAddtime(Date addtime) {
		this.addtime = addtime;
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	/**
	 * 设置：通知编号
	 */
	public void setTongzhibianhao(String tongzhibianhao) {
		this.tongzhibianhao = tongzhibianhao;
	}
	/**
	 * 获取：通知编号
	 */
	public String getTongzhibianhao() {
		return tongzhibianhao;
	}
	
	/**
	 * 设置：预约编号
	 */
	public void setYuyuebianhao(String yuyuebianhao) {
		this.yuyuebianhao = yuyuebianhao;
	}
	/**
	 * 获取：预约编号
	 */
	public String getYuyuebianhao() {
		return yuyuebianhao;
	}
	
	/**
	 * 设置：医生账号
	 */
	public void setYishengzhanghao(String yishengzhanghao) {
		this.yishengzhanghao = yishengzhanghao;
	}
	/**
	 * 获取：医生账号
	 */
	public String getYishengzhanghao() {
		return yishengzhanghao;
	}
	
	/**
	 * 设置：用户账号
	 */
	public void setZhanghao(String zhanghao) {
		this.zhanghao = zhanghao;
	}
	/**
	 * 获取：用户账号
	 */
	public String getZhanghao() {
		return zhanghao;
	}
	
	/**
	 * 设置：通知类型
	 */
	public void setTongzhitype(String tongzhitype) {
		this.tongzhitype = tongzhitype;
	}
	/**
	 * 获取：通知类型
	 */
	public String getTongzhitype() {
		return tongzhitype;
	}
	
	/**
	 * 设置：接收渠道
	 */
	public void setJieshouqudao(String jieshouqudao) {
		this.jieshouqudao = jieshouqudao;
	}
	/**
	 * 获取：接收渠道
	 */
	public String getJieshouqudao() {
		return jieshouqudao;
	}
	
	/**
	 * 设置：接收地址
	 */
	public void setJieshoudizhi(String jieshoudizhi) {
		this.jieshoudizhi = jieshoudizhi;
	}
	/**
	 * 获取：接收地址
	 */
	public String getJieshoudizhi() {
		return jieshoudizhi;
	}
	
	/**
	 * 设置：发送状态
	 */
	public void setFasongzhuangtai(String fasongzhuangtai) {
		this.fasongzhuangtai = fasongzhuangtai;
	}
	/**
	 * 获取：发送状态
	 */
	public String getFasongzhuangtai() {
		return fasongzhuangtai;
	}
	
	/**
	 * 设置：发送时间
	 */
	public void setFasongshijian(Date fasongshijian) {
		this.fasongshijian = fasongshijian;
	}
	/**
	 * 获取：发送时间
	 */
	public Date getFasongshijian() {
		return fasongshijian;
	}
	
	/**
	 * 设置：重试次数
	 */
	public void setChongshicishu(Integer chongshicishu) {
		this.chongshicishu = chongshicishu;
	}
	/**
	 * 获取：重试次数
	 */
	public Integer getChongshicishu() {
		return chongshicishu;
	}
	
	/**
	 * 设置：失败原因
	 */
	public void setShibaiyuanyin(String shibaiyuanyin) {
		this.shibaiyuanyin = shibaiyuanyin;
	}
	/**
	 * 获取：失败原因
	 */
	public String getShibaiyuanyin() {
		return shibaiyuanyin;
	}
	
	/**
	 * 设置：处理状态
	 */
	public void setChulizhuangtai(String chulizhuangtai) {
		this.chulizhuangtai = chulizhuangtai;
	}
	/**
	 * 获取：处理状态
	 */
	public String getChulizhuangtai() {
		return chulizhuangtai;
	}
	
	/**
	 * 设置：处理时间
	 */
	public void setChulishijian(Date chulishijian) {
		this.chulishijian = chulishijian;
	}
	/**
	 * 获取：处理时间
	 */
	public Date getChulishijian() {
		return chulishijian;
	}
	
	/**
	 * 设置：处理人
	 */
	public void setChuliren(String chuliren) {
		this.chuliren = chuliren;
	}
	/**
	 * 获取：处理人
	 */
	public String getChuliren() {
		return chuliren;
	}
	
	/**
	 * 设置：备注信息
	 */
	public void setBeizhixinxi(String beizhixinxi) {
		this.beizhixinxi = beizhixinxi;
	}
	/**
	 * 获取：备注信息
	 */
	public String getBeizhixinxi() {
		return beizhixinxi;
	}
}