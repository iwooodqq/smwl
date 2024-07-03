package org.example.admin.remote.dto.res;


import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class ShortLinkCountQueryResDTO {

    /**
     * 短链接分组返回查询参数
     */
    private String gid;
    private Integer LinkCount;
}
