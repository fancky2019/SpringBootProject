package com.example.demo.model.entity.shiro;

import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 角色权限关系表
 * </p>
 *
 * @author author
 * @since 2022-08-31
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)

public class RolePermission implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

    /**
     * 角色id
     */
    private String roleId;

    /**
     * 权限id
     */
    private String permissionId;


}
