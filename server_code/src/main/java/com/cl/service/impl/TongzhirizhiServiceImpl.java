package com.cl.service.impl;

import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.List;

import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.cl.utils.PageUtils;
import com.cl.utils.Query;


import com.cl.dao.TongzhirizhiDao;
import com.cl.entity.TongzhirizhiEntity;
import com.cl.service.TongzhirizhiService;
import com.cl.entity.view.TongzhirizhiView;
import java.util.Date;

@Service("tongzhirizhiService")
public class TongzhirizhiServiceImpl extends ServiceImpl<TongzhirizhiDao, TongzhirizhiEntity> implements TongzhirizhiService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        Page<TongzhirizhiEntity> page = this.selectPage(
                new Query<TongzhirizhiEntity>(params).getPage(),
                new EntityWrapper<TongzhirizhiEntity>()
        );
        return new PageUtils(page);
    }
    
    @Override
	public PageUtils queryPage(Map<String, Object> params, Wrapper<TongzhirizhiEntity> wrapper) {
		  Page<TongzhirizhiView> page =new Query<TongzhirizhiView>(params).getPage();
	        page.setRecords(baseMapper.selectListView(page,wrapper));
	    	PageUtils pageUtil = new PageUtils(page);
	    	return pageUtil;
 	}
    
	@Override
	public List<TongzhirizhiView> selectListView(Wrapper<TongzhirizhiEntity> wrapper) {
		return baseMapper.selectListView(wrapper);
	}

	@Override
	public TongzhirizhiView selectView(Wrapper<TongzhirizhiEntity> wrapper) {
		return baseMapper.selectView(wrapper);
	}
	
	@Override
	public List<TongzhirizhiEntity> selectFailedList(int maxRetryCount) {
		return baseMapper.selectList(
			new EntityWrapper<TongzhirizhiEntity>()
				.eq("status", 2)
				.lt("retry_count", maxRetryCount)
				.eq("handle_status", 0)
		);
	}
	
	@Override
	public void handleLog(Long id, String handleBeizhu) {
		TongzhirizhiEntity log = baseMapper.selectById(id);
		if(log != null) {
			log.setHandleStatus(1);
			log.setHandleBeizhu(handleBeizhu);
			log.setHandleTime(new Date());
			baseMapper.updateById(log);
		}
	}

}
