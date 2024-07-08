package org.example.admin.remote.dto.req;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;

import java.util.List;

@Data
public class ShortLinkRecycleBinPagereqDTO extends Page{
    /**
     * 分组标识
     */
    private List<String> gidList;
}
