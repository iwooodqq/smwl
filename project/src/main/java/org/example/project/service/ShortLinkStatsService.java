package org.example.project.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.example.project.dto.req.ShortLinkGroupStatsReqDTO;
import org.example.project.dto.req.ShortLinkStatsAccessRecordReqDTO;
import org.example.project.dto.req.ShortLinkStatsReqDTO;

import org.example.project.dto.res.ShortLinkStatsAccessRecordRespDTO;
import org.example.project.dto.res.ShortLinkStatsRespDTO;

/**
 * 短链接监控接口层
 */
public interface ShortLinkStatsService {

    /**
     * 获取单个短链接监控数据
     *
     * @param requestParam 获取短链接监控数据入参
     * @return 短链接监控数据
     */
    ShortLinkStatsRespDTO oneShortLinkStats(ShortLinkStatsReqDTO requestParam);

    IPage<ShortLinkStatsAccessRecordRespDTO> groupShortLinkStatsAccessRecord(ShortLinkStatsAccessRecordReqDTO requestParam);

    ShortLinkStatsRespDTO groupShortLinkStats(ShortLinkGroupStatsReqDTO requestParam);
}