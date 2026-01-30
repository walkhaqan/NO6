package com.cl.entity.view;

import com.cl.entity.TongzhijiluEntity;

/**
 * 通知记录
 * 后端返回视图实体辅助类 
 * 通常用于返回前端数据需要添加额外的字段或信息
 * @author 
 * @email 
 * @date 2025-03-27 15:44:15
 */
public class TongzhijiluView extends TongzhijiluEntity {
	private static final long serialVersionUID = 1L;

	public TongzhijiluView() {
	}
		
	public TongzhijiluView(TongzhijiluEntity entity) {
		if(entity!=null){
			this.setId(entity.getId());
			this.setTongzhibianhao(entity.getTongzhibianhao());
			this.setYuyuebianhao(entity.getYuyuebianhao());
			this.setYishengzhanghao(entity.getYishengzhanghao());
			this.setZhanghao(entity.getZhanghao());
			this.setTongzhitype(entity.getTongzhitype());
			this.setJieshouqudao(entity.getJieshouqudao());
			this.setJieshoudizhi(entity.getJieshoudizhi());
			this.setFasongzhuangtai(entity.getFasongzhuangtai());
			this.setFasongshijian(entity.getFasongshijian());
			this.setChongshicishu(entity.getChongshicishu());
			this.setShibaiyuanyin(entity.getShibaiyuanyin());
			this.setChulizhuangtai(entity.getChulizhuangtai());
			this.setChulishijian(entity.getChulishijian());
			this.setChuliren(entity.getChuliren());
			this.setBeizhixinxi(entity.getBeizhixinxi());
			this.setAddtime(entity.getAddtime());
		}
	}
}