package com.system.assessment.service.Impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.system.assessment.constants.OperationType;
import com.system.assessment.constants.PathConstants;
import com.system.assessment.constants.RelationType;
import com.system.assessment.constants.TaskType;
import com.system.assessment.exception.CustomException;
import com.system.assessment.exception.CustomExceptionType;
import com.system.assessment.mapper.EvaluateMapper;
import com.system.assessment.mapper.RelationshipMapper;
import com.system.assessment.mapper.TodoListMapper;
import com.system.assessment.mapper.UserMapper;
import com.system.assessment.pojo.EvaluateRelationship;
import com.system.assessment.pojo.TodoList;
import com.system.assessment.pojo.User;
import com.system.assessment.service.ExcelService;
import com.system.assessment.service.RelationshipService;
import com.system.assessment.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class RelationshipServiceImpl implements RelationshipService {
    @Value("${spring.profiles.active:dev}") // 默认环境为开发环境
    private String activeProfile;

    @Autowired
    public RelationshipMapper relationshipMapper;

    @Autowired
    public EvaluateMapper evaluateMapper;

    @Autowired
    public UserMapper userMapper;

    @Autowired
    public TodoListMapper todoListMapper;

    @Autowired
    public ExcelService excelService;


    @Override
    public Integer findSingleRelationship(Integer evaluatorId, Integer evaluatedId) {
        Integer newEpoch = evaluateMapper.findNewEpoch();
        if(newEpoch == null){
            newEpoch = 1;
        }
        return relationshipMapper.findSingleRelationship(evaluatorId, evaluatedId);
    }

    @Override
    public Integer addFixedRelationshipById(Integer evaluatorId, Integer evaluatedId) {
        Integer epoch = evaluateMapper.findNewEpoch();
        if(epoch == null){
            epoch = 1;
        }
        EvaluateRelationship evaluateRelationship = new EvaluateRelationship();
        evaluateRelationship.setEnable(1);
        evaluateRelationship.setEpoch(epoch);
        evaluateRelationship.setEvaluateType(RelationType.fixed.getDescription());
        evaluateRelationship.setEvaluator(evaluatorId);
        evaluateRelationship.setEvaluatedUser(evaluatedId);
        relationshipMapper.addRelationship(evaluateRelationship);
        return 1;
    }

    @Override
    public Integer addFixedRelationship(String evaluatorName, String evaluatedName) {
        Integer evaluatorId = userMapper.findIdByNameAndIsDelete(evaluatorName);
        Integer evaluatedId = userMapper.findIdByNameAndIsDelete(evaluatedName);
        Integer epoch = evaluateMapper.findNewEpoch();
        if(epoch == null){
            epoch = 1;
        }
        EvaluateRelationship evaluateRelationship = new EvaluateRelationship();
        evaluateRelationship.setEnable(1);
        evaluateRelationship.setEpoch(epoch);
        evaluateRelationship.setEvaluateType(RelationType.fixed.getDescription());
        evaluateRelationship.setEvaluator(evaluatorId);
        evaluateRelationship.setEvaluatedUser(evaluatedId);
        relationshipMapper.addRelationship(evaluateRelationship);
        return 1;
    }

    public  String safeGetStringCellValue(Cell evaluatorCell) {
        if (evaluatorCell == null) {
            return "";  // 如果单元格为null，返回空字符串
        }

        // 判断单元格类型
        switch (evaluatorCell.getCellType()) {
            case STRING:
                return evaluatorCell.getStringCellValue().trim();  // 如果是字符串，返回去除空格后的值
            case NUMERIC:
                return String.valueOf(evaluatorCell.getNumericCellValue()).trim();  // 如果是数字，转换为字符串并去除空格
            case BOOLEAN:
                return String.valueOf(evaluatorCell.getBooleanCellValue()).trim();  // 如果是布尔值，转换为字符串并去除空格
            case FORMULA:
                // 如果是公式类型，返回公式的值（可能是字符串、数字等）
                // 你可能需要进一步根据公式的计算结果来处理
                return evaluatorCell.getCellFormula().trim();  // 这里返回公式本身，可能需要进一步的值计算
            default:
                return "";  // 如果是空单元格或其他类型，返回空字符串
        }
    }

    @Override
    public List<AssessorVO> findAssessor(Integer userId) {

        return  relationshipMapper.findAssessor(userId);
    }

    @Override
    public List<RelatedPersonInfoVO> relatedPersonInfo(Integer userId) {
        return relationshipMapper.findRelatedPersonInfo(userId);
    }

    @Override
    public void exportExcel(HttpServletResponse response) {
        ArrayList<RelationshipMatrixVO> relationshipMatrixVOS = new ArrayList<>();
        List<User> allUser = relationshipMapper.findAllUser(null);
        allUser.forEach(user -> {
            RelationshipMatrixVO relationshipMatrixVO = new RelationshipMatrixVO();
            // 1.添加基本信息
            relationshipMatrixVO.setName(user.getName());
            relationshipMatrixVO.setUserId(user.getId());
            relationshipMatrixVO.setSupervisorName1(user.getSupervisorName1());
            relationshipMatrixVO.setSupervisorName2(user.getSupervisorName2());
            relationshipMatrixVO.setSupervisorName3(user.getSupervisorName3());
            relationshipMatrixVO.setHrName(user.getHrName());
            relationshipMatrixVO.setFirstAdminName(user.getFirstAdminName());
            relationshipMatrixVO.setSecondAdminName(user.getSecondAdminName());
            relationshipMatrixVO.setSuperAdminName(user.getSuperAdminName());
            relationshipMatrixVO.setBusiness(user.getBusinessType());
            relationshipMatrixVO.setLxyz(user.getLxyz());
            relationshipMatrixVO.setDepartment(user.getDepartment());
            relationshipMatrixVO.setWeight1(user.getWeight1());
            relationshipMatrixVO.setWeight2(user.getWeight2());
            relationshipMatrixVO.setWeight3(user.getWeight3());
            // 2.查找固定评估人
            List<RelationshipEvaluatorInfo> fixedList = relationshipMapper.findEvaluatorById(user.getId(), RelationType.fixed.getCode());
            String relatedPerson = fixedList.stream()
                    .map(info -> (info.name != null ? info.name : "") + "/" + (info.workNum != null ? info.workNum : ""))
                    .collect(Collectors.joining(";"));;

            relationshipMatrixVO.setRelatedPerson(relatedPerson);
            relationshipMatrixVOS.add(relationshipMatrixVO);
        });

        String savePath;
        File destinationFile = null;

        // 模板文件路径
        if ("prd".equals(activeProfile)) {
            savePath = PathConstants.EXCEL_FOLDER ; // 生产环境路径
            destinationFile = new File(savePath,  "export.xlsx");
        } else {
            ClassPathResource resource = new ClassPathResource("static/template");
            File directory = null;
            try {
                directory = resource.getFile();
                // 保存文件为 template.docx
                destinationFile = new File(directory, "export.xlsx");
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

            // 获取第二行（索引为 3，因为行号从 0 开始）
            Row styleRow = sheet.getRow(3);
            if (styleRow == null) {
                throw new IllegalArgumentException("模板中的第四行不存在，无法应用颜色");
            }

            int rowIndex = 4;
            for (int i = 0; i < relationshipMatrixVOS.size(); i++) {
                RelationshipMatrixVO user = relationshipMatrixVOS.get(i);
                // 获取或创建行
                Row row = sheet.getRow(rowIndex);
                if (row == null) {
                    row = sheet.createRow(rowIndex);
                }

                // 按表头顺序填写单元格并设置样式
                createCellWithStyle(row, 0, user.getName(), styleRow.getCell(0), workbook); // ID
                createCellWithStyle(row, 1, user.getDepartment(), styleRow.getCell(1), workbook); // Name
                createCellWithStyle(row, 2, user.getLxyz(), styleRow.getCell(2), workbook); // Department
                createCellWithStyle(row, 3, user.getBusiness(), styleRow.getCell(3), workbook); //
                createCellWithStyle(row, 4, user.getSupervisorName1(), styleRow.getCell(4), workbook); //
                createCellWithStyle(row, 5, user.getWeight1(), styleRow.getCell(5), workbook); //
                createCellWithStyle(row, 6, user.getSupervisorName2(), styleRow.getCell(6), workbook); //
                createCellWithStyle(row, 7, user.getWeight2(), styleRow.getCell(7), workbook); //
                createCellWithStyle(row, 8, user.getSupervisorName3(), styleRow.getCell(8), workbook); //
                createCellWithStyle(row, 9, user.getWeight3(), styleRow.getCell(9), workbook); //
                createCellWithStyle(row, 10, user.getHrName(), styleRow.getCell(10), workbook); //
                createCellWithStyle(row, 11, user.getRelatedPerson(), styleRow.getCell(11), workbook); //
                createCellWithStyle(row, 12, user.getSuperAdminName(), styleRow.getCell(12), workbook); //
                createCellWithStyle(row, 13, user.getFirstAdminName(), styleRow.getCell(13), workbook); //
                createCellWithStyle(row, 14, user.getSecondAdminName(), styleRow.getCell(14), workbook); //
                createCellWithStyle(row, 15, null, styleRow.getCell(15), workbook); //
                // 下一行
                rowIndex++;
            }

            // 设置文件下载响应头
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            String fileName = URLEncoder.encode("矩阵关系导出表格.xlsx", "UTF-8");
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
        }

        // 复制样式
        if (styleCell != null) {
            CellStyle newStyle = workbook.createCellStyle();
            newStyle.cloneStyleFrom(styleCell.getCellStyle()); // 克隆样式
            cell.setCellStyle(newStyle);
        }
    }


    @Override
    public ImportINfo addRelationshipExcel(MultipartFile file) {
        ImportINfo importINfo = new ImportINfo();
        Integer success = 0;
        try {
            // 解析 Excel 文件
            Workbook workbook = new XSSFWorkbook(file.getInputStream());
            Sheet sheet = workbook.getSheetAt(0);  // 获取第一个工作表
            Row headerRow = sheet.getRow(2); //获取第三行的表头信息
            Map<String, Integer> columnIndexMap = new HashMap<>(); // 存储每个列名和它的索引

            List<String> errorList = new ArrayList<>();

            // 动态获取表头的列索引
            for (Cell cell : headerRow) {
                String header = cell.getStringCellValue().trim();
                if (header.contains("\n")) {
                    // 使用 split 提取 \n 前的部分
                    String[] parts = header.split("\n");
                    columnIndexMap.put(parts[0].trim(), cell.getColumnIndex());
                } else {
                    // 如果没有 \n，直接添加原字符串
                    columnIndexMap.put(header, cell.getColumnIndex());
                }
            }
            //验证用户传入的表格是否符合用户导入的格式
            if(columnIndexMap.get("被评估人") == null || columnIndexMap.get("相关人") == null){
                log.error("用户导入的文件内容有误!");
                throw new CustomException(CustomExceptionType.USER_INPUT_ERROR, "用户导入的文件内容有误!");
            }
            for (int i = 3; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row != null) {
                    String evaluatedInfo = getCellValue(row, columnIndexMap.get("被评估人"));
                    try {
                        excelService.addRelationshipExcel(row, columnIndexMap);
                        log.info(evaluatedInfo + "导入成功!");
                        success = success + 1;
                    }catch (Exception e){
                        log.error(evaluatedInfo + "导入失败!");
                        errorList.add(evaluatedInfo);
                    }

                }
            }
            importINfo.setSuccess(success);
            importINfo.setErrorList(errorList);
            return importINfo;

        }catch (IOException e) {
            log.error("矩阵关系导入有误!");
            return null;
        } catch (IllegalStateException e){
            log.error("文件格式或内容不符合要求!");
            return null;
        }
    }

    @Override
    @Transactional
    public Integer addRelationshipByFile(MultipartFile file) {
        Map<Integer, String> evaluatedNameMap = new HashMap();
        //先将库中存在的固定评估关系删除
        relationshipMapper.deleteFixedRelationship();
        try {
             Workbook workbook = new XSSFWorkbook(file.getInputStream());

            Sheet sheet = workbook.getSheetAt(0);  // 获取第一个工作表
            Row headerRow = sheet.getRow(0);
            Cell cell = headerRow.getCell(0);
            String testCell = cell.getStringCellValue().trim();
            if(!testCell.equals("评估人")){
                log.error("用户导入的文件内容有误!");
                throw new CustomException(CustomExceptionType.USER_INPUT_ERROR, "用户导入的文件内容有误!");
            }

            Row evaluatedNameRow = sheet.getRow(2);  // 第二行包含被评估人的名称

            Set<String> evaluatedNameSet = new HashSet<>();
            Set<String> evaluatorNameSet = new HashSet<>();

            for (int j = 1; j < evaluatedNameRow.getLastCellNum(); j++){
                Cell evaluatedCell = evaluatedNameRow.getCell(j);
                if(evaluatedCell == null){
                    continue;
                }

                String evaluatedName = safeGetStringCellValue(evaluatedCell);
                //判断是否出现了两次一样的名字
                if(evaluatedNameSet.contains(evaluatedName)){
                    String error = "被评估人中，名字["+evaluatedName+"]出现了两次!";
                    log.error(error);
                    throw new CustomException(CustomExceptionType.USER_INPUT_ERROR, error);
                }else {
                    evaluatedNameSet.add(evaluatedName);
                }

                //需要先判断该被评估人是否在库中，若不在，则跳过；若在，加入到集合中
                User user = userMapper.findValuedUserByName(evaluatedName);
                if(user == null)
                    continue;
                evaluatedNameMap.put(j, evaluatedName);
            }

            // 读取每一行，构造关系
            for (int i = 3; i <= sheet.getLastRowNum(); i++) { // 从第三行开始
                Row row = sheet.getRow(i);
                if (row == null)
                    continue;
                Cell evaluatorCell = row.getCell(0);
                if (evaluatorCell == null)
                    continue;
                String evaluatorName = safeGetStringCellValue(evaluatorCell);

                //判断是否出现了两次一样的名字
                if(evaluatorNameSet.contains(evaluatorName)){
                    String error = "评估人中，名字["+evaluatorName+"]出现了两次!";
                    log.error(error);
                    throw new CustomException(CustomExceptionType.USER_INPUT_ERROR, error);
                }else {
                    evaluatorNameSet.add(evaluatorName);
                }

                //先判断该人是否在数据库中，若不在，则直接跳过该行。
                User evaluatorUser = userMapper.findValuedUserByName(evaluatorName);
                if(evaluatorUser == null)
                    continue;
                for (int index = 1; index < row.getLastCellNum(); index++) { // 从第二列开始
                    Cell relationshipCell = row.getCell(index);
                    if (relationshipCell != null && relationshipCell.getCellType() == CellType.NUMERIC) {
                        double cellValue = relationshipCell.getNumericCellValue();
                        // 检查单元格的值是否为1
                        if (cellValue == 1.0) {
                           //若为1，则证明两人存在关系；接着，在evaluatedNameMap查找，其对应的被评估人是否在库中，若在Map中查不到，则证明该被评估人不合规，关系不予建立
                            String evaluatedPreConfirmed = evaluatedNameMap.get(index);
                            if(evaluatedPreConfirmed == null){
                                //该被评估人不在map中，跳过
                                continue;
                            }else if(evaluatedPreConfirmed.equals(evaluatorName)) {
                                continue;
                            }else {
                                //该被评估人在集合中，建立关系
                                addFixedRelationship(evaluatorName, evaluatedPreConfirmed);
                            }
                        }
                    }
                }

            }
        }catch (IOException e) {
            log.error("矩阵关系导入有误!");
            throw new CustomException(CustomExceptionType.USER_INPUT_ERROR, "矩阵关系导入有误");
        } catch (IllegalStateException e){
            log.error("文件格式或内容不符合要求!");
            throw new CustomException(CustomExceptionType.USER_INPUT_ERROR, "文件格式或内容不符合要求");
        }
        return 1;
    }



    @Override
    public List<Integer> findAllEvaluated() {
        return relationshipMapper.findAllEvaluated();
    }

    @Override
    @Transactional
    public Integer addEvaluationMatrixAtSecondStage(FixEvaluatedAddingVO fixEvaluatedAddingVO) {
        Integer newEpoch = evaluateMapper.findNewEpoch();
        Integer userId = fixEvaluatedAddingVO.getUserId();
        for (int index = 0; index < fixEvaluatedAddingVO.getEvaluatorIds().size(); index++){
            Integer evaluatorId = fixEvaluatedAddingVO.getEvaluatorIds().get(index);
            //1.添加关系到矩阵
            addEvaluationMatrix(userId, evaluatorId);
            //2.添加新的打分任务
            TodoListVO todoListIsExist = todoListMapper.findTodoListIsExist(evaluatorId, userId, newEpoch);
            //2.1 系统中找不到对应关系的[未完成]的任务，则添加新任务
            if(todoListIsExist == null){
                //添加新任务
                addNewTask(evaluatorId, userId, newEpoch);
            }
        }

        return 1;
    }

    public Integer addNewTask(Integer evaluatorId, Integer evaluatedId, Integer epoch){
        TodoList todoList = new TodoList();
        todoList.setType(TaskType.SurroundingEvaluation.getDescription());
        todoList.setPresentDate(LocalDateTime.now());
        todoList.setOperation(OperationType.NOTFINISHED.getCode());
        todoList.setRejectReason(null);
        todoList.setOwnerId(evaluatorId);
        todoList.setCompleteTime(null);
        todoList.setEvaluatorId(evaluatorId);
        todoList.setEvaluatedId(evaluatedId);
        String evaluatedName = userMapper.findNameByUserId(evaluatedId);
        String evaluatedWorkNum = userMapper.findWorkNumById(evaluatedId);
        String evaluatorName = userMapper.findNameByUserId(evaluatorId);
        String  evaluatorWorkNum = userMapper.findWorkNumById(evaluatorId);
        todoList.setEvaluatorName(evaluatorName + "/" +evaluatorWorkNum);
        todoList.setEvaluatedName(evaluatedName + "/" +evaluatedWorkNum);
        todoList.setEnable(0); //分数设置待确认状态
        todoList.setEpoch(epoch);
        todoList.setConfidenceLevel(1.0);
        String Detail = "请完成对" + evaluatedName + "/" +evaluatedWorkNum + "的周边评议";
        todoList.setDetail(Detail);
        todoListMapper.addTodoList(todoList);
        return 1;
    }

    @Override
    @Transactional
    public Integer addEvaluationMatrix(FixEvaluatedAddingVO fixEvaluatedAddingVO) {
        Integer userId = fixEvaluatedAddingVO.getUserId();
        for (int index = 0; index < fixEvaluatedAddingVO.getEvaluatorIds().size(); index++){
            Integer evaluatorId = fixEvaluatedAddingVO.getEvaluatorIds().get(index);
            addEvaluationMatrix(userId, evaluatorId);
        }
        return 1;
    }

    @Override
    public Integer addEvaluationMatrix(Integer userId, Integer evaluatorId) {

        Integer newEpoch = evaluateMapper.findNewEpoch();
        if(newEpoch == null){
            newEpoch = 1;
        }
        EvaluateRelationship evaluateRelationship = new EvaluateRelationship();
        evaluateRelationship.setEvaluateType(RelationType.fixed.getDescription());
        evaluateRelationship.setEvaluator(evaluatorId);
        evaluateRelationship.setEvaluatedUser(userId);
        evaluateRelationship.setEnable(1);
        evaluateRelationship.setEpoch(newEpoch);
        return relationshipMapper.addRelationship(evaluateRelationship);
    }

    @Override
    public Integer deleteEvaluationMatrix(Integer userId, Integer evaluatorId) {
        return relationshipMapper.deleteEvaluation(userId, evaluatorId, RelationType.fixed.getCode());
    }

    @Override
    @Transactional
    public Integer deleteEvaluationMatrixAtSecondStage(Integer userId, Integer evaluatorId) {
        Integer newEpoch = evaluateMapper.findNewEpoch();
        //1.删除关系
        relationshipMapper.deleteEvaluationMatrix(userId, evaluatorId, RelationType.fixed.getCode());
        //2.若已完成则将打分标志置为“已删除”
        todoListMapper.setFinishedOperationToDeleted(evaluatorId, userId, OperationType.FINISHEDANDDELETED.getCode(),newEpoch);
        // 若未完成则delete
        todoListMapper.DeleteUnFinished(evaluatorId, userId, OperationType.NOTFINISHED.getCode(),newEpoch);
        return 1;
    }

    public String RelatedToStr(List<RelationshipEvaluatorInfo> fixedList){
        // 初始化 StringBuilder 用于高效拼接字符串
        StringBuilder relatedPersonBuilder = new StringBuilder();

        // 检查 fixedList 是否为 null
        if (fixedList == null) {
            return ""; // 根据业务需求返回空字符串或其他默认值
        }

        for (int i = 0; i < fixedList.size(); i++) {
            RelationshipEvaluatorInfo info = fixedList.get(i);

            // 检查 info 是否为 null
            if (info == null) {
                continue; // 跳过当前循环，继续处理下一个元素
            }

            // 获取 name 和 workNum，处理可能的 null 值
            String name = info.name != null ? info.name : "";
            String workNum = info.workNum != null ? info.workNum : "";

            // 拼接 name 和 workNum
            String combined = name + "/" + workNum;

            // 将拼接后的字符串添加到 StringBuilder
            relatedPersonBuilder.append(combined);

            // 如果不是最后一个元素，添加分号分隔符
            if (i < fixedList.size() - 1) {
                relatedPersonBuilder.append(";");
            }
        }
        // 将 StringBuilder 转换为 String
        String relatedPerson = relatedPersonBuilder.toString();
        return relatedPerson;
    };

    @Override
    @Transactional
    public DataListResult findEvaluationMatrix(EvaluationRelationshipVO evaluationRelationshipVO) {

        ArrayList<RelationshipMatrixVO> relationshipMatrixVOS = new ArrayList<>();
        String name = evaluationRelationshipVO.getName();
        DataListResult<RelationshipMatrixVO> objectDataListResult = new DataListResult<>();

        if(name != null && !name.equals("")){
            List<User> allUser = relationshipMapper.findAllUser(name);

            allUser.forEach(user -> {
                RelationshipMatrixVO relationshipMatrixVO = new RelationshipMatrixVO();
                // 1.添加基本信息
                relationshipMatrixVO.setName(user.getName());
                relationshipMatrixVO.setUserId(user.getId());
                relationshipMatrixVO.setSupervisorName1(user.getSupervisorName1());
                relationshipMatrixVO.setSupervisorName2(user.getSupervisorName2());
                relationshipMatrixVO.setSupervisorName3(user.getSupervisorName3());
                relationshipMatrixVO.setHrName(user.getHrName());
                relationshipMatrixVO.setFirstAdminName(user.getFirstAdminName());
                relationshipMatrixVO.setSecondAdminName(user.getSecondAdminName());
                relationshipMatrixVO.setSuperAdminName(user.getSuperAdminName());
                relationshipMatrixVO.setBusiness(user.getBusinessType());
                relationshipMatrixVO.setLxyz(user.getLxyz());
                relationshipMatrixVO.setDepartment(user.getDepartment());
                relationshipMatrixVO.setWeight1(user.getWeight1());
                relationshipMatrixVO.setWeight2(user.getWeight2());
                relationshipMatrixVO.setWeight3(user.getWeight3());
                // 2.查找相关人
                List<RelationshipEvaluatorInfo> fixedList = relationshipMapper.findEvaluatorById(user.getId(), RelationType.fixed.getCode());
                String relatedPerson = RelatedToStr(fixedList);
                relationshipMatrixVO.setRelatedPerson(relatedPerson);
                relationshipMatrixVOS.add(relationshipMatrixVO);
            });

            objectDataListResult.setTotal(1);
            objectDataListResult.setData(relationshipMatrixVOS);

        }else {
            Integer pageNum = evaluationRelationshipVO.getPageNum();
            Integer pageSize = evaluationRelationshipVO.getPageSize();
            PageHelper.startPage(pageNum, pageSize);
            List<User> allUser = relationshipMapper.findAllUser(null);
            PageInfo<User> pageInfo = new PageInfo<>(allUser);
            long total = pageInfo.getTotal(); // 获取总记录数

            PageHelper.clearPage();
            allUser.forEach(user -> {
                RelationshipMatrixVO relationshipMatrixVO = new RelationshipMatrixVO();
                // 1.添加基本信息
                relationshipMatrixVO.setName(user.getName());
                relationshipMatrixVO.setUserId(user.getId());
                relationshipMatrixVO.setSupervisorName1(user.getSupervisorName1());
                relationshipMatrixVO.setSupervisorName2(user.getSupervisorName2());
                relationshipMatrixVO.setSupervisorName3(user.getSupervisorName3());
                relationshipMatrixVO.setHrName(user.getHrName());
                relationshipMatrixVO.setFirstAdminName(user.getFirstAdminName());
                relationshipMatrixVO.setSecondAdminName(user.getSecondAdminName());
                relationshipMatrixVO.setSuperAdminName(user.getSuperAdminName());
                relationshipMatrixVO.setBusiness(user.getBusinessType());
                relationshipMatrixVO.setLxyz(user.getLxyz());
                relationshipMatrixVO.setDepartment(user.getDepartment());
                relationshipMatrixVO.setWeight1(user.getWeight1());
                relationshipMatrixVO.setWeight2(user.getWeight2());
                relationshipMatrixVO.setWeight3(user.getWeight3());
                // 2.查找固定评估人
                List<RelationshipEvaluatorInfo> fixedList = relationshipMapper.findEvaluatorById(user.getId(), RelationType.fixed.getCode());
                String relatedPerson = RelatedToStr(fixedList);
                relationshipMatrixVO.setRelatedPerson(relatedPerson);
                relationshipMatrixVOS.add(relationshipMatrixVO);
            });
            objectDataListResult.setTotal(total);
            objectDataListResult.setData(relationshipMatrixVOS);
        }

        return objectDataListResult;
    }

    @Override
    @Transactional
    public Integer addSelfEvaluated(AddSelfEvaluatedVO addSelfEvaluatedVO) {

        Integer newEpoch = evaluateMapper.findNewEpoch();
        if(newEpoch == null){
            newEpoch = 1;
        }
        List<Integer> userIds = addSelfEvaluatedVO.getUserIds();
        if(userIds == null){
            return 1;
        }
        Integer existSize = 0;
        List<EvaluateRelationship> evaluateRelationshipsList = new ArrayList<>();

        Integer selfId = addSelfEvaluatedVO.getSelfId();
        List<SelfEvaluatedVO> selfList = new ArrayList<>();
        //查出所有以本人未评估人的评估关系
        List<SelfEvaluatedVO> allRelationshipList = evaluateMapper.findSelfEvaluated(selfId);
        if(allRelationshipList != null){
            for (int i=0; i < allRelationshipList.size();i++){
                SelfEvaluatedVO selfEvaluatedVO = allRelationshipList.get(i);
                //收集所有自主评估关系
                if(selfEvaluatedVO.getType().equals(RelationType.self.getDescription())){
                    selfList.add(selfEvaluatedVO);
                }
            }
            existSize = selfList.size();
            if(existSize >= 3){
                throw new CustomException(CustomExceptionType.USER_INPUT_ERROR, "您已和至少三位同事建立了自主评估关系，无法再选择自主评估人!");
            }
        }


        for (int index = 0; index < userIds.size(); index++){
            Integer userId = userIds.get(index);
            //1. 判断选择的人中是否包含自己
            if(userId.equals(selfId)){
                throw new CustomException(CustomExceptionType.USER_INPUT_ERROR, "您无法与自身建立评估关系，请重新选择!");
            }
            //2. 判断要添加的被评估人是否已经和该用户建立了关系
            if(allRelationshipList != null){
                for (int idx = 0; idx < allRelationshipList.size(); idx++){
                    Integer evaluatedUser = allRelationshipList.get(idx).getEvaluatedUser();
                    String name = allRelationshipList.get(idx).getName();
                    if(userId.equals(evaluatedUser)){
                        throw new CustomException(CustomExceptionType.USER_INPUT_ERROR, "您已和" + name +"建立了评估关系，请重新选择!");
                    }
                }
            }
            EvaluateRelationship evaluateRelationship = new EvaluateRelationship();
            evaluateRelationship.setEvaluateType(RelationType.self.getDescription());
            evaluateRelationship.setEvaluator(addSelfEvaluatedVO.getSelfId());
            evaluateRelationship.setEvaluatedUser(userIds.get(index));
            evaluateRelationship.setEpoch(newEpoch);
            evaluateRelationship.setEnable(1);

            if(existSize >= 3){
                throw new CustomException(CustomExceptionType.USER_INPUT_ERROR, "您选择的自主评估人数量超过了三位，请重新选择!");
            }
            existSize = existSize + 1;
            evaluateRelationshipsList.add(evaluateRelationship);

        }

        for (int idx = 0; idx < evaluateRelationshipsList.size(); idx++){
            EvaluateRelationship evaluateRelationship = evaluateRelationshipsList.get(idx);
            relationshipMapper.addRelationship(evaluateRelationship);
        }


        return 1;
    }

    @Override
    @Transactional
    public Integer addSelfEvaluatedAtSecondStage(AddSelfEvaluatedVO addSelfEvaluatedVO) {
        Integer newEpoch = evaluateMapper.findNewEpoch();
        //1.添加自主评估人
        addSelfEvaluated(addSelfEvaluatedVO);
        Integer selfId = addSelfEvaluatedVO.getSelfId();
        List<Integer> evaluatedIds = addSelfEvaluatedVO.getUserIds();
        //2.添加新任务
        for (int index = 0; index < evaluatedIds.size(); index++){
            Integer evalutedId = evaluatedIds.get(index);
            addNewTask(selfId, evalutedId, newEpoch);
        }

        return 1;
    }


    @Override
    public Integer addRelationship(EvaluateRelationship evaluateRelationship) {
        return relationshipMapper.addRelationship(evaluateRelationship);
    }

    // 获取单元格的字符串值，并去除前后空格
    private String getCellValue(Row row, Integer columnIndex) {
        if (columnIndex == null || row == null) return "";

        Cell cell = row.getCell(columnIndex);
        if (cell == null) return "";

        // 根据单元格类型处理返回值
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim(); // 返回字符串值
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString(); // 如果是日期，返回日期字符串
                } else {
                    DecimalFormat df = new DecimalFormat("0.##########"); // 格式化数字，避免科学计数法
                    return df.format(cell.getNumericCellValue());
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                try {
                    return cell.getStringCellValue().trim(); // 优先尝试返回公式结果的字符串
                } catch (IllegalStateException e) {
                    return String.valueOf(cell.getNumericCellValue()); // 如果失败，返回公式计算的数值结果
                }
            case BLANK:
                return ""; // 空白单元格返回空字符串
            default:
                return ""; // 未支持的类型返回空字符串
        }
    }


}
