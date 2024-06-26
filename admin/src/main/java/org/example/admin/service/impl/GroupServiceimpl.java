package org.example.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.AllArgsConstructor;
import org.example.admin.dao.entity.GroupDo;
import org.example.admin.dao.mapper.GroupMapper;
import org.example.admin.service.GroupService;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class GroupServiceimpl extends ServiceImpl<GroupMapper, GroupDo>implements GroupService {
}
