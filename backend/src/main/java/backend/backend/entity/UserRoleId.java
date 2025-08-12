package backend.backend.entity;

import java.io.Serializable;

import jakarta.persistence.Embeddable;

@Embeddable
public class UserRoleId implements Serializable {
    private Integer userId;
    private Integer roleId;

    public UserRoleId() {}

    public UserRoleId(Integer userId, Integer roleId) {
        this.userId = userId;
        this.roleId = roleId;
    }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public Integer getRoleId() { return roleId; }
    public void setRoleId(Integer roleId) { this.roleId = roleId; }

}
