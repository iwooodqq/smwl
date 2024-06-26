package org.example.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.admin.dao.entity.GroupDo;

public interface GroupService extends IService<GroupDo> {
    void savegroup(String group);
}
