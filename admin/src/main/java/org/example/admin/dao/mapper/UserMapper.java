package org.example.admin.dao.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.example.admin.dao.entity.UserDo;
@Mapper
public interface UserMapper extends BaseMapper<UserDo> {
}
