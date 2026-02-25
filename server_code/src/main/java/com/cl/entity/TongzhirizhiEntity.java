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
 * 通知日志
 * 数据库通用操作实体类（普通增删改查）
 * @author 
 * @email 
 * @date 2025-03-27 15:44:15
 */
@TableName("tongzhirizhi")
public class TongzhirizhiEntity<T> implements Serializable {
	private static final long serialVersionUID = 1L;


	public TongzhirizhiEntity() {
		
	}
	
	public TongzhirizhiEntity(T t) {
		try {
			BeanUtils.copyProperties(this, t);
		} catch (IllegalAccessException | InvocationTargetException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 主键id
	 */
	@TableId(type = IdType.AUTO)
	private Long id;
	/**
	 * 通知ID
	 */
					
	private Long tongzhiId;
	
	/**
	 * 通知编号
	 */
					
	private String tongzhibianhao;
	
	/**
	 * 接收账号
	 */
					
	private String zhanghao;
	
	/**
	 * 接收手机
	 */
					
	private String shouji;
	
	/**
	 * 接收邮箱
	 */
					
	private String youxiang;
	
	/**
	 * 发送渠道：SMS短信 EMAIL邮件
	 */
					
	private String sendChannel;
	
	/**
	 * 发送内容
	 */
					
	private String content;
	
	/**
	 * 发送状态：0待发送 1已发送 2发送失败
	 */
					
	private Integer status;
	
	/**
	 * 失败原因
	 */
					
	private String failReason;
	
	/**
	 * 重试次数
	 */
					
	private Integer retryCount;
	
	/**
	 * 处理状态：0未处理 1已处理
	 */
					
	private Integer handleStatus;
	
	/**
	 * 处理备注
	 */
					
	private String handleBeizhu;
	

	@JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat 		
	private Date addtime;
	
	/**
	 * 发送时间
	 */
				
	@JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat 		
	private Date sendTime;
	
	/**
	 * 处理时间
	 */
				
	@JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat 		
	private Date handleTime;

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
	public void setTongzhiId(Long tongzhiId) {
		this.tongzhiId = tongzhiId;
	}
	public Long getTongzhiId() {
		return tongzhiId;
	}
	public void setTongzhibianhao(String tongzhibianhao) {
		this.tongzhibianhao = tongzhibianhao;
	}
	public String getTongzhibianhao() {
		return tongzhibianhao;
	}
	public void setZhanghao(String zhanghao) {
		this.zhanghao = zhanghao;
	}
	public String getZhanghao() {
		return zhanghao;
	}
	public void setShouji(String shouji) {
		this.shouji = shouji;
	}
	public String getShouji() {
		return shouji;
	}
	public void setYouxiang(String youxiang) {
		this.youxiang = youxiang;
	}
	public String getYouxiang() {
		return youxiang;
	}
	public void setSendChannel(String sendChannel) {
		this.sendChannel = sendChannel;
	}
	public String getSendChannel() {
		return sendChannel;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getContent() {
		return content;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public Integer getStatus() {
		return status;
	}
	public void setFailReason(String failReason) {
		this.failReason = failReason;
	}
	public String getFailReason() {
		return failReason;
	}
	public void setRetryCount(Integer retryCount) {
		this.retryCount = retryCount;
	}
	public Integer getRetryCount() {
		return retryCount;
	}
	public void setHandleStatus(Integer handleStatus) {
		this.handleStatus = handleStatus;
	}
	public Integer getHandleStatus() {
		return handleStatus;
	}
	public void setHandleBeizhu(String handleBeizhu) {
		this.handleBeizhu = handleBeizhu;
	}
	public String getHandleBeizhu() {
		return handleBeizhu;
	}
	public void setSendTime(Date sendTime) {
		this.sendTime = sendTime;
	}
	public Date getSendTime() {
		return sendTime;
	}
	public void setHandleTime(Date handleTime) {
		this.handleTime = handleTime;
	}
	public Date getHandleTime() {
		return handleTime;
	}

}
