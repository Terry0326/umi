package com.ugoodtech.umi.core.domain;

/* Copyright (C) Ugoodtech, Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.ugoodtech.umi.core.domain.serializer.SystemUserSerializer;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

/**
 * 系统用户
 */
@Entity
@Table(name = "system_users")//系统用户
@JsonSerialize(using = SystemUserSerializer.class)
public class SystemUser implements Serializable, UserDetails {

    private static final long serialVersionUID = -1944409005559327088L;

    private Long id;//数据库id
    private String username; //用户名
    private String password; //密码
    private String realName;//真实姓名
    private String telephone;//手机号
    private boolean enabled;//是否激活
    private boolean accountExpired;//账户失效
    private boolean accountLocked;//是否被锁
    private boolean credentialsExpired;//证书失效
    private Set<Role> roles = new HashSet<>();//角色
    private Date creationTime;//创建时间
    private Date updateTime;
    private String description;//描述
    private SystemUser creator;
    private Long appUid;//前台关联账户
//    private Institution institution;
    private String roleName;

    /**
     * Default constructor - creates a new instance with no values set.
     */
    public SystemUser() {
    }

    public SystemUser(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public SystemUser(Long id) {
        this.id = id;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long getId() {
        return id;
    }

    @Override
    @Column(nullable = false, length = 50, unique = true)
    public String getUsername() {
        return username;
    }

    @Column(name = "real_name", length = 50)
    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    @Column(length = 20)
    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }


    @Override
    @Column(nullable = false)
    @JsonIgnore
    public String getPassword() {
        return password;
    }


    @ManyToMany(fetch = FetchType.EAGER)
    @Fetch(FetchMode.SELECT)
    @JoinTable(
            name = "system_user_role",
            joinColumns = {@JoinColumn(name = "user_id")},
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    public Set<Role> getRoles() {
        return roles;
    }

    @Transient
    public List<String> getRoleNames() {
        List<String> rolenames = new ArrayList<>();
        for (Role role : roles) {
            rolenames.add(role.getName());
        }
        return rolenames;
    }

    @Transient
    public boolean hasRole(String roleName) {
        return this.getRoleNames().contains(roleName);
    }

    /**
     * Adds a role for the user
     *
     * @param role the fully instantiated role
     */
    public void addRole(Role role) {
        getRoles().add(role);
    }

    /**
     * @return GrantedAuthority[] an array of roles.
     * @see UserDetails#getAuthorities()
     */
    @Override
    @Transient
    public Set<GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> authorities = new LinkedHashSet<GrantedAuthority>();
        authorities.addAll(roles);
        return authorities;
    }

    @Override
    @Column(name = "account_enabled")
    public boolean isEnabled() {
        return enabled;
    }

    @Column(name = "account_expired", nullable = false)
    @JsonIgnore
    public boolean isAccountExpired() {
        return accountExpired;
    }

    /**
     * @return true if account is still active
     * @see UserDetails#isAccountNonExpired()
     */
    @Override
    @Transient
    public boolean isAccountNonExpired() {
        return !isAccountExpired();
    }

    @Column(name = "account_locked", nullable = false)
    @JsonIgnore
    public boolean isAccountLocked() {
        return accountLocked;
    }

    /**
     * @return false if account is locked
     * @see UserDetails#isAccountNonLocked()
     */
    @Override
    @Transient
    @JsonIgnore
    public boolean isAccountNonLocked() {
        return !isAccountLocked();
    }

    @Column(name = "credentials_expired", nullable = false)
    @JsonIgnore
    public boolean isCredentialsExpired() {
        return credentialsExpired;
    }

    /**
     * @return true if credentials haven't expired
     * @see UserDetails#isCredentialsNonExpired()
     */
    @Override
    @Transient
    @JsonIgnore
    public boolean isCredentialsNonExpired() {
        return !credentialsExpired;
    }


    public void setId(Long id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setAccountExpired(boolean accountExpired) {
        this.accountExpired = accountExpired;
    }

    public void setAccountLocked(boolean accountLocked) {
        this.accountLocked = accountLocked;
    }

    public void setCredentialsExpired(boolean credentialsExpired) {
        this.credentialsExpired = credentialsExpired;
    }
 

	@Column(length = 500)
    public String getDescription() {
        return description;
    }
	@Column(name = "app_Uid")
    public Long getAppUid() {
		return appUid;
	}

	public void setAppUid(Long appUid) {
		this.appUid = appUid;
	}

	public void setDescription(String description) {
        this.description = description;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SystemUser)) {
            return false;
        }

        final SystemUser user = (SystemUser) o;

        return !(username != null ? !username.equals(user.getUsername()) : user.getUsername() != null);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return (username != null ? username.hashCode() : 0);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        ToStringBuilder sb = new ToStringBuilder(this, ToStringStyle.DEFAULT_STYLE)
                .append("username", this.username)
                .append("enabled", this.enabled)
                .append("accountExpired", this.accountExpired)
                .append("credentialsExpired", this.credentialsExpired)
                .append("accountLocked", this.accountLocked);

        if (roles != null) {
            sb.append("Granted Authorities: ");
            Set<GrantedAuthority> authorities = this.getAuthorities();
            for (GrantedAuthority authority : authorities) {
                sb.append(authority.getAuthority()).append(",");
            }
        } else {
            sb.append("No Granted Authorities");
        }
        return sb.toString();
    }

    @Column(name = "creation_time")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "Asia/Shanghai")
    public Date getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(Date creationTime) {
        this.creationTime = creationTime;
    }


    @ManyToOne
    @JoinColumn(name = "creator_id")
    @JsonIgnore
    public SystemUser getCreator() {
        return creator;
    }

    public void setCreator(SystemUser creator) {
        this.creator = creator;
    } 
   
	public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    @Column(name = "update_time")
//    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "Asia/Shanghai")
    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}
