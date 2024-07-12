package org.example.project.dto.res;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ShortLinkStatsAccessRecordRespDTO {
    /**
     * 访客类型
     */
    private String uvType;

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
    /**
     * 用户信息
     */
    private String user;
    /**
     * 访客时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date createTime;
}
