package com.cl.service.impl;

import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.List;
import java.util.Date;

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
	public List<TongzhijiluEntity> selectRetryList(Integer maxRetryCount) {
		return baseMapper.selectRetryList(maxRetryCount);
	}
	
	@Override
	public Integer selectUnreadCount(String zhanghao) {
		return baseMapper.selectUnreadCount(zhanghao);
	}
	
	@Override
	public boolean markAsRead(Long id) {
		TongzhijiluEntity entity = new TongzhijiluEntity();
		entity.setId(id);
		entity.setDuquzhuangtai(1);
		entity.setDuqushijian(new Date());
		return this.updateById(entity);
	}
	
	@Override
	public boolean batchRetry(List<Long> ids) {
		if (ids == null || ids.isEmpty()) {
			return false;
		}
		for (Long id : ids) {
			TongzhijiluEntity entity = new TongzhijiluEntity();
			entity.setId(id);
			entity.setFasongzhuangtai(0);
			entity.setShibaiyuanyin(null);
			this.updateById(entity);
		}
		return true;
	}
	
	


}
