package com.cl.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.Map;
import java.util.List;
import java.util.Date;
import java.util.UUID;
import java.text.SimpleDateFormat;

import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.cl.utils.PageUtils;
import com.cl.utils.Query;

import com.cl.dao.TongzhijiluDao;
import com.cl.entity.TongzhijiluEntity;
import com.cl.service.TongzhijiluService;
import com.cl.entity.view.TongzhijiluView;

/**
 * 通知记录
 * 服务实现类
 * @author 
 * @email 
 * @date 2025-03-27 15:44:15
 */
@Service("tongzhijiluService")
public class TongzhijiluServiceImpl extends ServiceImpl<TongzhijiluDao, TongzhijiluEntity> implements TongzhijiluService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        Page<TongzhijiluEntity> page = this.selectPage(
                new Query<TongzhijiluEntity>(params).getPage(),
                new EntityWrapper<TongzhijiluEntity>()
        );
        return new PageUtils(page);
    }
    
    @Override
	public PageUtils queryPage(Map<String, Object> params, Wrapper<TongzhijiluEntity> wrapper) {
		  Page<TongzhijiluView> page =new Query<TongzhijiluView>(params).getPage();
	        page.setRecords(baseMapper.selectListView(page,wrapper));
	    	PageUtils pageUtil = new PageUtils(page);
	    	return pageUtil;
 	}
    
	@Override
	public List<TongzhijiluView> selectListView(Wrapper<TongzhijiluEntity> wrapper) {
		return baseMapper.selectListView(wrapper);
	}

	@Override
	public TongzhijiluView selectView(Wrapper<TongzhijiluEntity> wrapper) {
		return baseMapper.selectView(wrapper);
	}
	
	@Override
	@Transactional
	public boolean sendNotification(TongzhijiluEntity tongzhijilu) {
		try {
			// 设置发送状态为发送中
			tongzhijilu.setFasongzhuangtai("发送中");
			tongzhijilu.setFasongshijian(new Date());
			this.updateById(tongzhijilu);
			
			// 模拟发送通知
			boolean sendResult = false;
			if ("短信".equals(tongzhijilu.getJieshouqudao())) {
				// 模拟短信发送
				sendResult = sendSMS(tongzhijilu.getJieshoudizhi(), generateNotificationContent(tongzhijilu));
			} else if ("邮件".equals(tongzhijilu.getJieshouqudao())) {
				// 模拟邮件发送
				sendResult = sendEmail(tongzhijilu.getJieshoudizhi(), generateNotificationContent(tongzhijilu));
			}
			
			// 更新发送结果
			if (sendResult) {
				tongzhijilu.setFasongzhuangtai("发送成功");
			} else {
				tongzhijilu.setFasongzhuangtai("发送失败");
				tongzhijilu.setShibaiyuanyin("网络连接超时");
				tongzhijilu.setChongshicishu(tongzhijilu.getChongshicishu() == null ? 1 : tongzhijilu.getChongshicishu() + 1);
			}
			
			this.updateById(tongzhijilu);
			return sendResult;
		} catch (Exception e) {
			tongzhijilu.setFasongzhuangtai("发送失败");
			tongzhijilu.setShibaiyuanyin(e.getMessage());
			tongzhijilu.setChongshicishu(tongzhijilu.getChongshicishu() == null ? 1 : tongzhijilu.getChongshicishu() + 1);
			this.updateById(tongzhijilu);
			return false;
		}
	}
	
	@Override
	@Transactional
	public boolean retryNotification(Long id) {
		TongzhijiluEntity tongzhijilu = this.selectById(id);
		if (tongzhijilu == null || !"发送失败".equals(tongzhijilu.getFasongzhuangtai())) {
			return false;
		}
		
		// 检查重试次数是否超过限制
		if (tongzhijilu.getChongshicishu() != null && tongzhijilu.getChongshicishu() >= 3) {
			tongzhijilu.setChulizhuangtai("已放弃");
			tongzhijilu.setChulishijian(new Date());
			this.updateById(tongzhijilu);
			return false;
		}
		
		return sendNotification(tongzhijilu);
	}
	
	@Override
	@Transactional
	public boolean handleFailedNotification(Long id, String chuliren, String chulizhuangtai, String beizhixinxi) {
		TongzhijiluEntity tongzhijilu = this.selectById(id);
		if (tongzhijilu == null) {
			return false;
		}
		
		tongzhijilu.setChulizhuangtai(chulizhuangtai);
		tongzhijilu.setChulishijian(new Date());
		tongzhijilu.setChuliren(chuliren);
		tongzhijilu.setBeizhixinxi(beizhixinxi);
		
		return this.updateById(tongzhijilu);
	}
	
	@Override
	@Transactional
	public boolean createNotificationsForAppointment(String yuyuebianhao, String yishengzhanghao, String zhanghao, String shouji, Date yuyueshijian) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			
			// 创建预约成功通知
			TongzhijiluEntity successNotification = new TongzhijiluEntity();
			successNotification.setTongzhibianhao(generateNotificationId());
			successNotification.setYuyuebianhao(yuyuebianhao);
			successNotification.setYishengzhanghao(yishengzhanghao);
			successNotification.setZhanghao(zhanghao);
			successNotification.setTongzhitype("预约成功通知");
			successNotification.setJieshouqudao("短信");
			successNotification.setJieshoudizhi(shouji);
			successNotification.setFasongzhuangtai("待发送");
			successNotification.setChongshicishu(0);
			successNotification.setAddtime(new Date());
			this.insert(successNotification);
			
			// 立即发送预约成功通知
			sendNotification(successNotification);
			
			// 创建就诊前一天提醒通知
			TongzhijiluEntity dayBeforeNotification = new TongzhijiluEntity();
			dayBeforeNotification.setTongzhibianhao(generateNotificationId());
			dayBeforeNotification.setYuyuebianhao(yuyuebianhao);
			dayBeforeNotification.setYishengzhanghao(yishengzhanghao);
			dayBeforeNotification.setZhanghao(zhanghao);
			dayBeforeNotification.setTongzhitype("就诊前一天提醒");
			dayBeforeNotification.setJieshouqudao("短信");
			dayBeforeNotification.setJieshoudizhi(shouji);
			dayBeforeNotification.setFasongzhuangtai("待发送");
			dayBeforeNotification.setChongshicishu(0);
			dayBeforeNotification.setAddtime(new Date());
			this.insert(dayBeforeNotification);
			
			// 立即发送就诊前一天提醒通知
			sendNotification(dayBeforeNotification);
			
			// 创建就诊当天提醒通知
			TongzhijiluEntity dayOfNotification = new TongzhijiluEntity();
			dayOfNotification.setTongzhibianhao(generateNotificationId());
			dayOfNotification.setYuyuebianhao(yuyuebianhao);
			dayOfNotification.setYishengzhanghao(yishengzhanghao);
			dayOfNotification.setZhanghao(zhanghao);
			dayOfNotification.setTongzhitype("就诊当天提醒");
			dayOfNotification.setJieshouqudao("短信");
			dayOfNotification.setJieshoudizhi(shouji);
			dayOfNotification.setFasongzhuangtai("待发送");
			dayOfNotification.setChongshicishu(0);
			dayOfNotification.setAddtime(new Date());
			this.insert(dayOfNotification);
			
			// 立即发送就诊当天提醒通知
			sendNotification(dayOfNotification);
			
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * 生成通知编号
	 */
	private String generateNotificationId() {
		return "TZ" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 8);
	}
	
	/**
	 * 生成通知内容
	 */
	private String generateNotificationContent(TongzhijiluEntity tongzhijilu) {
		StringBuilder content = new StringBuilder();
		if ("预约成功通知".equals(tongzhijilu.getTongzhitype())) {
			content.append("尊敬的用户，您已成功预约医生。请按时就诊。");
		} else if ("就诊前一天提醒".equals(tongzhijilu.getTongzhitype())) {
			content.append("尊敬的用户，提醒您明天有预约，请准时就诊。");
		} else if ("就诊当天提醒".equals(tongzhijilu.getTongzhitype())) {
			content.append("尊敬的用户，提醒您今天有预约，请准时就诊。");
		}
		return content.toString();
	}
	
	/**
	 * 模拟发送短信
	 */
	private boolean sendSMS(String phone, String content) {
		// 这里应该是实际的短信发送逻辑
		// 为了演示，我们模拟90%的成功率
		return Math.random() > 0.1;
	}
	
	/**
	 * 模拟发送邮件
	 */
	private boolean sendEmail(String email, String content) {
		// 这里应该是实际的邮件发送逻辑
		// 为了演示，我们模拟90%的成功率
		return Math.random() > 0.1;
	}
}