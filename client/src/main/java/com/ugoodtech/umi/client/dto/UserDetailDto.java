package com.ugoodtech.umi.client.dto;

/* Copyright (C) Ugoodtech, Inc - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * Written by Stone Shaw.
 */

import com.ugoodtech.umi.core.domain.User;
import io.swagger.annotations.ApiModel;

@ApiModel("用户详情")
public class UserDetailDto extends UserDto {
    private boolean following;
    private boolean blockMe;
    private boolean block;
    private boolean see;
    public UserDetailDto() {
    }

    public UserDetailDto(User user) {
        super(user);
    }

    public boolean isFollowing() {
        return following;
    }

    public void setFollowing(boolean following) {
        this.following = following;
    }

    public boolean isBlockMe() {
        return blockMe;
    }

    public void setBlockMe(boolean blockMe) {
        this.blockMe = blockMe;
    }

    public boolean isBlock() {
        return block;
    }

    public void setBlock(boolean block) {
        this.block = block;
    }

    public boolean isSee() {
        return see;
    }

    public void setSee(boolean see) {
        this.see = see;
    }

}
