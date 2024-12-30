package com.system.assessment.controller;

import com.system.assessment.constants.ProcessType;
import com.system.assessment.exception.CustomExceptionType;
import com.system.assessment.exception.ResponseResult;
import com.system.assessment.pojo.EvaluateTable;
import com.system.assessment.service.EmailService;
import com.system.assessment.service.EvaluateService;
import com.system.assessment.service.RelationshipService;
import com.system.assessment.service.TemplateCreateService;
import com.system.assessment.template.panelTemplate;
import com.system.assessment.vo.AverageScoringConditionVO;
import com.system.assessment.vo.AverageScoringVO;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")  // 统一为所有方法设置路径前缀
@Slf4j
public class TestController {

    @Autowired
    public EmailService emailService;

    @Autowired
    public RelationshipService relationshipService;

    @Autowired
    public EvaluateService evaluateService;

    @Autowired
    public TemplateCreateService templateCreateService;

    @RequestMapping(value = "/email",method = RequestMethod.GET)
    public ResponseResult email(){
        String content = panelTemplate.htmlTemplateTest("PZY");

        emailService.sendMessageHTML("2339134840@qq.com", "评议结果", content);
        return ResponseResult.success();
    }

    @RequestMapping(value = "/word",method = RequestMethod.GET)
    public ResponseResult word() throws IOException {

        EvaluateTable evaluateTable = evaluateService.evaluateTableInfo();
        String context = evaluateTable.getOpeningRemarks();
        String content = panelTemplate.htmlTemplate2("PZY", context);
        emailService.sendMessageHTML("2339134840@qq.com", "评议结果", content);
        return ResponseResult.success();
    }

    @RequestMapping(value = "/excel",method = RequestMethod.POST)
    public ResponseResult excel(@RequestParam("file") MultipartFile file) throws IOException {
        Integer newEnableProcess = evaluateService.findNewEnableProcess();
        if(newEnableProcess == null){
            newEnableProcess = 1;
        }
        if(!newEnableProcess.equals(ProcessType.BuildRelationships.getCode())){
            return ResponseResult.error(CustomExceptionType.USER_INPUT_ERROR.getCode(), "该操作在当前环节无效!");
        }

        if (file.isEmpty()) {
            return ResponseResult.error(401, "该文件为空");
        }
        List<String> errorList = relationshipService.addRelationshipExcel(file);
        if(errorList == null){
            return ResponseResult.error(CustomExceptionType.USER_INPUT_ERROR.getCode(), "矩阵导入错误!");
        }else if(errorList.size() == 0){
            return ResponseResult.success();
        }else {
            String error = String.join(",", errorList) + "导入失败!";
            return ResponseResult.error(401, error);
        }
    }


//    @RequestMapping(value = "/word",method = RequestMethod.GET)
//    public ResponseResult word() throws IOException {
//        Map<String, String> variables = new HashMap<>();
//        variables.put("score", "95");
//        variables.put("lxyz", "97.5");
//        variables.put("business", "92.1");
//        // 获取当前日期
//        LocalDate today = LocalDate.now();
//
//        // 格式化日期为 "2024年11月16日"
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy年MM月dd日");
//        String formattedDate = today.format(formatter);
//
//        // 替换普通空格为非换行空格
//        variables.put("yyyyymmmddd", formattedDate);
//        File wordFile = templateCreateService.updateWordFile("朴振宇",variables);
//        emailService.sendMessageCarryFile("2339134840@qq.com", "雷达图显示","雷达图在附件", wordFile);
//        return ResponseResult.success();
//    }

}
