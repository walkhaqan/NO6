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
	 * 查询需要重试的通知记录
	 */
	List<TongzhijiluEntity> selectRetryList(Integer maxRetryCount);
	
	/**
	 * 查询用户的未读通知数量
	 */
	Integer selectUnreadCount(String zhanghao);
	
	/**
	 * 标记通知为已读
	 */
	boolean markAsRead(Long id);
	
	/**
	 * 批量重试发送失败的通知
	 */
	boolean batchRetry(List<Long> ids);
   	
   
}
