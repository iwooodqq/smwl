package org.example.admin.dto.res;
import lombok.Data;

@Data
public class GroupResDto {
    /**
     * 分组标识
     */
    private String gid;
    /**
     * 分组名称
     */
    private String name;
    /**
     * 创建分组用户名
     */
    private String username;
    /**
     * 分组排序
     */
    private Integer sortOrder;
}
