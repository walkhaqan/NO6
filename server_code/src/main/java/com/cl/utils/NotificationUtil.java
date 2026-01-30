package com.cl.utils;

import com.cl.entity.YonghuEntity;
import com.cl.entity.TongzhijiluEntity;
import com.cl.service.YonghuService;
import com.cl.service.TongzhijiluService;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * 通知工具类
 * @author 
 */
@Component
public class NotificationUtil {

    @Autowired
    private YonghuService yonghuService;
    
    @Autowired
    private TongzhijiluService tongzhijiluService;
    
    /**
     * 检查用户通知渠道状态
     * @param userAccount 用户账号
     * @param channel 通知渠道（短信、邮件等）
     * @return 是否可用
     */
    public boolean checkNotificationChannelStatus(String userAccount, String channel) {
        try {
            // 获取用户信息
            YonghuEntity user = yonghuService.selectOne(
                new EntityWrapper<YonghuEntity>().eq("zhanghao", userAccount)
            );
            
            if (user == null) {
                return false;
            }
            
            // 检查用户是否开启了通知
            if (!"开启".equals(user.getTongzhizhuangtai())) {
                return false;
            }
            
            // 检查用户通知设置是否包含该渠道
            String notificationSettings = user.getTongzhishezhi();
            if (notificationSettings != null && !notificationSettings.isEmpty()) {
                if (!notificationSettings.contains(channel)) {
                    return false;
                }
            }
            
            // 检查渠道对应的地址是否有效
            if ("短信".equals(channel)) {
                return user.getShouji() != null && !user.getShouji().isEmpty();
            } else if ("邮件".equals(channel)) {
                return user.getYouxiang() != null && !user.getYouxiang().isEmpty();
            }
            
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * 获取用户的通知接收地址
     * @param userAccount 用户账号
     * @param channel 通知渠道
     * @return 接收地址
     */
    public String getNotificationAddress(String userAccount, String channel) {
        try {
            YonghuEntity user = yonghuService.selectOne(
                new EntityWrapper<YonghuEntity>().eq("zhanghao", userAccount)
            );
            
            if (user == null) {
                return null;
            }
            
            if ("短信".equals(channel)) {
                return user.getShouji();
            } else if ("邮件".equals(channel)) {
                return user.getYouxiang();
            }
            
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * 发送短信通知
     * @param phone 手机号
     * @param content 短信内容
     * @return 是否发送成功
     */
    public boolean sendSMS(String phone, String content) {
        try {
            // 这里应该调用实际的短信服务API
            // 模拟短信发送
            System.out.println("发送短信到 " + phone + ": " + content);
            
            // 模拟90%的成功率
            return Math.random() > 0.1;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * 发送邮件通知
     * @param email 邮箱地址
     * @param subject 邮件主题
     * @param content 邮件内容
     * @return 是否发送成功
     */
    public boolean sendEmail(String email, String subject, String content) {
        try {
            // 这里应该调用实际的邮件服务API
            // 模拟邮件发送
            System.out.println("发送邮件到 " + email + ", 主题: " + subject + ", 内容: " + content);
            
            // 模拟85%的成功率
            return Math.random() > 0.15;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * 创建通知记录
     * @param notificationNumber 通知编号
     * @param appointmentNumber 预约编号
     * @param doctorAccount 医生账号
     * @param userAccount 用户账号
     * @param notificationType 通知类型
     * @param channel 接收渠道
     * @param address 接收地址
     * @return 通知记录ID
     */
    public Long createNotificationRecord(String notificationNumber, String appointmentNumber, 
                                        String doctorAccount, String userAccount, 
                                        String notificationType, String channel, String address) {
        try {
            TongzhijiluEntity notification = new TongzhijiluEntity();
            notification.setTongzhibianhao(notificationNumber);
            notification.setYuyuebianhao(appointmentNumber);
            notification.setYishengzhanghao(doctorAccount);
            notification.setZhanghao(userAccount);
            notification.setTongzhitype(notificationType);
            notification.setJieshouqudao(channel);
            notification.setJieshoudizhi(address);
            notification.setFasongzhuangtai("待发送");
            notification.setChongshicishu(0);
            notification.setChulizhuangtai("未处理");
            
            tongzhijiluService.insert(notification);
            return notification.getId();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * 更新通知发送状态
     * @param notificationId 通知ID
     * @param status 发送状态
     * @param failureReason 失败原因
     * @return 是否更新成功
     */
    public boolean updateNotificationStatus(Long notificationId, String status, String failureReason) {
        try {
            TongzhijiluEntity notification = tongzhijiluService.selectById(notificationId);
            if (notification == null) {
                return false;
            }
            
            notification.setFasongzhuangtai(status);
            notification.setFasongshijian(new Date());
            
            if ("发送失败".equals(status)) {
                notification.setChongshicishu(notification.getChongshicishu() + 1);
                notification.setShibaiyuanyin(failureReason);
            }
            
            tongzhijiluService.updateById(notification);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}