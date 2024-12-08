package com.system.assessment.controller;

import com.system.assessment.constants.PathConstants;
import com.system.assessment.constants.ProcessType;
import com.system.assessment.exception.CustomExceptionType;
import com.system.assessment.exception.ResponseResult;
import com.system.assessment.pojo.EvaluateProcess;
import com.system.assessment.service.EvaluateService;
import com.system.assessment.service.ScoringBoardService;
import com.system.assessment.service.TemplateCreateService;
import com.system.assessment.utils.AuthenticationUtil;
import com.system.assessment.utils.FileUtil;
import com.system.assessment.vo.PanelScoreBoardVO;
import com.system.assessment.vo.ProcessStepVO;
import com.system.assessment.vo.ScoreGettingDetailIncludeEvaluated;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("/api")  // 统一为所有方法设置路径前缀
@Slf4j
@Api(tags = "个人得分面板")
public class PersonalPanelController {

    @Value("${spring.profiles.active:dev}") // 默认环境为开发环境
    private String activeProfile;

    @Autowired
    public TemplateCreateService templateCreateService;

    @Autowired
    public EvaluateService evaluateService;

    @Autowired
    public ScoringBoardService scoringBoardService;


    @ApiOperation("个人面板-得分雷达图")
    @RequestMapping(value = "/panel",method = RequestMethod.GET)
    public ResponseResult selfPanel(){
        Integer newEnableProcess = evaluateService.findNewEnableProcess();
        if(newEnableProcess == null){
            newEnableProcess = 0;
        }
        if(!newEnableProcess.equals(ProcessType.ResultPublic.getCode())){
            return ResponseResult.error(CustomExceptionType.USER_INPUT_ERROR.getCode(), "本轮结果尚未统计完全, 无法查看得分情况!");
        }

        Integer evaluatedId = AuthenticationUtil.getUserId();
        PanelScoreBoardVO panelScoreBoardVO = scoringBoardService.selfPanel(evaluatedId);
        return ResponseResult.success(panelScoreBoardVO);
    }

    @ApiOperation("报告模版上传")
    @RequestMapping(value = "/template/upload",method = RequestMethod.POST)
    public ResponseResult templateUpload(@RequestParam("file") MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            return ResponseResult.error(CustomExceptionType.USER_INPUT_ERROR.getCode(), "上传失败，文件为空！");
        }

        // 检查是否为Word文件
        String contentType = file.getContentType();
        if (contentType == null || (!contentType.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document")
                && !contentType.equals("application/msword"))) {
            return ResponseResult.error(CustomExceptionType.USER_INPUT_ERROR.getCode(), "上传失败，文件类型错误！");
        }

        // 确定保存路径
        File destinationFile;
        String fileName = "template.docx";

        if ("prd".equals(activeProfile)) {
            Path filePath = Paths.get(PathConstants.TEMPLATE_FOLDER, fileName);
            destinationFile = filePath.toFile();
        } else {
            ClassPathResource resource = new ClassPathResource(PathConstants.TEMPLATE_FOLDER);
            File directory = resource.getFile();
            // 保存文件为 template.docx
            destinationFile = new File(directory, "template.docx");
        }

        // 确保目录存在
        File parentDir = destinationFile.getParentFile();
        if (!parentDir.exists() && !parentDir.mkdirs()) {
            return  ResponseResult.error(CustomExceptionType.SYSTEM_ERROR.getCode(), "创建新文件夹失败");
        }

        try (InputStream inputStream = file.getInputStream();
             OutputStream outputStream = new FileOutputStream(destinationFile)) {

            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            return ResponseResult.success("文件上传成功!");
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseResult.error(CustomExceptionType.SYSTEM_ERROR.getCode(), "文件上传失败!");
        }
    }

    @ApiOperation("获取个人报告模版")
    @RequestMapping(value = "/report/self",method = RequestMethod.POST)
    public ResponseResult selfReport(){
        String report = templateCreateService.selfReport();
        return ResponseResult.success(report);
    }

}
