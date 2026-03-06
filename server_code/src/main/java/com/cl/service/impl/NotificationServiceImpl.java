package com.cl.service.impl;

import com.cl.entity.MessagesEntity;
import com.cl.entity.TongzhijiluEntity;
import com.cl.entity.YishengyuyueEntity;
import com.cl.service.MessagesService;
import com.cl.service.NotificationService;
import com.cl.service.TongzhijiluService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 通知服务实现类
 */
@Service
public class NotificationServiceImpl implements NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationServiceImpl.class);

    @Autowired
    private TongzhijiluService tongzhijiluService;

    @Autowired
    private MessagesService messagesService;

    // 最大重试次数
    private static final int MAX_RETRY_COUNT = 3;

    // 通知类型常量
    private static final int TYPE_APPOINTMENT_SUCCESS = 1;
    private static final int TYPE_24H_REMINDER = 2;
    private static final int TYPE_1H_REMINDER = 3;
    private static final int TYPE_15MIN_REMINDER = 4;

    // 发送状态常量
    private static final int STATUS_PENDING = 0;
    private static final int STATUS_SENDING = 1;
    private static final int STATUS_SUCCESS = 2;
    private static final int STATUS_FAILED = 3;

    @Override
    @Transactional
    public void createNotifications(YishengyuyueEntity yuyue) {
        if (yuyue == null || yuyue.getYuyueshijian() == null) {
            logger.warn("预约信息为空或预约时间为空，无法创建通知");
            return;
        }

        Date yuyueTime = yuyue.getYuyueshijian();
        String yuyuebianhao = yuyue.getYuyuebianhao();
        String zhanghao = yuyue.getZhanghao();
        String shouji = yuyue.getShouji();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String timeStr = sdf.format(yuyueTime);

        // 1. 创建预约成功通知（立即发送）
        TongzhijiluEntity successNotification = new TongzhijiluEntity();
        successNotification.setYuyuebianhao(yuyuebianhao);
        successNotification.setTongzhileixing(TYPE_APPOINTMENT_SUCCESS);
        successNotification.setTongzhineirong(String.format("您的预约已成功！预约医生：%s，就诊时间：%s，请按时就诊。",
                yuyue.getYishengzhanghao(), timeStr));
        successNotification.setZhanghao(zhanghao);
        successNotification.setShouji(shouji);
        successNotification.setFasongzhuangtai(STATUS_PENDING);
        successNotification.setChongshicishu(0);
        successNotification.setJihuafasongshijian(new Date());
        successNotification.setDuquzhuangtai(0);
        tongzhijiluService.insert(successNotification);

        // 立即发送预约成功通知
        sendNotification(successNotification);

        // 2. 创建就诊前24小时提醒
        Calendar cal24h = Calendar.getInstance();
        cal24h.setTime(yuyueTime);
        cal24h.add(Calendar.HOUR, -24);
        if (cal24h.getTime().after(new Date())) {
            TongzhijiluEntity notification24h = new TongzhijiluEntity();
            notification24h.setYuyuebianhao(yuyuebianhao);
            notification24h.setTongzhileixing(TYPE_24H_REMINDER);
            notification24h.setTongzhineirong(String.format("【就诊提醒】您预约的就诊将在24小时后（%s）开始，请做好准备。",
                    timeStr));
            notification24h.setZhanghao(zhanghao);
            notification24h.setShouji(shouji);
            notification24h.setFasongzhuangtai(STATUS_PENDING);
            notification24h.setChongshicishu(0);
            notification24h.setJihuafasongshijian(cal24h.getTime());
            notification24h.setDuquzhuangtai(0);
            tongzhijiluService.insert(notification24h);
        }

        // 3. 创建就诊前1小时提醒
        Calendar cal1h = Calendar.getInstance();
        cal1h.setTime(yuyueTime);
        cal1h.add(Calendar.HOUR, -1);
        if (cal1h.getTime().after(new Date())) {
            TongzhijiluEntity notification1h = new TongzhijiluEntity();
            notification1h.setYuyuebianhao(yuyuebianhao);
            notification1h.setTongzhileixing(TYPE_1H_REMINDER);
            notification1h.setTongzhineirong(String.format("【就诊提醒】您预约的就诊将在1小时后（%s）开始，请尽快前往医院。",
                    timeStr));
            notification1h.setZhanghao(zhanghao);
            notification1h.setShouji(shouji);
            notification1h.setFasongzhuangtai(STATUS_PENDING);
            notification1h.setChongshicishu(0);
            notification1h.setJihuafasongshijian(cal1h.getTime());
            notification1h.setDuquzhuangtai(0);
            tongzhijiluService.insert(notification1h);
        }

        // 4. 创建就诊前15分钟提醒
        Calendar cal15min = Calendar.getInstance();
        cal15min.setTime(yuyueTime);
        cal15min.add(Calendar.MINUTE, -15);
        if (cal15min.getTime().after(new Date())) {
            TongzhijiluEntity notification15min = new TongzhijiluEntity();
            notification15min.setYuyuebianhao(yuyuebianhao);
            notification15min.setTongzhileixing(TYPE_15MIN_REMINDER);
            notification15min.setTongzhineirong(String.format("【就诊提醒】您预约的就诊将在15分钟后（%s）开始，请立即前往诊室。",
                    timeStr));
            notification15min.setZhanghao(zhanghao);
            notification15min.setShouji(shouji);
            notification15min.setFasongzhuangtai(STATUS_PENDING);
            notification15min.setChongshicishu(0);
            notification15min.setJihuafasongshijian(cal15min.getTime());
            notification15min.setDuquzhuangtai(0);
            tongzhijiluService.insert(notification15min);
        }

        logger.info("已为预约 {} 创建所有通知记录", yuyuebianhao);
    }

    @Override
    @Async
    public void sendAppointmentSuccessNotification(YishengyuyueEntity yuyue) {
        createNotifications(yuyue);
    }

    @Override
    public boolean sendNotification(TongzhijiluEntity notification) {
        if (notification == null) {
            return false;
        }

        // 更新状态为发送中
        notification.setFasongzhuangtai(STATUS_SENDING);
        tongzhijiluService.updateById(notification);

        boolean smsSuccess = false;
        boolean messageSuccess = false;

        try {
            // 发送短信通知
            if (notification.getShouji() != null && !notification.getShouji().isEmpty()) {
                smsSuccess = sendSms(notification.getShouji(), notification.getTongzhineirong());
            }

            // 发送站内信通知
            if (notification.getZhanghao() != null && !notification.getZhanghao().isEmpty()) {
                messageSuccess = sendMessage(notification.getZhanghao(), notification.getTongzhineirong());
            }

            // 更新发送结果
            if (smsSuccess || messageSuccess) {
                notification.setFasongzhuangtai(STATUS_SUCCESS);
                notification.setShijifasongshijian(new Date());
                notification.setShibaiyuanyin(null);
                logger.info("通知 {} 发送成功", notification.getId());
            } else {
                notification.setFasongzhuangtai(STATUS_FAILED);
                notification.setShibaiyuanyin("短信和站内信均发送失败");
                notification.setChongshicishu(notification.getChongshicishu() + 1);
                logger.warn("通知 {} 发送失败", notification.getId());
            }

            tongzhijiluService.updateById(notification);
            return notification.getFasongzhuangtai() == STATUS_SUCCESS;

        } catch (Exception e) {
            logger.error("发送通知时发生异常: {}", e.getMessage(), e);
            notification.setFasongzhuangtai(STATUS_FAILED);
            notification.setShibaiyuanyin(e.getMessage());
            notification.setChongshicishu(notification.getChongshicishu() + 1);
            tongzhijiluService.updateById(notification);
            return false;
        }
    }

    @Override
    public boolean sendSms(String phone, String content) {
        // 模拟短信发送
        // 实际项目中这里应该调用短信服务商的API
        logger.info("发送短信到 {}: {}", phone, content);

        // 模拟发送成功率90%
        boolean success = Math.random() > 0.1;
        if (!success) {
            logger.warn("短信发送失败（模拟）: {}", phone);
        }
        return success;
    }

    @Override
    public boolean sendMessage(String zhanghao, String content) {
        try {
            // 创建站内信
            MessagesEntity message = new MessagesEntity();
            message.setUserid(0L); // 系统消息
            message.setUsername("系统");
            message.setContent(content);
            messagesService.insert(message);

            logger.info("站内信发送成功: {}", zhanghao);
            return true;
        } catch (Exception e) {
            logger.error("站内信发送失败: {}", e.getMessage());
            return false;
        }
    }

    @Override
    @Scheduled(fixedRate = 60000) // 每分钟执行一次
    public void retryFailedNotifications() {
        logger.debug("开始重试发送失败的通知...");

        List<TongzhijiluEntity> failedList = tongzhijiluService.selectRetryList(MAX_RETRY_COUNT);

        if (failedList != null && !failedList.isEmpty()) {
            logger.info("发现 {} 条需要重试的通知", failedList.size());
            for (TongzhijiluEntity notification : failedList) {
                logger.info("重试发送通知: {}", notification.getId());
                sendNotification(notification);
            }
        }
    }

    @Override
    @Scheduled(fixedRate = 60000) // 每分钟执行一次
    public void sendScheduledNotifications() {
        logger.debug("开始检查待发送的通知...");

        // 查询计划发送时间已到且状态为待发送的通知
        List<TongzhijiluEntity> pendingList = tongzhijiluService.selectList(
                new com.baomidou.mybatisplus.mapper.EntityWrapper<TongzhijiluEntity>()
                        .eq("fasongzhuangtai", STATUS_PENDING)
                        .le("jihuafasongshijian", new Date())
        );

        if (pendingList != null && !pendingList.isEmpty()) {
            logger.info("发现 {} 条待发送的通知", pendingList.size());
            for (TongzhijiluEntity notification : pendingList) {
                logger.info("发送定时通知: {}", notification.getId());
                sendNotification(notification);
            }
        }
    }
}
