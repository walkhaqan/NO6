package com.cl.dao;

import com.cl.entity.TongzhirizhiEntity;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;

import org.apache.ibatis.annotations.Param;
import com.cl.entity.view.TongzhirizhiView;


/**
 * 通知日志
 * 
 * @author 
 * @email 
 * @date 2025-03-27 15:44:15
 */
public interface TongzhirizhiDao extends BaseMapper<TongzhirizhiEntity> {
	
	List<TongzhirizhiView> selectListView(@Param("ew") Wrapper<TongzhirizhiEntity> wrapper);

	List<TongzhirizhiView> selectListView(Pagination page,@Param("ew") Wrapper<TongzhirizhiEntity> wrapper);
	
	TongzhirizhiView selectView(@Param("ew") Wrapper<TongzhirizhiEntity> wrapper);
	

}
