package org.example.project.dto.req;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;
import org.example.project.dao.entity.LinkDO;

@Data
public class ShortLinkPagereqDTO extends Page<LinkDO> {
    /**
     * 分组标识
     */
    private String gid;
}
