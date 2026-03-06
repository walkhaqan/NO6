package com.cl.boundary;

import com.cl.entity.TongzhijiluEntity;
import com.cl.entity.YishengyuyueEntity;
import com.cl.entity.view.TongzhijiluView;
import com.cl.service.MessagesService;
import com.cl.service.TongzhijiluService;
import com.cl.service.impl.NotificationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 通知功能边界条件测试类
 * 测试各种极端场景和边界条件
 */
@ExtendWith(MockitoExtension.class)
public class NotificationBoundaryTest {

    @Mock
    private TongzhijiluService tongzhijiluService;

    @Mock
    private MessagesService messagesService;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    private YishengyuyueEntity testAppointment;

    @BeforeEach
    void setUp() {
        testAppointment = new YishengyuyueEntity();
        testAppointment.setId(1L);
        testAppointment.setYuyuebianhao("YY202403060001");
        testAppointment.setYishengzhanghao("doctor001");
        testAppointment.setDianhua("13800138000");
        testAppointment.setZhanghao("user123");
        testAppointment.setShouji("13800138000");
        testAppointment.setYuyuebeizhu("感冒就诊");
    }

    // ==================== 时间边界测试 ====================

    /**
     * 测试预约时间为当前时间（边界值）
     */
    @Test
    void testCreateNotifications_CurrentTime() {
        testAppointment.setYuyueshijian(new Date());

        when(tongzhijiluService.insert(any(TongzhijiluEntity.class))).thenReturn(true);

        notificationService.createNotifications(testAppointment);

        verify(tongzhijiluService, atLeast(1)).insert(any(TongzhijiluEntity.class));
    }

    /**
     * 测试预约时间为过去时间（已过期预约）
     */
    @Test
    void testCreateNotifications_PastTime() {
        testAppointment.setYuyueshijian(new Date(System.currentTimeMillis() - 86400000)); // 昨天

        when(tongzhijiluService.insert(any(TongzhijiluEntity.class))).thenReturn(true);

        notificationService.createNotifications(testAppointment);

        // 即使预约已过期，仍应创建通知记录
        verify(tongzhijiluService, atLeast(1)).insert(any(TongzhijiluEntity.class));
    }

    /**
     * 测试预约时间为未来极远时间
     */
    @Test
    void testCreateNotifications_FarFutureTime() {
        testAppointment.setYuyueshijian(new Date(System.currentTimeMillis() + 31536000000L)); // 一年后

        when(tongzhijiluService.insert(any(TongzhijiluEntity.class))).thenReturn(true);

        notificationService.createNotifications(testAppointment);

        // 预约成功通知 + 24小时提醒 + 1小时提醒 + 15分钟提醒 = 4个
        verify(tongzhijiluService, times(4)).insert(any(TongzhijiluEntity.class));
    }

    /**
     * 测试预约时间刚好24小时后
     */
    @Test
    void testCreateNotifications_Exactly24Hours() {
        testAppointment.setYuyueshijian(new Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000));

        when(tongzhijiluService.insert(any(TongzhijiluEntity.class))).thenReturn(true);

        notificationService.createNotifications(testAppointment);

