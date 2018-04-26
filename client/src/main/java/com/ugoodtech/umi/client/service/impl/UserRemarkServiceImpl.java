package com.ugoodtech.umi.client.service.impl;

import java.sql.Timestamp;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.querydsl.core.BooleanBuilder;
import com.ugoodtech.umi.client.service.UserRemarkService;
import com.ugoodtech.umi.core.domain.QTopic;
import com.ugoodtech.umi.core.domain.User;
import com.ugoodtech.umi.core.domain.UserRemark;
import com.ugoodtech.umi.core.exception.UmiException;
import com.ugoodtech.umi.core.repository.TopicRepository;
import com.ugoodtech.umi.core.repository.UserRemarkRepository;

@Service
public class UserRemarkServiceImpl implements UserRemarkService{
	@Autowired
	private UserRemarkRepository userRemarkRepository;
	@Override
	public void saveRemark(Long remarkerId, Long targetId, String remarkName)
			throws UmiException {
		// TODO Auto-generated method stub
		UserRemark remark =userRemarkRepository.findRemark(remarkerId, targetId);
		if(null==remark){
			remark.setRemarker(remarkerId);
			remark.setTargetUserId(targetId);
		} 
			remark.setRemarkName(remarkName);
		 
		Date date = new Date();       
		Timestamp updateTime = new Timestamp(date.getTime());
		remark.setUpdateTime(updateTime);
		userRemarkRepository.save(remark);
	}

}
