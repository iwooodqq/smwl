package org.example.project.dto.req;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;
import org.example.project.dao.entity.LinkDO;

import java.util.List;

@Data
public class ShortLinkRecycleBinPagereqDTO extends Page<LinkDO>{
    /**
     * 分组标识
     */
    private List<String> gidList;
}
