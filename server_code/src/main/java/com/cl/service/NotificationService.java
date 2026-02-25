package com.cl.service;

import com.cl.entity.JiuzhentongzhiEntity;
import com.cl.entity.TongzhirizhiEntity;
import com.cl.entity.YonghuEntity;
import com.cl.entity.YishengyuyueEntity;
import com.cl.service.YonghuService;
import com.cl.service.JiuzhentongzhiService;
import com.cl.service.TongzhirizhiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import java.util.List;

@Service
public class NotificationService {

    @Autowired
    private YonghuService yonghuService;
    
    @Autowired
    private JiuzhentongzhiService jiuzhentongzhiService;
    
    @Autowired
    private TongzhirizhiService tongzhirizhiService;

    private static final int MAX_RETRY_COUNT = 3;
    
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    public void sendNotification(YishengyuyueEntity yuyue) {
        String zhanghao = yuyue.getZhanghao();
        
        YonghuEntity yonghu = yonghuService.selectOne(
            new com.baomidou.mybatisplus.mapper.EntityWrapper<YonghuEntity>()
                .eq("zhanghao", zhanghao)
        );
        
        if (yonghu == null) {
            return;
        }
        
        String bianhao = "TZ" + System.currentTimeMillis();
        
        JiuzhentongzhiEntity tongzhi = new JiuzhentongzhiEntity();
        tongzhi.setTongzhibianhao(bianhao);
        tongzhi.setYishengzhanghao(yuyue.getYishengzhanghao());
        tongzhi.setDianhua(yuyue.getDianhua());
        tongzhi.setJiuzhenshijian(yuyue.getYuyueshijian());
        tongzhi.setTongzhishijian(new Date());
        tongzhi.setZhanghao(yuyue.getZhanghao());
        tongzhi.setShouji(yuyue.getShouji());
        tongzhi.setStatus(0);
        tongzhi.setRetryCount(0);
        tongzhi.setAddtime(new Date());
        
        StringBuilder content = new StringBuilder();
        content.append("就诊预约提醒：");
        content.append("尊敬的").append(yonghu.getXingming()).append("，");
        content.append("您预约的").append(yuyue.getYishengzhanghao()).append("医生的就诊时间为：");
        content.append(sdf.format(yuyue.getYuyueshijian())).append("，");
        content.append("请准时前往就诊。");
        tongzhi.setTongzhibeizhu(content.toString());
        
        ChannelStatus channelStatus = getUserNotificationChannelStatus(yonghu);
        
        if (!channelStatus.canSend()) {
            tongzhi.setStatus(2);
            tongzhi.setFailReason(channelStatus.getStatusDescription());
            jiuzhentongzhiService.insert(tongzhi);
            return;
        }
        
        tongzhi.setSendChannel(channelStatus.getEffectiveChannel());
        
        boolean allSuccess = true;
        StringBuilder failReasons = new StringBuilder();
        
        if (channelStatus.isSmsAvailable()) {
            boolean smsResult = sendSms(yonghu.getShouji(), content.toString());
            createLog(tongzhi, "SMS", yonghu.getShouji(), null, content.toString(), smsResult, 
                     smsResult ? null : "短信发送失败", 0);
            if (!smsResult) {
                allSuccess = false;
                failReasons.append("短信发送失败;");
            }
        }
        
        if (channelStatus.isEmailAvailable()) {
            boolean emailResult = sendEmail(yonghu.getYouxiang(), "就诊预约通知", content.toString());
            createLog(tongzhi, "EMAIL", null, yonghu.getYouxiang(), content.toString(), emailResult,
                     emailResult ? null : "邮件发送失败", 0);
            if (!emailResult) {
                allSuccess = false;
                failReasons.append("邮件发送失败;");
            }
        }
        
        if (allSuccess) {
            tongzhi.setStatus(1);
        } else {
            tongzhi.setStatus(2);
            tongzhi.setRetryCount(1);
            tongzhi.setFailReason(failReasons.toString());
        }
        
        jiuzhentongzhiService.insert(tongzhi);
    }
    
    private boolean sendSms(String phone, String content) {
        System.out.println("[模拟短信发送] 发送至：" + phone + "，内容：" + content);
        return true;
    }
    
    private boolean sendEmail(String email, String subject, String content) {
        System.out.println("[模拟邮件发送] 发送至：" + email + "，主题：" + subject + "，内容：" + content);
        return true;
    }
    
    private void createLog(JiuzhentongzhiEntity tongzhi, String channel, String phone, String email, 
                          String content, boolean success, String failReason, int retryCount) {
        TongzhirizhiEntity log = new TongzhirizhiEntity();
        log.setTongzhiId(tongzhi.getId());
        log.setTongzhibianhao(tongzhi.getTongzhibianhao());
        log.setZhanghao(tongzhi.getZhanghao());
        log.setShouji(phone);
        log.setYouxiang(email);
        log.setSendChannel(channel);
        log.setContent(content);
        log.setStatus(success ? 1 : 2);
        log.setFailReason(failReason);
        log.setRetryCount(retryCount);
        log.setHandleStatus(0);
        log.setAddtime(new Date());
        if (success) {
            log.setSendTime(new Date());
        }
        tongzhirizhiService.insert(log);
    }
    
