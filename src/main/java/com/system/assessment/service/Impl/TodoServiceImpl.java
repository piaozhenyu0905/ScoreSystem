package com.system.assessment.service.Impl;

import com.github.pagehelper.PageHelper;
import com.system.assessment.constants.Guideline;
import com.system.assessment.constants.OperationType;
import com.system.assessment.constants.PathConstants;
import com.system.assessment.mapper.EvaluateMapper;
import com.system.assessment.mapper.RelationshipMapper;
import com.system.assessment.mapper.TodoListMapper;
import com.system.assessment.mapper.UserMapper;
import com.system.assessment.pojo.TodoList;
import com.system.assessment.service.EmailService;
import com.system.assessment.service.TodoService;
import com.system.assessment.utils.AuthenticationUtil;
import com.system.assessment.utils.MathUtils;
import com.system.assessment.vo.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class TodoServiceImpl implements TodoService {

    @Value("${spring.profiles.active:dev}") // 默认环境为开发环境
    private String activeProfile;


    @Autowired
    public UserMapper userMapper;

    @Autowired
    public EvaluateMapper evaluateMapper;

    @Autowired
    public TodoListMapper todoListMapper;

    @Autowired
    public RelationshipMapper relationshipMapper;

    @Autowired
    public EmailService emailService;

    @Autowired
    @Lazy
    public AsyncTask asyncTask;

    @Override
    public LastTickScoreList findLastTick(Long taskId) {
        Integer newEpoch = evaluateMapper.findNewEpoch();
        if(newEpoch == 0 || newEpoch == null){
            newEpoch = 1;
        }
        List<Double> lastTick = todoListMapper.findLastTick(newEpoch, taskId);
        LastTickScoreList lastTickScoreList = new LastTickScoreList();
        lastTickScoreList.setScoreList(lastTick);

        return lastTickScoreList;
    }

    @Override
    public void noticeAllNotCompleted() {
        asyncTask.noticeAllNotCompleted();
    }

    @Override
    public Boolean isValued(long todoListId) {
        Integer operation = todoListMapper.isValued(todoListId);
        return  operation != OperationType.INVALUED.getCode();
    }

    @Override
    public List<AssessmentHistoryVO> findHistory(Integer userId) {
        List<AssessmentHistoryVO> histories = todoListMapper.findHistory(userId);
        histories.forEach(history->{
            history.postHandle();
        });
        return histories;
    }

    @Override
    public ScoreResult findScoreResult(Long taksId) {
        ScoreResult scoreResult = todoListMapper.findScoreResult(taksId);
        if(scoreResult != null){
            scoreResult.postHandle();
        }
        return scoreResult;
    }

    @Override
    public Integer addTodoList(TodoList todoList) {
        return todoListMapper.addTodoList(todoList);
    }

    public List<TodoListVO> findTodoList(Integer userId, Integer pageNum, Integer pageSize){
        //分页设置
        Integer newEpoch = evaluateMapper.findNewEpoch();
        PageHelper.startPage(pageNum, pageSize);
        return todoListMapper.findTodoList(userId, newEpoch);
    }

    @Override
    public void exportEvaluatedInfo(HttpServletResponse response) {
        Integer userId = AuthenticationUtil.getUserId();

        List<AssessorVO> evaluatedList = relationshipMapper.findEvaluatedById(userId);

        if(evaluatedList == null){
            evaluatedList = new ArrayList<>();
        }

        String savePath;
        File destinationFile = null;

        // 模板文件路径
        if ("prd".equals(activeProfile)) {
            savePath = PathConstants.EXCEL_FOLDER ; // 生产环境路径
            destinationFile = new File(savePath,  "tick.xlsx");
        } else {
            ClassPathResource resource = new ClassPathResource("static/template");
            File directory = null;
            try {
                directory = resource.getFile();
                // 保存文件为 template.docx
                destinationFile = new File(directory, "tick.xlsx");
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        FileInputStream fis = null;
        try {
            fis = new FileInputStream(destinationFile);
            Workbook workbook = new XSSFWorkbook(fis);
            // 假设数据写入第一个 Sheet
            Sheet sheet = workbook.getSheetAt(0);
            Row styleRow = sheet.getRow(0);

            int rowIndex = 1;
            for (int i = 0; i < evaluatedList.size(); i++) {
                AssessorVO assessorVO = evaluatedList.get(i);
                // 获取或创建行
                Row row = sheet.getRow(rowIndex);
                if (row == null) {
                    row = sheet.createRow(rowIndex);
                }

                // 按表头顺序填写单元格并设置样式
                createCellWithStyle(row, 0, assessorVO.getUsername(), styleRow.getCell(0), workbook); // ID
                createCellWithStyle(row, 1, assessorVO.getDepartment(), styleRow.getCell(1), workbook); // Name

                // 下一行
                rowIndex++;
            }

            // 设置文件下载响应头
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            String fileName = URLEncoder.encode("导出待评分列表.xlsx", "UTF-8");
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName);

            // 写入数据到响应流
            workbook.write(response.getOutputStream());
            workbook.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void exportTick(HttpServletResponse response){

        Integer userId = AuthenticationUtil.getUserId();
        Integer newEpoch = evaluateMapper.findNewEpoch();
        List<ExportTickHistoryVO> exportTickHistoryVOS = todoListMapper.exportTickHistory(newEpoch, userId);

        if(exportTickHistoryVOS == null){
            exportTickHistoryVOS = new ArrayList<>();
        }

        String savePath;
        File destinationFile = null;

        // 模板文件路径
        if ("prd".equals(activeProfile)) {
            savePath = PathConstants.EXCEL_FOLDER ; // 生产环境路径
            destinationFile = new File(savePath,  "tick.xlsx");
        } else {
            ClassPathResource resource = new ClassPathResource("static/template");
            File directory = null;
            try {
                directory = resource.getFile();
                // 保存文件为 template.docx
                destinationFile = new File(directory, "tick.xlsx");
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        FileInputStream fis = null;
        try {
            fis = new FileInputStream(destinationFile);
            Workbook workbook = new XSSFWorkbook(fis);
            // 假设数据写入第一个 Sheet
            Sheet sheet = workbook.getSheetAt(0);

            Row styleRow = sheet.getRow(0);

            int rowIndex = 1;
            for (int i = 0; i < exportTickHistoryVOS.size(); i++) {
                ExportTickHistoryVO exportTickHistoryVO = exportTickHistoryVOS.get(i);
                // 获取或创建行
                Row row = sheet.getRow(rowIndex);
                if (row == null) {
                    row = sheet.createRow(rowIndex);
                }
                ArrayList<Double> scores = new ArrayList<>(Collections.nCopies(Guideline.values().length, 0.0));
                Double totalScore = 0.0;
                List<ScoreVO> scoreList = exportTickHistoryVO.getScoreList();
                for (int index = 0; index < scoreList.size(); index++){
                    ScoreVO scoreVO = scoreList.get(index);
                    Integer dimensionIdx = scoreVO.getDimensionId() - 1;
                    Double score = scoreVO.getScore();
                    scores.set(dimensionIdx, score);

                    totalScore = totalScore + score;
                }
                totalScore = MathUtils.transformer(totalScore, 1);

                // 按表头顺序填写单元格并设置样式
                createCellWithStyle(row, 0, exportTickHistoryVO.getEvaluatedName(), styleRow.getCell(0), workbook); // ID
                createCellWithStyle(row, 1, exportTickHistoryVO.getDepartment(), styleRow.getCell(1), workbook); // Name
                createCellWithStyle(row, 2, exportTickHistoryVO.getCompleteTime(), styleRow.getCell(2), workbook); // Department
                createCellWithStyle(row, 3, totalScore, styleRow.getCell(3), workbook); //
                createCellWithStyle(row, 4, scores.get(0), styleRow.getCell(4), workbook); //
                createCellWithStyle(row, 5, scores.get(1), styleRow.getCell(5), workbook); //
                createCellWithStyle(row, 6, scores.get(2), styleRow.getCell(6), workbook); //
                createCellWithStyle(row, 7, scores.get(3), styleRow.getCell(7), workbook); //
                createCellWithStyle(row, 8, scores.get(4), styleRow.getCell(8), workbook); //
                createCellWithStyle(row, 9, scores.get(5), styleRow.getCell(9), workbook); //
                createCellWithStyle(row, 10, scores.get(6), styleRow.getCell(10), workbook); //
                createCellWithStyle(row, 11, scores.get(7), styleRow.getCell(11), workbook); //
                createCellWithStyle(row, 12, scores.get(8), styleRow.getCell(12), workbook); //
                // 下一行
                rowIndex++;
            }

            // 设置文件下载响应头
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            String fileName = URLEncoder.encode("个人打分情况导出表格.xlsx", "UTF-8");
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName);

            // 写入数据到响应流
            workbook.write(response.getOutputStream());
            workbook.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private  void createCellWithStyle(Row row, int columnIndex, Object value, Cell styleCell, Workbook workbook) {
        Cell cell = row.createCell(columnIndex);

        // 设置单元格值
        if (value instanceof Double) {
            cell.setCellValue((Double) value);
        } else if (value instanceof String) {
            cell.setCellValue((String) value);
        } else if(value instanceof LocalDateTime){
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String formattedDateTime = ((LocalDateTime) value).format(formatter);
            cell.setCellValue(formattedDateTime);
        }

        // 创建新的单元格样式
        CellStyle newStyle = workbook.createCellStyle();

        // 复制原有样式（如果有）
        if (styleCell != null) {
            newStyle.cloneStyleFrom(styleCell.getCellStyle());
        }

        // 创建字体，并设置为 Times New Roman
        Font font = workbook.createFont();
        font.setFontName("Times New Roman");

        // 将字体应用到单元格样式
        newStyle.setFont(font);
        cell.setCellStyle(newStyle);

    }



}
