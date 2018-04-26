package com.ugoodtech.umi.core.domain;

/* Copyright (C) Ugoodtech, Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ugoodtech.umi.core.domain.converter.GenderConverter;
import com.ugoodtech.umi.core.domain.converter.RegisterWayConverter;
import com.ugoodtech.umi.core.domain.converter.UserStatusConverter;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
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
 * 客户端用户
 */
@ApiModel
@Entity
@Table(name = "users")
public class User extends BaseEntity implements Serializable, UserDetails {
    @ApiModelProperty("用户名或手机号码")
    private String username;                    // required
    @JsonIgnore
    @ApiModelProperty(hidden = true)
    private String password;                    // required
    private String dialingCode;
    private boolean enabled;
    @JsonIgnore
    @ApiModelProperty(hidden = true)
    private boolean accountExpired;
    @JsonIgnore
    @ApiModelProperty(hidden = true)
    private boolean accountLocked;
    @JsonIgnore
    @ApiModelProperty(hidden = true)
    private boolean credentialsExpired;
    @ApiModelProperty("昵称")
    private String nickname;
    @ApiModelProperty("性别")
    private Gender gender;
    @ApiModelProperty("地址")
    private Address address;
    @ApiModelProperty("签名")
    private String signature;
    @ApiModelProperty("头像key")
    private String avatar;
    private Date lastLoginTime;
    private Set<Role> roles = new HashSet<>();
    //
    @ApiModelProperty("发帖数量")
    private Long publishTopicsNum;
    @ApiModelProperty("评论数量")
    private Long commentNum;
    @ApiModelProperty("粉丝数量")
    private Long followersNum;
    @ApiModelProperty("关注数量")
    private Long followingNum;
    @ApiModelProperty("帖子点赞数量")
    private Long topicLikeNum;
    private UserStatus status;
    private RegistrationWay registrationWay;
    private boolean infoCompleted;
    private boolean receiveNotification;
    private String geoHash;

    /**
     * Default constructor - creates a new instance with no values set.
     */
    public User() {
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    @Override
    @Column(nullable = false, length = 50, unique = true)
    public String getUsername() {
        return username;
    }

    @Override
    @Column(nullable = false)
    @JsonIgnore
    public String getPassword() {
        return password;
    }

    @Column(length = 10)
    public String getDialingCode() {
        return dialingCode;
    }

    public void setDialingCode(String dialingCode) {
        this.dialingCode = dialingCode;
    }

    @Override
    @Column(name = "account_enabled")
    public boolean isEnabled() {
        return enabled;
    }

    @Column(name = "account_expired", nullable = false)
    public boolean isAccountExpired() {
        return accountExpired;
    }

    /**
     * @return true if account is still active
     * @see UserDetails#isAccountNonExpired()
     */
    @Override
    @Transient
    @JsonIgnore
    public boolean isAccountNonExpired() {
        return !isAccountExpired();
    }

    @Column(name = "account_locked", nullable = false)
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

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
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

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof User)) {
            return false;
        }

        final User user = (User) o;

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

        return sb.toString();
    }

    @ManyToMany(fetch = FetchType.EAGER)
    @Fetch(FetchMode.SELECT)
    @JoinTable(
            name = "user_role",
            joinColumns = {@JoinColumn(name = "user_id")},
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    @JsonIgnore
    public Set<Role> getRoles() {
        return roles;
    }

    @Transient
    @JsonIgnore
    public List<String> getRoleNames() {
        List<String> rolenames = new ArrayList<>();
        for (Role role : roles) {
            rolenames.add(role.getName());
        }
        return rolenames;
    }

    @Transient
    @JsonIgnore
    public boolean hasRole(String roleName) {
        return this.getRoleNames().contains(roleName);
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
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
    @JsonIgnore
    public Set<GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> authorities = new LinkedHashSet<GrantedAuthority>();
        authorities.addAll(roles);
        return authorities;
    }

    @Column(length = 100, unique = true)
    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    @Column
    @Convert(converter = GenderConverter.class)
    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    @Embedded
    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    @Column(length = 200)
    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    @Column(length = 50)
    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    @Column
    public Date getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(Date lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public Long getPublishTopicsNum() {
        return publishTopicsNum;
    }

    public void setPublishTopicsNum(Long publishTopicsNum) {
        this.publishTopicsNum = publishTopicsNum;
    }

    @Column
    @Convert(converter = UserStatusConverter.class)
    public UserStatus getStatus() {
        return status;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }

    public Long getFollowersNum() {
        return followersNum;
    }

    public void setFollowersNum(Long followersNum) {
        this.followersNum = followersNum;
    }

    public Long getFollowingNum() {
        return followingNum;
    }

    public void setFollowingNum(Long followingNum) {
        this.followingNum = followingNum;
    }

    public Long getTopicLikeNum() {
        return topicLikeNum;
    }

    public void setTopicLikeNum(Long topicLikeNum) {
        this.topicLikeNum = topicLikeNum;
    }

    public Long getCommentNum() {
        return commentNum;
    }

    public void setCommentNum(Long commentNum) {
        this.commentNum = commentNum;
    }

    public boolean isInfoCompleted() {
        return infoCompleted;
    }

    public void setInfoCompleted(boolean infoCompleted) {
        this.infoCompleted = infoCompleted;
    }

    @Column(name = "receive_notif")
    public boolean isReceiveNotification() {
        return receiveNotification;
    }

    public void setReceiveNotification(boolean receiveNotification) {
        this.receiveNotification = receiveNotification;
    }

    @Column(length = 20)
    public String getGeoHash() {
        return geoHash;
    }

    public void setGeoHash(String geoHash) {
        this.geoHash = geoHash;
    }

    @Column
    @Convert(converter = RegisterWayConverter.class)
    public RegistrationWay getRegistrationWay() {
        return registrationWay;
    }

    public void setRegistrationWay(RegistrationWay registerWay) {
        this.registrationWay = registerWay;
    }

    @JsonFormat(shape = JsonFormat.Shape.OBJECT)
    public enum UserStatus {
        NORMAL(1, "正常"), IN_BLACKLIST(2, "已加入黑名单");
        private Integer code;
        private String description;

        UserStatus(Integer code, String description) {
            this.code = code;
            this.description = description;
        }

        @JsonProperty
        public Integer getCode() {
            return code;
        }

        @JsonProperty
        public String getDescription() {
            return description;
        }

        public static final Map<Integer, UserStatus> dbValues = new HashMap<>();

        static {
            for (UserStatus status : values()) {
                dbValues.put(status.code, status);
            }
        }

        @JsonCreator
        public static UserStatus forValue(Integer value) {
            return dbValues.get(value);
        }
    }

    @JsonFormat(shape = JsonFormat.Shape.OBJECT)
    public enum RegistrationWay {
        PHONE_NUMBER_REGISTER(1, "手机号注册"), WEI_XIN_REGISTER(2, "微信注册"), QQ_REGISTER(3, "QQ注册");
        private Integer code;
        private String description;

        RegistrationWay(Integer code, String description) {
            this.code = code;
            this.description = description;
        }

        @JsonProperty
        public Integer getCode() {
            return code;
        }

        @JsonProperty
        public String getDescription() {
            return description;
        }

        public static final Map<Integer, RegistrationWay> dbValues = new HashMap<>();

        static {
            for (RegistrationWay way : values()) {
                dbValues.put(way.code, way);
            }
        }

        @JsonCreator
        public static RegistrationWay forValue(Integer value) {
            return dbValues.get(value);
        }
    }
}
