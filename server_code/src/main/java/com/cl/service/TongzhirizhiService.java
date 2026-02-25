package com.cl.service;

import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.service.IService;
import com.cl.utils.PageUtils;
import com.cl.entity.TongzhirizhiEntity;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Param;
import com.cl.entity.view.TongzhirizhiView;


/**
 * 通知日志
 *
 * @author 
 * @email 
 * @date 2025-03-27 15:44:15
 */
public interface TongzhirizhiService extends IService<TongzhirizhiEntity> {

    PageUtils queryPage(Map<String, Object> params);
    
   	List<TongzhirizhiView> selectListView(Wrapper<TongzhirizhiEntity> wrapper);
   	
   	TongzhirizhiView selectView(@Param("ew") Wrapper<TongzhirizhiEntity> wrapper);
   	
   	PageUtils queryPage(Map<String, Object> params,Wrapper<TongzhirizhiEntity> wrapper);
   	
   	List<TongzhirizhiEntity> selectFailedList(int maxRetryCount);
   	
   	void handleLog(Long id, String handleBeizhu);
}
