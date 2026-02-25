package com.cl.controller;

import java.util.*;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.cl.annotation.SysLog;

import com.cl.entity.TongzhirizhiEntity;
import com.cl.entity.view.TongzhirizhiView;

import com.cl.service.TongzhirizhiService;
import com.cl.utils.PageUtils;
import com.cl.utils.R;
import com.cl.utils.MPUtil;

@RestController
@RequestMapping("/tongzhirizhi")
public class TongzhirizhiController {
    @Autowired
    private TongzhirizhiService tongzhirizhiService;

    @RequestMapping("/page")
    public R page(@RequestParam Map<String, Object> params, TongzhirizhiEntity tongzhirizhi,
                  HttpServletRequest request){
        EntityWrapper<TongzhirizhiEntity> ew = new EntityWrapper<TongzhirizhiEntity>();
        PageUtils page = tongzhirizhiService.queryPage(params, MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, tongzhirizhi), params), params));
        return R.ok().put("data", page);
    }

    @RequestMapping("/lists")
    public R list(TongzhirizhiEntity tongzhirizhi){
        EntityWrapper<TongzhirizhiEntity> ew = new EntityWrapper<TongzhirizhiEntity>();
        ew.allEq(MPUtil.allEQMapPre(tongzhirizhi, "tongzhirizhi"));
        return R.ok().put("data", tongzhirizhiService.selectListView(ew));
    }

    @RequestMapping("/query")
    public R query(TongzhirizhiEntity tongzhirizhi){
        EntityWrapper<TongzhirizhiEntity> ew = new EntityWrapper<TongzhirizhiEntity>();
        ew.allEq(MPUtil.allEQMapPre(tongzhirizhi, "tongzhirizhi"));
        TongzhirizhiView tongzhirizhiView = tongzhirizhiService.selectView(ew);
        return R.ok("查询通知日志成功").put("data", tongzhirizhiView);
    }

    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
        TongzhirizhiEntity tongzhirizhi = tongzhirizhiService.selectById(id);
        tongzhirizhi = tongzhirizhiService.selectView(new EntityWrapper<TongzhirizhiEntity>().eq("id", id));
        return R.ok().put("data", tongzhirizhi);
    }

    @RequestMapping("/save")
    @SysLog("新增通知日志")
    public R save(@RequestBody TongzhirizhiEntity tongzhirizhi, HttpServletRequest request){
        tongzhirizhiService.insert(tongzhirizhi);
        return R.ok();
    }

    @RequestMapping("/update")
    @Transactional
    @SysLog("修改通知日志")
    public R update(@RequestBody TongzhirizhiEntity tongzhirizhi, HttpServletRequest request){
        tongzhirizhiService.updateById(tongzhirizhi);
        return R.ok();
    }

    @RequestMapping("/delete")
    @SysLog("删除通知日志")
    public R delete(@RequestBody Long[] ids){
        tongzhirizhiService.deleteBatchIds(Arrays.asList(ids));
        return R.ok();
    }

    @RequestMapping("/handle/{id}")
    @Transactional
    @SysLog("处理通知日志")
    public R handle(@PathVariable("id") Long id, @RequestParam String handleBeizhu, HttpServletRequest request){
        tongzhirizhiService.handleLog(id, handleBeizhu);
        return R.ok();
    }

    @RequestMapping("/handleBatch")
    @Transactional
    @SysLog("批量处理通知日志")
    public R handleBatch(@RequestBody Long[] ids, @RequestParam String handleBeizhu){
        for(Long id : ids) {
            tongzhirizhiService.handleLog(id, handleBeizhu);
        }
        return R.ok();
    }

    @RequestMapping("/failedList")
    public R failedList(@RequestParam Map<String, Object> params){
        List<TongzhirizhiEntity> failedList = tongzhirizhiService.selectFailedList(3);
        return R.ok().put("data", failedList);
    }

    @RequestMapping("/stats")
    public R stats(){
        Map<String, Object> result = new HashMap<>();
        EntityWrapper<TongzhirizhiEntity> totalEw = new EntityWrapper<>();
        int total = tongzhirizhiService.selectCount(totalEw);
        
        EntityWrapper<TongzhirizhiEntity> successEw = new EntityWrapper<>();
        successEw.eq("status", 1);
        int successCount = tongzhirizhiService.selectCount(successEw);
        
        EntityWrapper<TongzhirizhiEntity> failedEw = new EntityWrapper<>();
        failedEw.eq("status", 2);
        int failedCount = tongzhirizhiService.selectCount(failedEw);
        
        EntityWrapper<TongzhirizhiEntity> pendingEw = new EntityWrapper<>();
        pendingEw.eq("handle_status", 0).eq("status", 2);
        int pendingHandle = tongzhirizhiService.selectCount(pendingEw);
        
        result.put("total", total);
        result.put("success", successCount);
        result.put("failed", failedCount);
        result.put("pendingHandle", pendingHandle);
        return R.ok().put("data", result);
    }

}