    public void retryFailedNotifications() {
        List<TongzhirizhiEntity> failedList = tongzhirizhiService.selectFailedList(MAX_RETRY_COUNT);
        
        for (TongzhirizhiEntity log : failedList) {
            boolean result = false;
            if ("SMS".equals(log.getSendChannel())) {
                result = sendSms(log.getShouji(), log.getContent());
            } else if ("EMAIL".equals(log.getSendChannel())) {
                result = sendEmail(log.getYouxiang(), "就诊预约通知", log.getContent());
            }
            
            int newRetryCount = log.getRetryCount() + 1;
            if (result) {
                log.setStatus(1);
                log.setSendTime(new Date());
                log.setFailReason(null);
            } else {
                log.setFailReason("第" + newRetryCount + "次重试失败");
                if (newRetryCount >= MAX_RETRY_COUNT) {
                    log.setFailReason(log.getFailReason() + "，已达最大重试次数");
                }
            }
            log.setRetryCount(newRetryCount);
            
            tongzhirizhiService.updateById(log);
        }
    }
    
    public ChannelStatus getUserNotificationChannelStatus(YonghuEntity yonghu) {
        ChannelStatus status = new ChannelStatus();
        
        boolean smsEnabled = yonghu.getDuanxinTongzhi() != null && yonghu.getDuanxinTongzhi() == 1;
        boolean emailEnabled = yonghu.getYoujianTongzhi() != null && yonghu.getYoujianTongzhi() == 1;
        boolean hasPhone = yonghu.getShouji() != null && !yonghu.getShouji().isEmpty();
        boolean hasEmail = yonghu.getYouxiang() != null && !yonghu.getYouxiang().isEmpty();
        
        status.setSmsEnabled(smsEnabled);
        status.setEmailEnabled(emailEnabled);
        status.setHasPhone(hasPhone);
        status.setHasEmail(hasEmail);
        
        status.setSmsAvailable(smsEnabled && hasPhone);
        status.setEmailAvailable(emailEnabled && hasEmail);
        
        if (status.isSmsAvailable() && status.isEmailAvailable()) {
            status.setEffectiveChannel("ALL");
        } else if (status.isSmsAvailable()) {
            status.setEffectiveChannel("SMS");
        } else if (status.isEmailAvailable()) {
            status.setEffectiveChannel("EMAIL");
        } else {
            status.setEffectiveChannel("NONE");
        }
        
        return status;
    }
    
    public static class ChannelStatus {
        private boolean smsEnabled;
        private boolean emailEnabled;
        private boolean hasPhone;
        private boolean hasEmail;
        private boolean smsAvailable;
        private boolean emailAvailable;
        private String effectiveChannel;
        
        public boolean isSmsEnabled() {
            return smsEnabled;
        }
        
        public void setSmsEnabled(boolean smsEnabled) {
            this.smsEnabled = smsEnabled;
        }
        
        public boolean isEmailEnabled() {
            return emailEnabled;
        }
        
        public void setEmailEnabled(boolean emailEnabled) {
            this.emailEnabled = emailEnabled;
        }
        
        public boolean isHasPhone() {
            return hasPhone;
        }
        
        public void setHasPhone(boolean hasPhone) {
            this.hasPhone = hasPhone;
        }
        
        public boolean isHasEmail() {
            return hasEmail;
        }
        
        public void setHasEmail(boolean hasEmail) {
            this.hasEmail = hasEmail;
        }
        
        public boolean isSmsAvailable() {
            return smsAvailable;
        }
        
        public void setSmsAvailable(boolean smsAvailable) {
            this.smsAvailable = smsAvailable;
        }
        
        public boolean isEmailAvailable() {
            return emailAvailable;
        }
        
        public void setEmailAvailable(boolean emailAvailable) {
            this.emailAvailable = emailAvailable;
        }
        
        public String getEffectiveChannel() {
            return effectiveChannel;
        }
        
        public void setEffectiveChannel(String effectiveChannel) {
            this.effectiveChannel = effectiveChannel;
        }
        
        public boolean canSend() {
            return smsAvailable || emailAvailable;
        }
        
        public String getStatusDescription() {
            StringBuilder sb = new StringBuilder();
            if (!smsEnabled && !emailEnabled) {
                sb.append("用户未启用任何通知渠道");
            } else {
                if (smsEnabled && !hasPhone) {
                    sb.append("短信已启用但未配置手机号;");
                }
                if (emailEnabled && !hasEmail) {
                    sb.append("邮件已启用但未配置邮箱;");
                }
                if (sb.length() == 0) {
                    sb.append("无法发送通知");
                }
            }
            return sb.toString();
        }
    }
}
