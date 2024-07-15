package org.example.project.dto.res;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ShortLinkCreateResDTO {

    /**
     * 短链接
     */
    private String fullShortUri;

    private String originUri;
    private String gid;
}
