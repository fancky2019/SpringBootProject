package com.example.demo.model.entity.shiro;

import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 用户角色关系表
 * </p>
 *
 * @author author
 * @since 2022-08-31
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)

public class UserRole implements Serializable {

    private static final long serialVersionUID = 1L;

  private Integer id;

    /**
     * 用户id
     */
    private String userId;

    /**
     * 角色id
     */
    private String roleId;


}
