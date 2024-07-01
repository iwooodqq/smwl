package org.example.admin.remote.dto.res;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ShortLinkCreateResDTO {

    /**
     * 短链接
     */
    private String fullShortUri;

    private String originUri;
    private String gid;
}
