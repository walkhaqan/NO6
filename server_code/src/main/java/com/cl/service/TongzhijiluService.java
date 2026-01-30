package com.cl.service;

import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.service.IService;
import com.cl.utils.PageUtils;
import com.cl.entity.TongzhijiluEntity;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Param;
import com.cl.entity.view.TongzhijiluView;

/**
 * 通知记录
 *
 * @author 
 * @email 
 * @date 2025-03-27 15:44:15
 */
public interface TongzhijiluService extends IService<TongzhijiluEntity> {

    PageUtils queryPage(Map<String, Object> params);
    
   	List<TongzhijiluView> selectListView(Wrapper<TongzhijiluEntity> wrapper);
   	
   	TongzhijiluView selectView(@Param("ew") Wrapper<TongzhijiluEntity> wrapper);
   	
   	PageUtils queryPage(Map<String, Object> params,Wrapper<TongzhijiluEntity> wrapper);
   	
   	/**
   	 * 发送通知
   	 * @param tongzhijilu 通知记录
   	 * @return 发送结果
   	 */
   	boolean sendNotification(TongzhijiluEntity tongzhijilu);
   	
   	/**
   	 * 重试发送通知
   	 * @param id 通知记录ID
   	 * @return 重试结果
   	 */
   	boolean retryNotification(Long id);
   	
   	/**
   	 * 处理失败通知
   	 * @param id 通知记录ID
   	 * @param chuliren 处理人
   	 * @param chulizhuangtai 处理状态
   	 * @param beizhixinxi 备注信息
   	 * @return 处理结果
   	 */
   	boolean handleFailedNotification(Long id, String chuliren, String chulizhuangtai, String beizhixinxi);
   	
   	/**
   	 * 根据预约创建通知记录
   	 * @param yuyuebianhao 预约编号
   	 * @param yishengzhanghao 医生账号
   	 * @param zhanghao 用户账号
   	 * @param shouji 手机号
   	 * @param yuyueshijian 预约时间
   	 * @return 创建结果
   	 */
   	boolean createNotificationsForAppointment(String yuyuebianhao, String yishengzhanghao, String zhanghao, String shouji, Date yuyueshijian);
}