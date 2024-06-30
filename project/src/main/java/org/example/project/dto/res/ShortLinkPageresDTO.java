package org.example.project.dto.res;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.util.Date;
@Data
public class ShortLinkPageresDTO {
    /**
     * id
     */
    private Long id;
    /**
     * 域名
     */
    private String domain;

    /**
     * 短链接
     */
    private String shortUri;
    /**
     * 完整短链接
     */
    private String fullShortUrl;
    /**
     * 原始链接
     */
    private String originUrl;
    /**
     * 分组标识
     */
    private String gid;

    /**
     * 有效期类型 0：永久有效 1：用户自定义
     */
    private Integer validDateType;

    /**
     * 有效期
     */
    private Date validDate;

    /**
     * 描述
     */
    private String describe;
    /**
     * 网站标识
     */
    private String favicon;
}
