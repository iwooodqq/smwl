package org.example.admin.remote.dto.req;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 短链接监控访问记录参数
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShortLinkStatsAccessRecordReqDTO extends Page{

    /**
     * 完整短链接
     */
    private String fullShortUrl;

    /**
     * 分组标识
     */
    private String gid;

    /**
     * 开始日期
     */
    private String startDate;

    /**
     * 结束日期
     */
    private String endDate;
}