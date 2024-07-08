package org.example.project.toolkit;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;

import java.util.Date;
import java.util.Optional;

import static org.example.project.common.constant.ShortLinkConstant.DEFAULT_CREATE_VALUE_TIME;

public class LinkUtil {
    public static long getLinkCacheValidTime(Date validate){
        return Optional.ofNullable(validate).
                map(each-> DateUtil.between(new Date(),each, DateUnit.MS)).
                orElse(DEFAULT_CREATE_VALUE_TIME);
    }
}
