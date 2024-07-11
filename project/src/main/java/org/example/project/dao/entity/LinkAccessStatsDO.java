package org.example.project.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import org.example.project.common.database.BaseDo;

import java.util.Date;


@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_link_access_stats")
public class LinkAccessStatsDO extends BaseDo {
    private Long id;
    /**
     * 分组标识
     */
    private String gid;
    /**
     * 完整短链接
     */
    private String fullShortUrl;

    /**
     * 日期
     */
    private Date date;
    /**
     * 访问量
     */
    private Integer pv;
    /**
     * 独立访问数
     */
    private Integer uv;
    /**
     * 独立ip数
     */
    private Integer uip;
    /**
     * 小时
     */
    private Integer hour;
    /**
     * 星期
     */
    private Integer weekday;
}
