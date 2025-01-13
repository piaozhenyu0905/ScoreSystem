package com.system.assessment.service.Impl;

import com.github.pagehelper.PageHelper;
import com.system.assessment.constants.*;
import com.system.assessment.exception.CustomException;
import com.system.assessment.exception.CustomExceptionType;
import com.system.assessment.exception.ResponseResult;
import com.system.assessment.mapper.EvaluateMapper;
import com.system.assessment.mapper.RelationshipMapper;
import com.system.assessment.mapper.TodoListMapper;
import com.system.assessment.mapper.UserMapper;
import com.system.assessment.pojo.EvaluateRelationship;
import com.system.assessment.pojo.User;
import com.system.assessment.service.ExcelService;
import com.system.assessment.service.RelationshipService;
import com.system.assessment.service.UserService;
import com.system.assessment.utils.AuthenticationUtil;
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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Value("${spring.profiles.active:dev}") // 默认环境为开发环境
    private String activeProfile;

    @Autowired
    public UserMapper userMapper;

    @Autowired
    public RelationshipMapper relationshipMapper;

    @Autowired
    public UserService userService;

    @Autowired
    public ExcelService excelService;

    @Autowired
    public EvaluateMapper evaluateMapper;

    @Autowired
    public RelationshipService relationshipService;

    @Autowired
    public TodoListMapper todoListMapper;

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
    public void exportExcel(HttpServletResponse response) {

        List<User> allUser = relationshipMapper.exportAllUser();

        String savePath;
        File destinationFile = null;

        // 模板文件路径
        if ("prd".equals(activeProfile)) {
            savePath = PathConstants.EXCEL_FOLDER ; // 生产环境路径
            destinationFile = new File(savePath,  "exportUser.xlsx");
        } else {
            ClassPathResource resource = new ClassPathResource("static/template");
            File directory = null;
            try {
                directory = resource.getFile();
                // 保存文件为 template.docx
                destinationFile = new File(directory, "exportUser.xlsx");
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

            CellStyle cellStyle = workbook.createCellStyle();
            cellStyle.setAlignment(HorizontalAlignment.CENTER);  // 水平居中
            cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);  // 垂直居中


            int rowIndex = 1;
            for (int i = 0; i < allUser.size(); i++) {
                User user = allUser.get(i);
                // 获取或创建行
                Row row = sheet.getRow(rowIndex);
                if (row == null) {
                    row = sheet.createRow(rowIndex);
                }

                // 填写用户信息并应用样式
                row.createCell(0).setCellValue(user.getName());
                row.getCell(0).setCellStyle(cellStyle);  // 设置居中

                row.createCell(1).setCellValue(user.getWorkNum());
                row.getCell(1).setCellStyle(cellStyle);

                row.createCell(2).setCellValue(user.getDepartment());
                row.getCell(2).setCellStyle(cellStyle);

                row.createCell(3).setCellValue(user.getSupervisorName1());
                row.getCell(3).setCellStyle(cellStyle);

                row.createCell(4).setCellValue(user.getSupervisorName2());
                row.getCell(4).setCellStyle(cellStyle);

                row.createCell(5).setCellValue(user.getSupervisorName3());
                row.getCell(5).setCellStyle(cellStyle);

                row.createCell(6).setCellValue(user.getHrName());
                row.getCell(6).setCellStyle(cellStyle);


                LocalDate hireDate = user.getHireDate();
                if (hireDate != null) {
                    CellStyle dateCellStyle = workbook.createCellStyle();
                    dateCellStyle.setAlignment(HorizontalAlignment.CENTER);  // 水平居中
                    dateCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);  // 垂直居中
                    CreationHelper creationHelper = workbook.getCreationHelper();
                    dateCellStyle.setDataFormat(creationHelper.createDataFormat().getFormat("yyyy-MM-dd"));

                    // 设置日期单元格并应用日期格式
                    Cell cell = row.createCell(7);
                    cell.setCellValue(java.sql.Date.valueOf(hireDate));
                    cell.setCellStyle(dateCellStyle);  // 设置日期格式
                }else {
                    row.createCell(7).setCellValue("");
                }

                row.createCell(8).setCellValue(user.getResidence());
                row.getCell(8).setCellStyle(cellStyle);

                row.createCell(9).setCellValue(user.getPhoneNumber());
                row.getCell(9).setCellStyle(cellStyle);

                row.createCell(10).setCellValue(user.getEmail());
                row.getCell(10).setCellStyle(cellStyle);

                row.createCell(11).setCellValue(user.getBusinessType());
                row.getCell(11).setCellStyle(cellStyle);

                row.createCell(12).setCellValue(user.getLxyz());
                row.getCell(12).setCellStyle(cellStyle);

                // 角色处理


                row.createCell(13).setCellValue(user.getSuperAdminName());
                row.getCell(13).setCellStyle(cellStyle);

                row.createCell(14).setCellValue(user.getFirstAdminName());
                row.getCell(14).setCellStyle(cellStyle);

                row.createCell(15).setCellValue(user.getSecondAdminName());
                row.getCell(15).setCellStyle(cellStyle);

                Integer role = user.getRole();
                String roleName = "";
                if (role.equals(Role.superAdmin.getCode())) {
                    roleName = "超级管理员";
                } else if (role.equals(Role.normal.getCode())) {
                    roleName = "普通用户";
                } else if (role.equals(Role.firstAdmin.getCode())) {
                    roleName = "一级管理员";
                } else {
                    roleName = "二级管理员";
                }
                row.createCell(16).setCellValue(roleName);
                row.getCell(16).setCellStyle(cellStyle);


                // 下一行
                rowIndex++;
            }

            // 设置文件下载响应头
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            String fileName = URLEncoder.encode("用户信息导出表格.xlsx", "UTF-8");
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
    @Transactional
    public void updateNewUser(UserVO user) {

        Integer newestEnableProcess = evaluateMapper.findNewestEnableProcess();
        if(newestEnableProcess == null){
            newestEnableProcess = 1;
        }

        Integer newEpoch = evaluateMapper.findNewEpoch();
        if(newEpoch == null){
            newEpoch = 1;
        }

        user.preDo();
        String name = user.getName();
        if(name != null){
            name = name.trim();
        }
        String workNum = user.getWorkNum();
        if(workNum != null){
            workNum = workNum.trim();
        }
        user.setUsername(name + workNum); //用户名默认是姓名+工号
        user.setPassword("00000000");
        user.setResignationDate(null);
        user.setWeight(1.0);   //得分权重

        Integer superVisorNum = 0;
        if(user.getSupervisor1() != 0){
            Integer isDelete = userMapper.judgeUserIsDelete(user.getSupervisor1());
            if(isDelete != null && isDelete == 0){
                superVisorNum = superVisorNum + 1;
            }
        }
        if(user.getSupervisor2() != 0){
            Integer isDelete = userMapper.judgeUserIsDelete(user.getSupervisor2());
            if(isDelete != null && isDelete == 0){
                superVisorNum = superVisorNum + 1;
            }
        }
        if(user.getSupervisor3() != 0){
            Integer isDelete = userMapper.judgeUserIsDelete(user.getSupervisor3());
            if(isDelete != null && isDelete == 0){
                superVisorNum = superVisorNum + 1;
            }
        }

        Double weight;
        if(superVisorNum == 0){
            weight = 0.0;
        }else {
            weight = 0.9 / superVisorNum;
        }

        if(user.getSupervisor1() != 0){
            user.setWeight1(weight); //主管权重
        }else {
            user.setWeight1(-1.0);
        }
        if(user.getSupervisor2() != 0){
            user.setWeight2(weight); //主管权重
        }else {
            user.setWeight2(-1.0);
        }
        if(user.getSupervisor3() != 0){
            user.setWeight3(weight); //主管权重
        }else {
            user.setWeight3(-1.0);
        }

        user.setIsFirstLogin(true); //设置是否为第一次登录，导入后默认为true
        user.setAccountNonExpired(true);
        user.setCredentialsNonExpired(true);
        user.setEnabled(true);
        user.setAccountNonLocked(true);
        user.setIsDelete(false);

        userMapper.addUserVO(user);
        Integer id = user.getId();
        if(user.getSupervisor1() != 0){
            Integer evaluatorId = user.getSupervisor1();
            Integer isDelete = userMapper.judgeUserIsDelete(evaluatorId);
            //判断该主管是否仍有效（未被删除）
            if(isDelete != null && isDelete == 0){
                //第一,三，四阶段，只添加关系 ; 第二阶段，添加关系并新增任务
                Integer singleRelationship = relationshipMapper.findSingleRelationship(evaluatorId, id);
                if(singleRelationship == null){
                    //不存在，则建立固定关系
                    addFixedRelationshipById(evaluatorId, id);
                }
                if(newestEnableProcess.equals(ProcessType.Evaluation.getCode())){
                    //查看任务是否已存在
                    TodoListVO todoListIsExist = todoListMapper.findTodoListIsExist(evaluatorId, id, newEpoch);
                    //系统中找不到对应关系的[未完成]的任务，则添加新任务
                    if(todoListIsExist == null){
                        //添加新任务
                        relationshipService.addNewTask(evaluatorId, id, newEpoch);
                    }
                }
            }
        }
        if(user.getSupervisor2() != 0){
            Integer evaluatorId = user.getSupervisor2();
            Integer isDelete = userMapper.judgeUserIsDelete(evaluatorId);
            //判断该主管是否仍有效（未被删除）
            if(isDelete != null && isDelete == 0){
                //第一,三，四阶段，只添加关系 ; 第二阶段，添加关系并新增任务
                Integer singleRelationship = relationshipMapper.findSingleRelationship(evaluatorId, id);
                if(singleRelationship == null){
                    //不存在，则建立固定关系
                    addFixedRelationshipById(evaluatorId, id);
                }
                if(newestEnableProcess.equals(ProcessType.Evaluation.getCode())){
                    //查看任务是否已存在
                    TodoListVO todoListIsExist = todoListMapper.findTodoListIsExist(evaluatorId, id, newEpoch);
                    //系统中找不到对应关系的[未完成]的任务，则添加新任务
                    if(todoListIsExist == null){
                        //添加新任务
                        relationshipService.addNewTask(evaluatorId, id, newEpoch);
                    }
                }
            }
        }
        if(user.getSupervisor3() != 0){
            Integer evaluatorId = user.getSupervisor3();
            Integer isDelete = userMapper.judgeUserIsDelete(evaluatorId);
            //判断该主管是否仍有效（未被删除）
            if(isDelete != null && isDelete == 0){
                //第一,三，四阶段，只添加关系 ; 第二阶段，添加关系并新增任务
                Integer singleRelationship = relationshipMapper.findSingleRelationship(evaluatorId, id);
                if(singleRelationship == null){
                    //不存在，则建立固定关系
                    addFixedRelationshipById(evaluatorId, id);
                }
                if(newestEnableProcess.equals(ProcessType.Evaluation.getCode())){
                    //查看任务是否已存在
                    TodoListVO todoListIsExist = todoListMapper.findTodoListIsExist(evaluatorId, id, newEpoch);
                    //系统中找不到对应关系的[未完成]的任务，则添加新任务
                    if(todoListIsExist == null){
                        //添加新任务
                        relationshipService.addNewTask(evaluatorId, id, newEpoch);
                    }
                }
            }
        }
        if(user.getHr() != 0){
            Integer evaluatorId = user.getHr();
            Integer isDelete = userMapper.judgeUserIsDelete(evaluatorId);
            //判断该主管是否仍有效（未被删除）
            if(isDelete != null && isDelete == 0){
                //第一,三，四阶段，只添加关系 ; 第二阶段，添加关系并新增任务
                Integer singleRelationship = relationshipMapper.findSingleRelationship(evaluatorId, id);
                if(singleRelationship == null){
                    //不存在，则建立固定关系
                    addFixedRelationshipById(evaluatorId, id);
                }
                if(newestEnableProcess.equals(ProcessType.Evaluation.getCode())){
                    //查看任务是否已存在
                    TodoListVO todoListIsExist = todoListMapper.findTodoListIsExist(evaluatorId, id, newEpoch);
                    //系统中找不到对应关系的[未完成]的任务，则添加新任务
                    if(todoListIsExist == null){
                        //添加新任务
                        relationshipService.addNewTask(evaluatorId, id, newEpoch);
                    }
                }
            }
        }

    }

    @Override
    public ArrayList<String> deleteUsers(DeleteUserVO deleteUserVO) {
        ArrayList<String> errorList = new ArrayList<>();
        for(int index = 0; index < deleteUserVO.getIds().size(); index++){
            Integer id = deleteUserVO.getIds().get(index);
            try {
                userService.deleteUser(id);
            }catch (Exception e){
                User basicInfoBySelfId = findBasicInfoBySelfId(id);
                String name = basicInfoBySelfId.getName();
                errorList.add(name);
                log.error(name +"删除失败!");
            }
        }

        return errorList;
    }

    @Override
    public Integer findRole(Integer id) {
        return userMapper.findRole(id);
    }

    public Boolean RoleIsValued(String role){
        if(role == null || role.equals("")){
            return false;
        }
        if(!role.equals("超级管理员") && !role.equals("一级管理员") && !role.equals("二级管理员") && !role.equals("普通用户")){
            return false;
        }
        return true;
    };

    public Boolean LxyzIsValued(String lxyz){
        if(lxyz == null || lxyz.equals("")){
            return false;
        }
        if(!lxyz.equals("IP") && !lxyz.equals("LP") && !lxyz.equals("中坚") && !lxyz.equals("精英") && !lxyz.equals("成长")){
            return false;
        }
        return true;
    };

    public Boolean BusinessIsValued(String business){
        if(business == null || business.equals("")){
            return false;
        }
        if(!business.equals("研发") && !business.equals("非研发")){
            return false;
        }
        return true;
    };


    @Override
    public ImportVO uploadFile(MultipartFile file) {
        ImportVO importVO = new ImportVO();

        try {
            // 解析 Excel 文件
            Workbook workbook = new XSSFWorkbook(file.getInputStream());
            Sheet sheet = workbook.getSheetAt(0);  // 获取第一个工作表
            Row headerRow = sheet.getRow(0);
            Map<String, Integer> columnIndexMap = new HashMap<>(); // 存储每个列名和它的索引

            // 动态获取表头的列索引
            for (Cell cell : headerRow) {
                String header = cell.getStringCellValue().trim();
                if (header.contains("\n")) {
                    // 使用 split 提取 \n 前的部分
                    String[] parts = header.split("\n");
                    String part = parts[0];
                    String property;
                    if(part.contains("*")){
                        property = part.replace("*", "");
                    }
                    else {
                        property = part;
                    }
                    columnIndexMap.put(property.trim(), cell.getColumnIndex());
                } else {
                    String property;
                    if(header.contains("*")){
                        property = header.replace("*", "");
                    }
                    else {
                        property = header;
                    }
                    // 如果没有 \n，直接添加原字符串
                    columnIndexMap.put(property.trim(), cell.getColumnIndex());
                }
            }


            //验证用户传入的表格是否符合用户导入的格式
            if(columnIndexMap.get("姓名") == null || columnIndexMap.get("系统角色") == null){
                log.error("用户导入的文件内容有误!");
                throw new CustomException(CustomExceptionType.USER_INPUT_ERROR, "用户导入的文件内容有误!");
            }

            List<SupervisorVO> supervisorList = new ArrayList<>();
            Set<String> notImportSet = new HashSet<>();

            Set<String> notCompletedSet = new HashSet<>();

            // 假设第一行为表头，跳过第一行
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row != null) {
                    String name = getCellValue(row, columnIndexMap.get("姓名"));
                    try {
                        excelService.addUserExcel(row, columnIndexMap, supervisorList);
                    }catch (Exception e){
                        log.error(name + "导入失败!");
                        notImportSet.add(name);
                    }
                }
            }

            //进行主管id的设置
            for (int index = 0; index < supervisorList.size(); index++ ){
                SupervisorVO supervisorVO = supervisorList.get(index);
                String name = supervisorVO.getName();
                try {
                    excelService.addOtherInfo(supervisorVO);
                }catch (Exception e){
                    log.error(name + "导入失败!");
                    notCompletedSet.add(name);
                }

            }

            workbook.close();
            importVO.setNotImportSet(notImportSet);
            importVO.setNotCompletedSet(notCompletedSet);
            return importVO;

        } catch (IOException e) {
            log.error("用户导入数据有误!");
            throw new CustomException(CustomExceptionType.USER_INPUT_ERROR, "用户导入数据有误");
        }catch (IllegalStateException e){
            log.error("文件格式或内容不符合要求!");
            throw new CustomException(CustomExceptionType.USER_INPUT_ERROR, "文件格式或内容不符合要求");
        }

    }

    @Override
    public List<String> findDepartment() {
        return userMapper.findDepartment();
    }

    @Override
    public User findUserByName(String name) {
        return userMapper.findUserByName(name);
    }

    @Override
    @Transactional
    public Integer deleteUser(Integer userId) {
        Integer role = findRole(AuthenticationUtil.getUserId());
        if(!role.equals(Role.superAdmin.getCode())){
            throw new CustomException(CustomExceptionType.USER_INPUT_ERROR, "您暂无删除权限!");
        }

        Integer newEnableProcess = evaluateMapper.findNewestEnableProcess();
        if(newEnableProcess == null){
            newEnableProcess = 1;
        }

        Integer newEpoch = evaluateMapper.findNewEpoch();
        if(newEpoch == null){
            newEpoch = 1;
        }

        //第一阶段则直接删除关系
        if(newEnableProcess.equals(ProcessType.BuildRelationships.getCode())){
           relationshipMapper.clearRelationshipById(userId);
        }
        //位于第二阶段，则把跟这个有关的所有代办任务进行删除(无论是否打完了分)
        else if (newEnableProcess.equals(ProcessType.Evaluation.getCode())){
            //delete删除关系
            relationshipMapper.clearRelationshipById(userId);
            //对于完成了的任务，因为考虑到历史记录，则进行标志位浅删除
            todoListMapper.setFinishedOperationToDeletedInUser(userId, OperationType.FINISHEDANDDELETED.getCode(),newEpoch);
            //对于还没有完成的任务，则直接delete删除
            todoListMapper.DeleteUnFinishedInUser(userId, OperationType.NOTFINISHED.getCode(),newEpoch);
        }
        //第四阶段，浅删除关系
        else if(newEnableProcess.equals(ProcessType.ResultPublic.getCode())){
            relationshipMapper.deleteRelationshipById(userId);
        }
        //位于第三阶段，则不准进行删除
        else{
            throw new CustomException(CustomExceptionType.USER_INPUT_ERROR, "当前阶段无法进行用户删除!");
        }

        //2.若该用户是某人的主管或管理员，则删去这种关系
        userMapper.updateSupervisorAndAdmin(userId);
        //3.若该用户时某人的HRBP，则删除这种关系
        userMapper.updateHRBP(userId);
        return userMapper.deleteUser(userId);
    }

    @Override
    public List<UserVO> findBasicInfos(UserInfoSelectVO userInfoSelectVO) {
        if(userInfoSelectVO.getPageNum() != null && userInfoSelectVO.getPageSize() != null){
            PageHelper.startPage(userInfoSelectVO.getPageNum(), userInfoSelectVO.getPageSize());
        }
        return userMapper.findBasicInfos(userInfoSelectVO);
    }

    @Override
    public Integer updateUserInfo(UserVO user) {
        return userMapper.updateUserInfo(user);
    }

    @Override
    public Integer addUsersByExcel(List<User> users) {
        return userMapper.addUsersByExcel(users);
    }

    @Override
    public Integer addUser(User user) {
        return userMapper.addUser(user);
    }

    @Override
    public User findBasicInfoBySelfId(Integer userId) {
        return userMapper.findBasicInfoBySelfId(userId);
    }

    @Override
    public Integer updatePassword(PasswordUpdateVO passwordUpdateVO, Integer userId) {
        return userMapper.updatePassword(passwordUpdateVO.getNewPassword(), userId);
    }

    @Override
    public String findPassword(Integer userId) {
        return userMapper.findPassword(userId);
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

    // 获取单元格的 LocalDate 值，支持数值型和字符串型的日期格式
    private LocalDate getLocalDateValue(Row row, Integer columnIndex) {
        if (columnIndex == null) return null;
        Cell cell = row.getCell(columnIndex);
        if (cell == null) return null;

        if (cell.getCellType() == CellType.NUMERIC) {
            // 数值类型直接读取 LocalDate
            return cell.getLocalDateTimeCellValue().toLocalDate();
        } else if (cell.getCellType() == CellType.STRING) {
            // 字符串类型解析
            String dateStr = cell.getStringCellValue().trim();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            return LocalDate.parse(dateStr, formatter);
        }
        return null;
    }

    // 将角色名称转换为中文描述
    public String En2Zn(String role) {
        switch (role) {
            case "超级管理员":
                return Role.superAdmin.getDescription();
            case "一级管理员":
                return Role.firstAdmin.getDescription();
            case "二级管理员":
                return Role.SecondAdmin.getDescription();
            default:
                return Role.normal.getDescription();
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


}
