package com.ugoodtech.umi.core.domain;

import com.ugoodtech.umi.core.domain.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigInteger;
import java.util.Date;

@ApiModel("被举报用户列表")
public class UserTipDto extends BaseEntity{
    @ApiModelProperty("用户账号")
    private String userName;
    @ApiModelProperty("用户昵称")
    private String nickName;
    @ApiModelProperty("处理状态")
    private Boolean done;
    @ApiModelProperty("处理时间")
    private Date creationTime;
    @ApiModelProperty("处理结果")
    private String doneStr;
    @ApiModelProperty("举报次数")
    private BigInteger tipNum;
    public UserTipDto() {
    }
    public UserTipDto(String userName, String nickName,  Boolean done,String doneStr, Date creationTime,  BigInteger tipNum) {
        this.userName = userName;
        this.nickName = nickName;
        this.done = done;
        this.creationTime = creationTime;
        this.doneStr = doneStr;
        this.tipNum = tipNum;
    }


    public BigInteger getTipNum() {
        return tipNum;
    }

    public void setTipNum(BigInteger tipNum) {
        this.tipNum = tipNum;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }


    public Boolean getDone() {
        return done;
    }

    public void setDone(Boolean done) {
        this.done = done;
    }

    @Override
    public Date getCreationTime() {
        return creationTime;
    }

    @Override
    public void setCreationTime(Date creationTime) {
        this.creationTime = creationTime;
    }

    public String getDoneStr() {
        return doneStr;
    }

    public void setDoneStr(String doneStr) {
        this.doneStr = doneStr;
    }
}
