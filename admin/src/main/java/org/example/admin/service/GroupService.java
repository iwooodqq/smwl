package org.example.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.admin.dao.entity.GroupDo;
import org.example.admin.dto.req.GroupUpdateReqDTO;
import org.example.admin.dto.res.GroupResDto;

import java.util.List;

public interface GroupService extends IService<GroupDo> {
    void savegroup(String group);

    List<GroupResDto> sort();

    void updategroup(GroupUpdateReqDTO groupUpdateReqDTO);


    void deletegroup(String gid);

}
