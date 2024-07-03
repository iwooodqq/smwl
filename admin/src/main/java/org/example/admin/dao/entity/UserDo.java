package org.example.admin.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.example.admin.database.BaseDo;
import java.io.Serializable;
@TableName("t_user")
@Data
public class UserDo extends BaseDo implements Serializable {
    /**
     * id
     */
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 邮箱
     */
    private String mail;

    /**
     * 注销时间戳
     */
    private Long deletionTime;

    public UserDo() {}
}