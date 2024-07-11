package org.example.project.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;
import org.example.project.common.database.BaseDo;

@TableName("t_link_access_logs")
@Data
@Builder
public class LinkAccessLogsDO extends BaseDo {
    /**
     * id
     */
    private Long id;

    /**
     * 完整短链接
     */
    private String fullShortUrl;

    /**
     * 分组标识
     */
    private String gid;

    /**
     * 用户信息
     */
    private String user;

    /**
     * 浏览器
     */
    private String browser;

    /**
     * 操作系统
     */
    private String os;

    /**
     * ip
     */
    private String ip;
    /**
     * 访问网络
     */
    private String network;
    /**
     * 访问设备
     */
    private String device ;
    /**
     * 访问地区
     */
    private String locale ;
}
