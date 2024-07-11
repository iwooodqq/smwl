package org.example.project.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.example.project.dao.entity.LinkDeviceStatsDo;

public interface LinkDeviceStatsMapper extends BaseMapper<LinkDeviceStatsDo> {
    /**
     * 记录访问设备监控数据
     */
    @Insert("INSERT INTO t_link_device_stats (full_short_url, gid, date, cnt, device, create_time, update_time, del_flag) " +
            "VALUES( #{linkDeviceStatsDO.fullShortUrl}, #{linkDeviceStatsDO.gid}, #{linkDeviceStatsDO.date}, #{linkDeviceStatsDO.cnt}, #{linkDeviceStatsDO.device}, NOW(), NOW(), 0) " +
            "ON DUPLICATE KEY UPDATE cnt = cnt +  #{linkDeviceStatsDO.cnt};")
    void shortLinkDeviceState(@Param("linkDeviceStatsDO") LinkDeviceStatsDo linkDeviceStatsDO);
}
