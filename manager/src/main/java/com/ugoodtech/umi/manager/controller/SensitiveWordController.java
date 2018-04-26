package com.ugoodtech.umi.manager.controller;

/* Copyright (C) Ugoodtech, Inc - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * Written by Stone Shaw.
 */


import com.ugoodtech.umi.core.domain.SensitiveWord;
import com.ugoodtech.umi.core.domain.SystemUser;
import com.ugoodtech.umi.core.dto.JsonResponse;
import com.ugoodtech.umi.core.repository.SensitiveRepository;
import com.ugoodtech.umi.manager.dto.UpLoadExcelUtil;
import com.ugoodtech.umi.manager.service.SensitiveService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.util.List;

@RestController
@RequestMapping("/sensitive")
public class SensitiveWordController extends BaseController{

    @Autowired
    private SensitiveRepository sensitiveRepository;

    @Autowired
    private SensitiveService sensitiveService;


    @RequestMapping(value = "/words", method = RequestMethod.GET)
    public JsonResponse<Iterable<SensitiveWord>> getCategories(String param) {
        return JsonResponse.successResponseWithData(sensitiveService.query(param));
    }

    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    public JsonResponse<SensitiveWord> edit(Long id,String name) {
        List<SensitiveWord> sensitiveWords=sensitiveRepository.findByName(name);
        if(null!=sensitiveWords&&sensitiveWords.size()>0){
            for(SensitiveWord sensitiveWord:sensitiveWords){
                if(sensitiveWord.getId().equals(id)){
                    return JsonResponse.successResponse();
                }
            }
            return JsonResponse.errorResponseWithError("","该关键字已存在");
        }else{
            if(!StringUtils.isEmpty(name)){
                SensitiveWord sensitiveWord=sensitiveRepository.findOne(id);
                sensitiveWord.setName(name);
                return JsonResponse.successResponseWithData(sensitiveRepository.save(sensitiveWord));
            }else{
                return JsonResponse.errorResponseWithError("","关键字不能为空");
            }
        }


    }

    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public JsonResponse deleteCategory(Authentication authentication,
                                    Long wordId) {
        sensitiveRepository.delete(wordId);
        return JsonResponse.successResponse();
    }

    @RequestMapping("/importWord")
    @Transactional
//    @ResponseBody
    public void importWord(
            Authentication authentication,
            @RequestParam(value = "wordXls", required = false) MultipartFile file,
            HttpServletResponse response) throws Exception {


        String fileName = file.getOriginalFilename();
        String fileType = fileName.substring(fileName.length() - 3, fileName.length());
//        System.out.println("导入的文件名 ：" + fileName + "\t 文件后缀名：" + fileType);
        int totalNumber = 0;
        int failureNumber = 0;
        String usernameField = "";
        String passwordField = "";
        SystemUser user = (SystemUser) authentication.getPrincipal();
        String dataField = "";
        int buildingNumber = 0;
        if ("xls".equals(fileType) || "lsx".equals(fileType)) { // 上传文件是excel时    文件文件后缀名为xls 
//            System.out.println("xlsx");
            UpLoadExcelUtil upLoadExcelUtil=new UpLoadExcelUtil();
            List<String[]> list = upLoadExcelUtil.getExcelTest(file.getInputStream());
//            System.out.println(list.toString());
            buildingNumber=list.size()-1;
            if (list.size() > 0) {
                String[] str;
                totalNumber=list.size();
                if ("".equals(dataField)) {
                    for (int i = 1; i < list.size(); i++) {
//                        buildingNumber += 1;
                        failureNumber +=1;
                        str = list.get(i);
                        String word=str[0];
                        if(null!=sensitiveRepository.findByName(word)&&sensitiveRepository.findByName(word).size()>0){

                        }else{
                            if(!StringUtils.isEmpty(word)){
                                SensitiveWord sensitiveWord=new SensitiveWord();
                                sensitiveWord.setName(word);
                                sensitiveRepository.save(sensitiveWord);
                            }
                        }
                    }
//                    failureNumber = buildingNumber - successNumber - 1;
                }
//response.setStatus(HttpServletResponse.SC_OK);
                response.setContentType("text/html;charset=UTF-8");
                response.getWriter().println("{\"success\":true,\"totalNumber\":" + totalNumber + ",\"failureNumber\":\"" + failureNumber + "\",\"error_description\":\"" + usernameField + "\",\"passwordField\":\"" + passwordField + "\"}");
                return;

            }

        }
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("text/html;charset=UTF-8");
        response.getWriter().println("{\"success\":false,\"successNumber\":" + totalNumber + ",\"failureNumber\":\"" + failureNumber + "\",\"usernameField\":\"" + usernameField + "\",\"passwordField\":\"" + passwordField + "\"}");

//        jsonObjectResponse.setSuccess(true);
//        jsonObjectResponse.setData("");
//        jsonObjectResponse.setTotal(successNumber);
//        jsonObjectResponse.setMessage(failureNumber+"");

//        return jsonObjectResponse;
    }



}
