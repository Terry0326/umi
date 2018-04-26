package com.ugoodtech.umi.client.controller;

import java.util.List;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import springfox.documentation.annotations.ApiIgnore;

import com.ugoodtech.umi.client.dto.RichTopic;
import com.ugoodtech.umi.client.service.UserRemarkService;
import com.ugoodtech.umi.core.domain.Topic;
import com.ugoodtech.umi.core.domain.User;
import com.ugoodtech.umi.core.dto.JsonResponse;
import com.ugoodtech.umi.core.exception.UmiException;

@Api(description = "用户备注api")
@RestController
@RequestMapping("/topics")
public class UserRemarkController {
	@Autowired
	UserRemarkService remarkService;

	@ApiOperation("修改备注")
	@RequestMapping(value = "/upRemark", method = RequestMethod.POST)
	public JsonResponse getUserHomeTopics(
			@ApiIgnore Authentication authentication,
			@ApiParam("备注名称") @RequestParam String remarkName,
			@ApiParam("被备注人Id") @RequestParam Long targetId)
			throws UmiException {
		User remarker = (User) authentication.getPrincipal();
		remarkService.saveRemark(remarker.getId(), targetId, remarkName);
		return JsonResponse.successResponse();
	}
}
