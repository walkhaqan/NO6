package com.cl.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import com.cl.entity.TongzhijiluEntity;
import com.cl.entity.view.TongzhijiluView;
import java.util.List;
import java.util.Map;

/**
 * 通知记录
 * 
 * @author 
 * @email 
 * @date 2025-03-27 15:44:15
 */
public interface TongzhijiluDao extends BaseMapper<TongzhijiluEntity> {
	
	List<TongzhijiluView> selectListView(Pagination page, Wrapper<TongzhijiluEntity> wrapper);
	
	List<TongzhijiluView> selectListView(Wrapper<TongzhijiluEntity> wrapper);
	
	TongzhijiluView selectView(Wrapper<TongzhijiluEntity> wrapper);
	
	List<Map<String, Object>> selectValue(Map<String, Object> params, Wrapper<TongzhijiluEntity> wrapper);
	
	List<Map<String, Object>> selectTimeStatValue(Map<String, Object> params, Wrapper<TongzhijiluEntity> wrapper);
	
	List<Map<String, Object>> selectGroup(Map<String, Object> params, Wrapper<TongzhijiluEntity> wrapper);
}