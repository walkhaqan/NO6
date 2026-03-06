package com.cl.service;

import com.cl.entity.TongzhijiluEntity;
import com.cl.entity.YishengyuyueEntity;
import java.util.List;

/**
 * 通知服务接口
 * 用于处理就诊通知的发送逻辑
 */
public interface NotificationService {

	/**
	 * 预约成功后创建所有后续通知记录
	 * 包括：预约成功通知、就诊前24小时提醒、就诊前1小时提醒、就诊前15分钟提醒
	 * 
	 * @param yuyue 预约信息
	 */
	void createNotifications(YishengyuyueEntity yuyue);
	
	/**
	 * 立即发送预约成功通知
	 * 
	 * @param yuyue 预约信息
	 */
	void sendAppointmentSuccessNotification(YishengyuyueEntity yuyue);
	
	/**
	 * 发送通知
	 * 
	 * @param notification 通知记录
	 * @return 是否发送成功
	 */
	boolean sendNotification(TongzhijiluEntity notification);
	
	/**
	 * 发送短信通知（模拟）
	 * 
	 * @param phone 手机号
	 * @param content 短信内容
	 * @return 是否发送成功
	 */
	boolean sendSms(String phone, String content);
	
	/**
	 * 发送站内信通知
	 * 
	 * @param zhanghao 用户账号
	 * @param content 消息内容
	 * @return 是否发送成功
	 */
	boolean sendMessage(String zhanghao, String content);
	
	/**
	 * 重试发送失败的通知
	 */
	void retryFailedNotifications();
	
	/**
	 * 定时发送待发送的通知
	 */
	void sendScheduledNotifications();
}
