package com.cl.entity.view;

import com.cl.entity.TongzhijiluEntity;
import com.baomidou.mybatisplus.annotations.TableName;
import org.apache.commons.beanutils.BeanUtils;
import java.lang.reflect.InvocationTargetException;
import java.io.Serializable;


/**
 * 通知记录
 * 后端返回视图实体辅助类
 * （通常后端关联的表或者自定义的字段需要返回使用）
 */
@TableName("tongzhijilu")
public class TongzhijiluView extends TongzhijiluEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	public TongzhijiluView(){
	}

	public TongzhijiluView(TongzhijiluEntity tongzhijiluEntity){
		try {
			BeanUtils.copyProperties(this, tongzhijiluEntity);
		} catch (IllegalAccessException | InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 通知类型名称
	 */
	private String tongzhileixingmingcheng;
	
	/**
	 * 发送状态名称
	 */
	private String fasongzhuangtaimingcheng;
	
	/**
	 * 读取状态名称
	 */
	private String duquzhuangtaimingcheng;

	public String getTongzhileixingmingcheng() {
		if (this.getTongzhileixing() == null) {
			return "";
		}
		switch (this.getTongzhileixing()) {
			case 1: return "预约成功通知";
			case 2: return "就诊前24小时提醒";
			case 3: return "就诊前1小时提醒";
			case 4: return "就诊前15分钟提醒";
			default: return "未知类型";
		}
	}

	public String getFasongzhuangtaimingcheng() {
		if (this.getFasongzhuangtai() == null) {
			return "";
		}
		switch (this.getFasongzhuangtai()) {
			case 0: return "待发送";
			case 1: return "发送中";
			case 2: return "发送成功";
			case 3: return "发送失败";
			default: return "未知状态";
		}
	}

	public String getDuquzhuangtaimingcheng() {
		if (this.getDuquzhuangtai() == null) {
			return "";
		}
		return this.getDuquzhuangtai() == 1 ? "已读" : "未读";
	}
}
