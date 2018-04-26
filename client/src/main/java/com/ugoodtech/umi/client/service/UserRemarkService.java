package com.ugoodtech.umi.client.service;

import com.ugoodtech.umi.core.domain.User;
import com.ugoodtech.umi.core.exception.UmiException;

public interface UserRemarkService {
	/**
	 * 保存备注
	 */
	 void saveRemark(Long remarker,Long targetId,String remarkName) throws UmiException;
}