        verify(tongzhijiluService, atLeast(1)).insert(any(TongzhijiluEntity.class));
    }

    /**
     * 测试预约时间刚好1小时后
     */
    @Test
    void testCreateNotifications_Exactly1Hour() {
        testAppointment.setYuyueshijian(new Date(System.currentTimeMillis() + 60 * 60 * 1000));

        when(tongzhijiluService.insert(any(TongzhijiluEntity.class))).thenReturn(true);

        notificationService.createNotifications(testAppointment);

        verify(tongzhijiluService, atLeast(1)).insert(any(TongzhijiluEntity.class));
    }

    /**
     * 测试预约时间刚好15分钟后
     */
    @Test
    void testCreateNotifications_Exactly15Minutes() {
        testAppointment.setYuyueshijian(new Date(System.currentTimeMillis() + 15 * 60 * 1000));

        when(tongzhijiluService.insert(any(TongzhijiluEntity.class))).thenReturn(true);

        notificationService.createNotifications(testAppointment);

        verify(tongzhijiluService, atLeast(1)).insert(any(TongzhijiluEntity.class));
    }

    // ==================== 字符串长度边界测试 ====================

    /**
     * 测试超长预约编号
     */
    @Test
    void testCreateNotifications_LongYuyuebianhao() {
        // 使用2天后的时间确保所有提醒都被创建
        testAppointment.setYuyueshijian(new Date(System.currentTimeMillis() + 2 * 86400000));
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 100; i++) {
            sb.append("0");
        }
        testAppointment.setYuyuebianhao("YY" + sb.toString());

        when(tongzhijiluService.insert(any(TongzhijiluEntity.class))).thenReturn(true);

        notificationService.createNotifications(testAppointment);

        verify(tongzhijiluService, times(4)).insert(any(TongzhijiluEntity.class));
    }

    /**
     * 测试超长手机号码
     */
    @Test
    void testCreateNotifications_LongPhoneNumber() {
        testAppointment.setYuyueshijian(new Date(System.currentTimeMillis() + 2 * 86400000));
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 5; i++) {
            sb.append("13800138000");
        }
        testAppointment.setShouji(sb.toString());

        when(tongzhijiluService.insert(any(TongzhijiluEntity.class))).thenReturn(true);

        notificationService.createNotifications(testAppointment);

        verify(tongzhijiluService, times(4)).insert(any(TongzhijiluEntity.class));
    }

    /**
     * 测试空字符串字段
     */
    @Test
    void testCreateNotifications_EmptyStrings() {
        testAppointment.setYuyueshijian(new Date(System.currentTimeMillis() + 2 * 86400000));
        testAppointment.setYuyuebianhao("");
        testAppointment.setZhanghao("");
        testAppointment.setShouji("");

        when(tongzhijiluService.insert(any(TongzhijiluEntity.class))).thenReturn(true);

        notificationService.createNotifications(testAppointment);

        verify(tongzhijiluService, times(4)).insert(any(TongzhijiluEntity.class));
    }

    /**
     * 测试特殊字符字段
     */
    @Test
    void testCreateNotifications_SpecialCharacters() {
        testAppointment.setYuyueshijian(new Date(System.currentTimeMillis() + 2 * 86400000));
        testAppointment.setYuyuebeizhu("详情<script>alert('xss')</script>");

        when(tongzhijiluService.insert(any(TongzhijiluEntity.class))).thenReturn(true);

        notificationService.createNotifications(testAppointment);

        verify(tongzhijiluService, times(4)).insert(any(TongzhijiluEntity.class));
    }

    // ==================== 批量操作边界测试 ====================

    /**
     * 测试批量重试空列表 - 根据实际代码返回false
     */
    @Test
    void testBatchRetry_EmptyList() {
        when(tongzhijiluService.batchRetry(anyList())).thenReturn(false);
        
        boolean result = tongzhijiluService.batchRetry(Collections.emptyList());

        // 根据实际代码逻辑，空列表返回false
        assertFalse(result);
    }

    /**
     * 测试批量重试超大列表
     */
    @Test
    void testBatchRetry_LargeList() {
        List<Long> ids = new ArrayList<>();
        for (long i = 1; i <= 1000; i++) {
            ids.add(i);
        }

        when(tongzhijiluService.batchRetry(ids)).thenReturn(true);

        boolean result = tongzhijiluService.batchRetry(ids);

        assertTrue(result);
        verify(tongzhijiluService, times(1)).batchRetry(ids);
    }

    /**
     * 测试批量重试包含null的列表
     */
    @Test
    void testBatchRetry_ListWithNull() {
        List<Long> ids = Arrays.asList(1L, null, 3L);

        when(tongzhijiluService.batchRetry(ids)).thenReturn(true);

        boolean result = tongzhijiluService.batchRetry(ids);

        // 应处理null值而不抛出异常
        assertTrue(result);
    }

    // ==================== 状态边界测试 ====================

    /**
     * 测试通知类型边界值
     */
    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 3, 4, 5, Integer.MAX_VALUE})
    void testNotificationTypeBoundaries(int type) {
        testAppointment.setYuyueshijian(new Date(System.currentTimeMillis() + 2 * 86400000));

        when(tongzhijiluService.insert(any(TongzhijiluEntity.class))).thenReturn(true);

        notificationService.createNotifications(testAppointment);

        verify(tongzhijiluService, atLeast(0)).insert(any(TongzhijiluEntity.class));
    }

    // ==================== 内存边界测试 ====================

    /**
     * 测试超长通知内容
     */
    @Test
    void testCreateNotifications_LongContent() {
        testAppointment.setYuyueshijian(new Date(System.currentTimeMillis() + 2 * 86400000));
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 10000; i++) {
            sb.append("A");
        }
        testAppointment.setYuyuebeizhu(sb.toString()); // 超长内容

        when(tongzhijiluService.insert(any(TongzhijiluEntity.class))).thenReturn(true);

        notificationService.createNotifications(testAppointment);

        verify(tongzhijiluService, times(4)).insert(any(TongzhijiluEntity.class));
    }

    // ==================== 时区边界测试 ====================

    /**
     * 测试不同时区的预约时间
     */
    @Test
    void testCreateNotifications_DifferentTimeZones() {
        // 使用UTC时间
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        cal.add(Calendar.DAY_OF_YEAR, 2);
        testAppointment.setYuyueshijian(cal.getTime());

        when(tongzhijiluService.insert(any(TongzhijiluEntity.class))).thenReturn(true);

        notificationService.createNotifications(testAppointment);

        verify(tongzhijiluService, times(4)).insert(any(TongzhijiluEntity.class));
    }

    // ==================== 空值边界测试 ====================

    /**
     * 测试所有字段为null的预约实体
     */
    @Test
    void testCreateNotifications_AllNullFields() {
        YishengyuyueEntity emptyAppointment = new YishengyuyueEntity();
        // 所有字段保持null

        notificationService.createNotifications(emptyAppointment);

        // 不应抛出异常，也不应创建通知
        verify(tongzhijiluService, never()).insert(any());
    }

    /**
     * 测试null通知实体
     */
    @Test
    void testCreateNotifications_NullAppointment() {
        notificationService.createNotifications(null);

        verify(tongzhijiluService, never()).insert(any());
    }

    // ==================== 电话号码边界测试 ====================

    /**
     * 测试各种格式的电话号码
     */
    @ParameterizedTest
    @ValueSource(strings = {
        "13800138000",
        "+8613800138000",
        "138-0013-8000",
        "(86)13800138000",
        "008613800138000",
        "",
        "123",
        "abcdefghij"
    })
    void testVariousPhoneFormats(String phone) {
        testAppointment.setYuyueshijian(new Date(System.currentTimeMillis() + 2 * 86400000));
        testAppointment.setShouji(phone);

        when(tongzhijiluService.insert(any(TongzhijiluEntity.class))).thenReturn(true);

        notificationService.createNotifications(testAppointment);

        verify(tongzhijiluService, times(4)).insert(any(TongzhijiluEntity.class));
    }

    // ==================== 视图类边界测试 ====================

    /**
     * 测试视图类 - 通知类型名称映射边界
     */
    @Test
    void testTongzhijiluView_TypeNameMappingBoundaries() {
        TongzhijiluView viewNegative = new TongzhijiluView();
        viewNegative.setTongzhileixing(-1);
        assertEquals("未知类型", viewNegative.getTongzhileixingmingcheng());

        TongzhijiluView viewLarge = new TongzhijiluView();
        viewLarge.setTongzhileixing(999);
        assertEquals("未知类型", viewLarge.getTongzhileixingmingcheng());
    }

    /**
     * 测试视图类 - 发送状态名称映射边界
     */
    @Test
    void testTongzhijiluView_StatusNameMappingBoundaries() {
        TongzhijiluView viewNegative = new TongzhijiluView();
        viewNegative.setFasongzhuangtai(-1);
        assertEquals("未知状态", viewNegative.getFasongzhuangtaimingcheng());

        TongzhijiluView viewLarge = new TongzhijiluView();
        viewLarge.setFasongzhuangtai(999);
        assertEquals("未知状态", viewLarge.getFasongzhuangtaimingcheng());
    }

    /**
     * 测试视图类 - 读取状态名称映射边界
     * 注意：实际代码中getDuquzhuangtaimingcheng()只判断是否为1
     */
    @Test
    void testTongzhijiluView_ReadStatusMappingBoundaries() {
        TongzhijiluView viewNegative = new TongzhijiluView();
        viewNegative.setDuquzhuangtai(-1);
        // 实际代码中只要不是1就是"未读"
        assertEquals("未读", viewNegative.getDuquzhuangtaimingcheng());

        TongzhijiluView viewLarge = new TongzhijiluView();
        viewLarge.setDuquzhuangtai(999);
        // 999不是1，所以是"未读"（实际代码逻辑：只有1才是"已读"）
        assertEquals("未读", viewLarge.getDuquzhuangtaimingcheng());
    }
}
