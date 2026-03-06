package com.cl.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.cl.dao.TongzhijiluDao;
import com.cl.entity.TongzhijiluEntity;
import com.cl.entity.view.TongzhijiluView;
import com.cl.utils.PageUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 通知记录服务实现类单元测试
 * 测试TongzhijiluServiceImpl的所有方法
 */
@ExtendWith(MockitoExtension.class)
public class TongzhijiluServiceImplTest {

    @Mock
    private TongzhijiluDao tongzhijiluDao;

    @InjectMocks
    private TongzhijiluServiceImpl tongzhijiluService;

    private TongzhijiluEntity testEntity;
    private TongzhijiluView testView;

    @BeforeEach
    void setUp() {
        testEntity = new TongzhijiluEntity();
        testEntity.setId(1L);
        testEntity.setYuyuebianhao("YY202403060001");
        testEntity.setTongzhileixing(1);
        testEntity.setTongzhineirong("您的预约已成功");
        testEntity.setZhanghao("user123");
        testEntity.setShouji("13800138000");
        testEntity.setFasongzhuangtai(0);
        testEntity.setChongshicishu(0);
        testEntity.setJihuafasongshijian(new Date());
        testEntity.setDuquzhuangtai(0);

        testView = new TongzhijiluView();
        testView.setId(1L);
        testView.setYuyuebianhao("YY202403060001");
        testView.setTongzhileixing(1);
        testView.setFasongzhuangtai(0);
    }

    /**
     * 测试查询需要重试的通知列表
     */
    @Test
    void testSelectRetryList_Success() {
        TongzhijiluEntity failedNotification = new TongzhijiluEntity();
        failedNotification.setId(1L);
        failedNotification.setFasongzhuangtai(3);
        failedNotification.setChongshicishu(1);

        when(tongzhijiluDao.selectRetryList(3)).thenReturn(Arrays.asList(failedNotification));

        List<TongzhijiluEntity> result = tongzhijiluService.selectRetryList(3);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(3, result.get(0).getFasongzhuangtai().intValue());
    }

