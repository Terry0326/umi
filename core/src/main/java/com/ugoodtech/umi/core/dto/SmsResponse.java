package com.ugoodtech.umi.core.dto;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/* Copyright (C) Ugoodtech, Inc - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * <returnsms>
 * <returnstatus>Faild</returnstatus>
 *         <message>content解析错误</message>
 *         <balance>0</balance>
 *        <taskID>0</taskID>
 *         <billingAmount>0</billingAmount>
 *         <successCounts>0</successCounts>
 *         </returnsms>
 */

@XmlRootElement(name = "returnsms")
public class SmsResponse {
    private String returnStatus;
    private String message;
    private String billingAmount;
    private Integer successCounts;

    @XmlElement(name = "returnstatus")
    public String getReturnStatus() {
        return returnStatus;
    }

    public void setReturnStatus(String returnStatus) {
        this.returnStatus = returnStatus;
    }

    @XmlElement()
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @XmlElement(name = "BillingAmount")
    public String getBillingAmount() {
        return billingAmount;
    }

    public void setBillingAmount(String billingAmount) {
        this.billingAmount = billingAmount;
    }

    @XmlElement
    public Integer getSuccessCounts() {
        return successCounts;
    }

    public void setSuccessCounts(Integer successCounts) {
        this.successCounts = successCounts;
    }
}
