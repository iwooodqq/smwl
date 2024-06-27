package org.example.admin.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.admin.database.BaseDo;

import java.util.Date;

@Data
@TableName("t_group")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupDo extends BaseDo {
    /**
     * id
     */
    private Long id;
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