    /**
     * 测试查询需要重试的通知列表 - 空结果
     */
    @Test
    void testSelectRetryList_Empty() {
        when(tongzhijiluDao.selectRetryList(3)).thenReturn(Arrays.asList());

        List<TongzhijiluEntity> result = tongzhijiluService.selectRetryList(3);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    /**
     * 测试查询用户未读通知数量
     */
    @Test
    void testSelectUnreadCount_Success() {
        when(tongzhijiluDao.selectUnreadCount("user123")).thenReturn(5);

        Integer count = tongzhijiluService.selectUnreadCount("user123");

        assertNotNull(count);
        assertEquals(5, count.intValue());
    }

    /**
     * 测试查询用户未读通知数量 - 无未读
     */
    @Test
    void testSelectUnreadCount_Zero() {
        when(tongzhijiluDao.selectUnreadCount("user123")).thenReturn(0);

        Integer count = tongzhijiluService.selectUnreadCount("user123");

        assertNotNull(count);
        assertEquals(0, count.intValue());
    }

    /**
     * 测试标记已读 - 成功场景
     */
    @Test
    void testMarkAsRead_Success() {
        Long id = 1L;
        when(tongzhijiluDao.updateById(any(TongzhijiluEntity.class))).thenReturn(1);

        boolean result = tongzhijiluService.markAsRead(id);

        assertTrue(result);
        verify(tongzhijiluDao, times(1)).updateById(argThat(entity -> entity.getId().equals(id) &&
                entity.getDuquzhuangtai() == 1 &&
                entity.getDuqushijian() != null));
    }

    /**
     * 测试标记已读 - 更新失败
     */
    @Test
    void testMarkAsRead_Failure() {
        Long id = 1L;
        when(tongzhijiluDao.updateById(any(TongzhijiluEntity.class))).thenReturn(0);

        boolean result = tongzhijiluService.markAsRead(id);

        assertFalse(result);
    }

    /**
     * 测试批量重试 - 成功场景
     */
    @Test
    void testBatchRetry_Success() {
        List<Long> ids = Arrays.asList(1L, 2L, 3L);
        when(tongzhijiluDao.updateById(any(TongzhijiluEntity.class))).thenReturn(1);

        boolean result = tongzhijiluService.batchRetry(ids);

        assertTrue(result);
        verify(tongzhijiluDao, times(3)).updateById(argThat(entity -> entity.getFasongzhuangtai() == 0 &&
                entity.getShibaiyuanyin() == null));
    }

    /**
     * 测试批量重试 - 部分失败（但方法仍返回true，因为代码逻辑如此）
     */
    @Test
    void testBatchRetry_PartialFailure() {
        List<Long> ids = Arrays.asList(1L, 2L);
        when(tongzhijiluDao.updateById(any(TongzhijiluEntity.class)))
                .thenReturn(1)
                .thenReturn(0);

        boolean result = tongzhijiluService.batchRetry(ids);

        // 根据实际代码逻辑，只要处理了记录就返回true，不检查更新结果
        assertTrue(result);
    }

    /**
     * 测试批量重试 - 空列表（根据实际代码返回false）
     */
    @Test
    void testBatchRetry_EmptyList() {
        // 空列表返回false
        boolean result = tongzhijiluService.batchRetry(new ArrayList<>());

        assertFalse(result);
        verify(tongzhijiluDao, never()).updateById(any());
    }

    /**
     * 测试批量重试 - null列表（根据实际代码返回false）
     */
    @Test
    void testBatchRetry_NullList() {
        // null列表返回false
        boolean result = tongzhijiluService.batchRetry(null);

        assertFalse(result);
        verify(tongzhijiluDao, never()).updateById(any());
    }

    /**
     * 测试视图类 - 通知类型名称映射
     */
    @Test
    void testTongzhijiluView_TypeNameMapping() {
        TongzhijiluView view1 = new TongzhijiluView();
        view1.setTongzhileixing(1);
        assertEquals("预约成功通知", view1.getTongzhileixingmingcheng());

        TongzhijiluView view2 = new TongzhijiluView();
        view2.setTongzhileixing(2);
        assertEquals("就诊前24小时提醒", view2.getTongzhileixingmingcheng());

        TongzhijiluView view3 = new TongzhijiluView();
        view3.setTongzhileixing(3);
        assertEquals("就诊前1小时提醒", view3.getTongzhileixingmingcheng());

        TongzhijiluView view4 = new TongzhijiluView();
        view4.setTongzhileixing(4);
        assertEquals("就诊前15分钟提醒", view4.getTongzhileixingmingcheng());

        TongzhijiluView view5 = new TongzhijiluView();
        view5.setTongzhileixing(5);
        assertEquals("未知类型", view5.getTongzhileixingmingcheng());

        TongzhijiluView viewNull = new TongzhijiluView();
        viewNull.setTongzhileixing(null);
        assertEquals("", viewNull.getTongzhileixingmingcheng());
    }

    /**
     * 测试视图类 - 发送状态名称映射
     */
    @Test
    void testTongzhijiluView_StatusNameMapping() {
        TongzhijiluView view0 = new TongzhijiluView();
        view0.setFasongzhuangtai(0);
        assertEquals("待发送", view0.getFasongzhuangtaimingcheng());

        TongzhijiluView view1 = new TongzhijiluView();
        view1.setFasongzhuangtai(1);
        assertEquals("发送中", view1.getFasongzhuangtaimingcheng());

        TongzhijiluView view2 = new TongzhijiluView();
        view2.setFasongzhuangtai(2);
        assertEquals("发送成功", view2.getFasongzhuangtaimingcheng());

        TongzhijiluView view3 = new TongzhijiluView();
        view3.setFasongzhuangtai(3);
        assertEquals("发送失败", view3.getFasongzhuangtaimingcheng());

        TongzhijiluView view4 = new TongzhijiluView();
        view4.setFasongzhuangtai(4);
        assertEquals("未知状态", view4.getFasongzhuangtaimingcheng());

        TongzhijiluView viewNull = new TongzhijiluView();
        viewNull.setFasongzhuangtai(null);
        assertEquals("", viewNull.getFasongzhuangtaimingcheng());
    }

    /**
     * 测试视图类 - 读取状态名称映射
     */
    @Test
    void testTongzhijiluView_ReadStatusMapping() {
        TongzhijiluView view0 = new TongzhijiluView();
        view0.setDuquzhuangtai(0);
        assertEquals("未读", view0.getDuquzhuangtaimingcheng());

        TongzhijiluView view1 = new TongzhijiluView();
        view1.setDuquzhuangtai(1);
        assertEquals("已读", view1.getDuquzhuangtaimingcheng());

        TongzhijiluView viewNull = new TongzhijiluView();
        viewNull.setDuquzhuangtai(null);
        assertEquals("", viewNull.getDuquzhuangtaimingcheng());
    }

/**
     * 测试视图类 - 从实体构造
     */
    @Test
    void testTongzhijiluView_ConstructFromEntity() {
        TongzhijiluEntity entity = new TongzhijiluEntity();
        entity.setId(1L);
        entity.setYuyuebianhao("YY001");
        entity.setTongzhileixing(1);
        entity.setFasongzhuangtai(2);
        entity.setDuquzhuangtai(0);

        TongzhijiluView view = new TongzhijiluView();
        // 手动复制属性
        view.setId(entity.getId());
        view.setYuyuebianhao(entity.getYuyuebianhao());
        view.setTongzhileixing(entity.getTongzhileixing());
        view.setFasongzhuangtai(entity.getFasongzhuangtai());
        view.setDuquzhuangtai(entity.getDuquzhuangtai());

        assertEquals("YY001", view.getYuyuebianhao());
        assertEquals(1, view.getTongzhileixing().intValue());
        assertEquals("预约成功通知", view.getTongzhileixingmingcheng());
        assertEquals("发送成功", view.getFasongzhuangtaimingcheng());
        assertEquals("未读", view.getDuquzhuangtaimingcheng());
    }
}
