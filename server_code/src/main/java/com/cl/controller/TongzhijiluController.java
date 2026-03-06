package com.cl.controller;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

import com.cl.utils.ValidatorUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.cl.annotation.IgnoreAuth;
import com.cl.annotation.SysLog;

import com.cl.entity.TongzhijiluEntity;
import com.cl.entity.view.TongzhijiluView;

import com.cl.service.TongzhijiluService;
import com.cl.service.TokenService;
import com.cl.utils.PageUtils;
import com.cl.utils.R;
import com.cl.utils.MPUtil;
import com.cl.utils.MapUtils;
import com.cl.utils.CommonUtil;

/**
 * 通知记录
 * 后端接口
 * @author 
 * @email 
 * @date 2025-03-27 15:44:15
 */
@RestController
@RequestMapping("/tongzhijilu")
public class TongzhijiluController {
    @Autowired
    private TongzhijiluService tongzhijiluService;

    /**
     * 后台列表
     */
    @RequestMapping("/page")
    public R page(@RequestParam Map<String, Object> params, TongzhijiluEntity tongzhijilu,
                  HttpServletRequest request) {
        EntityWrapper<TongzhijiluEntity> ew = new EntityWrapper<TongzhijiluEntity>();

        PageUtils page = tongzhijiluService.queryPage(params, MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, tongzhijilu), params), params));
        return R.ok().put("data", page);
    }

    /**
     * 前端列表
     */
    @IgnoreAuth
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params, TongzhijiluEntity tongzhijilu,
                  HttpServletRequest request) {
        EntityWrapper<TongzhijiluEntity> ew = new EntityWrapper<TongzhijiluEntity>();

        PageUtils page = tongzhijiluService.queryPage(params, MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, tongzhijilu), params), params));
        return R.ok().put("data", page);
    }

    /**
     * 列表
     */
    @RequestMapping("/lists")
    public R list(TongzhijiluEntity tongzhijilu) {
        EntityWrapper<TongzhijiluEntity> ew = new EntityWrapper<TongzhijiluEntity>();
        ew.allEq(MPUtil.allEQMapPre(tongzhijilu, "tongzhijilu"));
        return R.ok().put("data", tongzhijiluService.selectListView(ew));
    }

    /**
     * 查询
     */
    @RequestMapping("/query")
    public R query(TongzhijiluEntity tongzhijilu) {
        EntityWrapper<TongzhijiluEntity> ew = new EntityWrapper<TongzhijiluEntity>();
        ew.allEq(MPUtil.allEQMapPre(tongzhijilu, "tongzhijilu"));
        TongzhijiluView tongzhijiluView = tongzhijiluService.selectView(ew);
        return R.ok("查询通知记录成功").put("data", tongzhijiluView);
    }

    /**
     * 后端详情
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id) {
        TongzhijiluEntity tongzhijilu = tongzhijiluService.selectById(id);
        tongzhijilu = tongzhijiluService.selectView(new EntityWrapper<TongzhijiluEntity>().eq("id", id));
        return R.ok().put("data", tongzhijilu);
    }

    /**
     * 前端详情
     */
    @IgnoreAuth
    @RequestMapping("/detail/{id}")
    public R detail(@PathVariable("id") Long id) {
        TongzhijiluEntity tongzhijilu = tongzhijiluService.selectById(id);
        tongzhijilu = tongzhijiluService.selectView(new EntityWrapper<TongzhijiluEntity>().eq("id", id));
        return R.ok().put("data", tongzhijilu);
    }

    /**
     * 后端保存
     */
    @RequestMapping("/save")
    @SysLog("新增通知记录")
    public R save(@RequestBody TongzhijiluEntity tongzhijilu, HttpServletRequest request) {
        tongzhijiluService.insert(tongzhijilu);
        return R.ok();
    }

    /**
     * 前端保存
     */
    @SysLog("新增通知记录")
    @RequestMapping("/add")
    public R add(@RequestBody TongzhijiluEntity tongzhijilu, HttpServletRequest request) {
        tongzhijiluService.insert(tongzhijilu);
        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @Transactional
    @SysLog("修改通知记录")
    public R update(@RequestBody TongzhijiluEntity tongzhijilu, HttpServletRequest request) {
        tongzhijiluService.updateById(tongzhijilu);
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @SysLog("删除通知记录")
    public R delete(@RequestBody Long[] ids) {
        tongzhijiluService.deleteBatchIds(Arrays.asList(ids));
        return R.ok();
    }

    /**
     * 标记通知为已读
     */
    @RequestMapping("/read/{id}")
    @SysLog("标记通知为已读")
    public R markAsRead(@PathVariable("id") Long id) {
        boolean result = tongzhijiluService.markAsRead(id);
        if (result) {
            return R.ok("标记成功");
        } else {
            return R.error("标记失败");
        }
    }

    /**
     * 批量标记为已读
     */
    @RequestMapping("/readBatch")
    @Transactional
    @SysLog("批量标记通知为已读")
    public R markAsReadBatch(@RequestBody Long[] ids) {
        for (Long id : ids) {
            tongzhijiluService.markAsRead(id);
        }
        return R.ok("批量标记成功");
    }

    /**
     * 获取用户未读通知数量
     */
    @RequestMapping("/unreadCount")
    public R getUnreadCount(@RequestParam String zhanghao) {
        Integer count = tongzhijiluService.selectUnreadCount(zhanghao);
        return R.ok().put("data", count);
    }

    /**
     * 批量重试发送失败的通知
     */
    @RequestMapping("/retry")
    @SysLog("批量重试发送失败的通知")
    public R batchRetry(@RequestBody Long[] ids) {
        boolean result = tongzhijiluService.batchRetry(Arrays.asList(ids));
        if (result) {
            return R.ok("已加入重试队列");
        } else {
            return R.error("重试失败");
        }
    }

    /**
     * 获取需要重试的通知列表
     */
    @RequestMapping("/retryList")
    public R getRetryList() {
        List<TongzhijiluEntity> list = tongzhijiluService.selectRetryList(3);
        return R.ok().put("data", list);
    }

    /**
     * 获取发送统计
     */
    @RequestMapping("/statistics")
    public R getStatistics() {
        Map<String, Object> result = new HashMap<>();

        // 总数量
        int total = tongzhijiluService.selectCount(null);
        result.put("total", total);

        // 待发送
        int pending = tongzhijiluService.selectCount(
                new EntityWrapper<TongzhijiluEntity>().eq("fasongzhuangtai", 0));
        result.put("pending", pending);

        // 发送成功
        int success = tongzhijiluService.selectCount(
                new EntityWrapper<TongzhijiluEntity>().eq("fasongzhuangtai", 2));
        result.put("success", success);

        // 发送失败
        int failed = tongzhijiluService.selectCount(
                new EntityWrapper<TongzhijiluEntity>().eq("fasongzhuangtai", 3));
        result.put("failed", failed);

        // 已读
        int read = tongzhijiluService.selectCount(
                new EntityWrapper<TongzhijiluEntity>().eq("duquzhuangtai", 1));
        result.put("read", read);

        // 未读
        int unread = tongzhijiluService.selectCount(
                new EntityWrapper<TongzhijiluEntity>().eq("duquzhuangtai", 0));
        result.put("unread", unread);

        return R.ok().put("data", result);
    }

    /**
     * 获取当前用户的通知列表
     */
    @RequestMapping("/myList")
    public R myList(@RequestParam Map<String, Object> params, HttpServletRequest request) {
        String tableName = request.getSession().getAttribute("tableName").toString();
        String username = (String) request.getSession().getAttribute("username");

        EntityWrapper<TongzhijiluEntity> ew = new EntityWrapper<TongzhijiluEntity>();
        ew.eq("zhanghao", username);
        ew.orderBy("addtime", false);

        PageUtils page = tongzhijiluService.queryPage(params, ew);
        return R.ok().put("data", page);
    }
}
