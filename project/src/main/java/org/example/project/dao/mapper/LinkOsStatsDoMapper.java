package org.example.project.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.example.project.dao.entity.LinkOsStatsDo;
import org.example.project.dto.req.ShortLinkGroupStatsReqDTO;
import org.example.project.dto.req.ShortLinkStatsReqDTO;

import java.util.HashMap;
import java.util.List;

public interface LinkOsStatsDoMapper extends BaseMapper<LinkOsStatsDo> {
    @Insert("INSERT INTO " +
            "t_link_os_stats (full_short_url,gid, date, cnt, os, create_time, update_time, del_flag) " +
            "VALUES( #{linkOsStats.fullShortUrl},#{linkOsStats.gid}, #{linkOsStats.date}, #{linkOsStats.cnt}, #{linkOsStats.os}, NOW(), NOW(), 0) " +
            "ON DUPLICATE KEY UPDATE cnt = cnt +  #{linkOsStats.cnt};")
    void shortLinkOsState(@Param("linkOsStats") LinkOsStatsDo linkOsStatsDO);

    /**
     * 根据短链接获取指定日期内操作系统监控数据
     */
    @Select("SELECT " +
            "    os, " +
            "    SUM(cnt) AS count " +
            "FROM " +
            "    t_link_os_stats " +
            "WHERE " +
            "    full_short_url = #{param.fullShortUrl} " +
            "    AND gid = #{param.gid} " +
            "    AND date BETWEEN #{param.startDate} and #{param.endDate} " +
            "GROUP BY " +
            "    full_short_url,  date, os;")
    List<HashMap<String, Object>> listOsStatsByShortLink(@Param("param") ShortLinkStatsReqDTO requestParam);
    /**
     * 根据分组获取指定日期内操作系统监控数据
     */
    @Select("SELECT " +
            "    os, " +
            "    SUM(cnt) AS count " +
            "FROM " +
            "    t_link_os_stats " +
            "WHERE " +
            "    gid = #{param.gid} " +
            "    AND date BETWEEN #{param.startDate} and #{param.endDate} " +
            "GROUP BY " +
            "    gid, os;")
    List<HashMap<String, Object>> listOsStatsByGroup(@Param("param") ShortLinkGroupStatsReqDTO requestParam);
}
