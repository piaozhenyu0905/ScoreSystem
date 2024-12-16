package com.system.assessment.service.Impl;

import com.github.pagehelper.PageHelper;
import com.system.assessment.constants.Role;
import com.system.assessment.exception.CustomException;
import com.system.assessment.exception.CustomExceptionType;
import com.system.assessment.exception.ResponseResult;
import com.system.assessment.mapper.RelationshipMapper;
import com.system.assessment.mapper.UserMapper;
import com.system.assessment.pojo.User;
import com.system.assessment.service.UserService;
import com.system.assessment.utils.AuthenticationUtil;
import com.system.assessment.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    @Autowired
    public UserMapper userMapper;

    @Autowired
    public RelationshipMapper relationshipMapper;

    @Autowired
    public UserService userService;

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
    @Transactional
    public Integer uploadFile(MultipartFile file) {
        try {
            // 解析 Excel 文件
            Workbook workbook = new XSSFWorkbook(file.getInputStream());
            Sheet sheet = workbook.getSheetAt(0);  // 获取第一个工作表
            Row headerRow = sheet.getRow(0);
            Map<String, Integer> columnIndexMap = new HashMap<>(); // 存储每个列名和它的索引

            // 动态获取表头的列索引
            for (Cell cell : headerRow) {
                columnIndexMap.put(cell.getStringCellValue().trim().toLowerCase(), cell.getColumnIndex());
            }
            //验证用户传入的表格是否符合用户导入的格式
            if(columnIndexMap.get("姓名") == null || columnIndexMap.get("系统角色") == null){
                log.error("用户导入的文件内容有误!");
                throw new CustomException(CustomExceptionType.USER_INPUT_ERROR, "用户导入的文件内容有误!");
            }

            Set<String> nameSet = new HashSet<>();

            List<SupervisorVO> supervisorList = new ArrayList<>();

            // 假设第一行为表头，跳过第一行
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row != null) {
                    User user = new User();
                    // 根据列名获取对应列的值，去掉前后空格
                    String name = getCellValue(row, columnIndexMap.get("姓名"));
                    if(name == null || name.equals("")){
                        continue;
                    }
                    if(nameSet.contains(name)){
                        String error = "名字["+name+"]出现了两次!";
                        log.error(error);
                        throw new CustomException(CustomExceptionType.USER_INPUT_ERROR, error);
                    }else {
                        nameSet.add(name);
                    }
                    user.setName(name);
                    User userByName = userMapper.findValuedUserByName(user.getName());
                    // 数据库中该用户已被导入过,则对用户数据进行更新
                    if (userByName != null) {
                        UserVO userVO = new UserVO();
                        userVO.setId(userByName.getId()); //id
                        userVO.setName(userByName.getName()); //姓名
                        String workNum = getCellValue(row, columnIndexMap.get("工号"));
                        if(workNum == null || workNum.equals("")){
                            throw new CustomException(CustomExceptionType.USER_INPUT_ERROR, "表格填写错误!工号不能为空!");
                        }
                        userVO.setWorkNum(workNum);
                        userVO.setDepartment(getCellValue(row, columnIndexMap.get("部门")));
                        userVO.setSupervisorName1(getCellValue(row, columnIndexMap.get("主管1")));
                        userVO.setSupervisorName2(getCellValue(row, columnIndexMap.get("主管2")));
                        userVO.setSupervisorName3(getCellValue(row, columnIndexMap.get("主管3")));
                        userVO.setSupervisorName4(getCellValue(row, columnIndexMap.get("主管4")));
                        userVO.setHrName(getCellValue(row, columnIndexMap.get("HRBP")));
                        userVO.setSupervisor1(0);
                        userVO.setSupervisor2(0);
                        userVO.setSupervisor3(0);
                        userVO.setSupervisor4(0);
                        userVO.setHr(0);

                        // 处理日期字段
                        userVO.setHireDate(getLocalDateValue(row, columnIndexMap.get("入职时间")));
                        userVO.setResidence(getCellValue(row, columnIndexMap.get("常驻地")));
                        userVO.setPhoneNumber(getCellValue(row, columnIndexMap.get("手机号码")));
                        userVO.setEmail(getCellValue(row, columnIndexMap.get("邮箱")));

                        String business = getCellValue(row, columnIndexMap.get("业务类型"));
                        if(!BusinessIsValued(business)){
                            throw new CustomException(CustomExceptionType.USER_INPUT_ERROR, "表格填写错误!业务类型只包含[研发]、[非研发]两种!");
                        }
                        userVO.setBusinessType(business);

                        String lxyz = getCellValue(row, columnIndexMap.get("lxyz类型"));
                        if(!LxyzIsValued(lxyz)){
                            throw new CustomException(CustomExceptionType.USER_INPUT_ERROR, "表格填写错误!LXYZ类型只包含[IP]、[LP]、[中坚]、[精英]和[成长]五种!");
                        }
                        userVO.setLxyz(lxyz);

                        // 设置角色
                        String role = getCellValue(row, columnIndexMap.get("系统角色"));
                        Boolean RoleIsValued = RoleIsValued(role);
                        if(!RoleIsValued){
                            throw new CustomException(CustomExceptionType.USER_INPUT_ERROR, "表格填写错误!系统角色只包含[超级管理员]、[一级管理员]、[二级管理员]、[普通用户]四种!");
                        }
                        userVO.setRole(Role.getCodeByDescription(En2Zn(role)));
                        userVO.setIsDelete(false);

                        SupervisorVO supervisorVO = new SupervisorVO();
                        supervisorVO.setId(userByName.getId());
                        supervisorVO.setName(userByName.getName());
                        supervisorVO.setSupervisorName1(userVO.getSupervisorName1());
                        supervisorVO.setSupervisorName2(userVO.getSupervisorName2());
                        supervisorVO.setSupervisorName3(userVO.getSupervisorName3());
                        supervisorVO.setSupervisorName4(userVO.getSupervisorName4());
                        supervisorVO.setHrName(userVO.getHrName());
                        supervisorList.add(supervisorVO);

                        userMapper.updateUserInfoInit(userVO);

                    }else {
                        user.setUsername(getCellValue(row, columnIndexMap.get("姓名")));
                        String workNum = getCellValue(row, columnIndexMap.get("工号"));
                        if(workNum == null || workNum.equals("")){
                            throw new CustomException(CustomExceptionType.USER_INPUT_ERROR, "表格填写错误!工号不能为空!");
                        }
                        user.setWorkNum(workNum);
                        user.setDepartment(getCellValue(row, columnIndexMap.get("部门")));
                        user.setSupervisorName1(getCellValue(row, columnIndexMap.get("主管1")));
                        user.setSupervisorName2(getCellValue(row, columnIndexMap.get("主管2")));
                        user.setSupervisorName3(getCellValue(row, columnIndexMap.get("主管3")));
                        user.setSupervisorName4(getCellValue(row, columnIndexMap.get("主管4")));
                        user.setHrName(getCellValue(row, columnIndexMap.get("HRBP")));

                        user.setSupervisor1(0);
                        user.setSupervisor2(0);
                        user.setSupervisor3(0);
                        user.setSupervisor4(0);
                        user.setHr(0);

                        // 处理日期字段
                        user.setHireDate(getLocalDateValue(row, columnIndexMap.get("入职时间")));
                        user.setResidence(getCellValue(row, columnIndexMap.get("常驻地")));
                        user.setPhoneNumber(getCellValue(row, columnIndexMap.get("手机号码")));
                        user.setEmail(getCellValue(row, columnIndexMap.get("邮箱")));

                        String business = getCellValue(row, columnIndexMap.get("业务类型"));
                        if(!BusinessIsValued(business)){
                            throw new CustomException(CustomExceptionType.USER_INPUT_ERROR, "表格填写错误!业务类型只包含[研发]、[非研发]两种!");
                        }
                        user.setBusinessType(business);

                        String lxyz = getCellValue(row, columnIndexMap.get("lxyz类型"));
                        if(!LxyzIsValued(lxyz)){
                            throw new CustomException(CustomExceptionType.USER_INPUT_ERROR, "表格填写错误!LXYZ类型只包含[IP]、[LP]、[中坚]、[精英]和[成长]五种!");
                        }
                        user.setLxyz(lxyz);

                        // 设置角色
                        String role = getCellValue(row, columnIndexMap.get("系统角色"));
                        if(!RoleIsValued(role)){
                            throw new CustomException(CustomExceptionType.USER_INPUT_ERROR, "表格填写错误!系统角色只包含[超级管理员]、[一级管理员]、[二级管理员]、[普通用户]四种!");
                        }
                        user.setRole(Role.getCodeByDescription(En2Zn(role)));

                        // 权限设置
                        user.setWeight(1.0);
                        user.setIsFirstLogin(true); //设置是否为第一次登录，导入后默认为true
                        user.setAccountNonExpired(true);
                        user.setCredentialsNonExpired(true);
                        user.setEnabled(true);
                        user.setAccountNonLocked(true);
                        user.setIsDelete(false);

                        addUser(user);

                        SupervisorVO supervisorVO = new SupervisorVO();
                        supervisorVO.setId(user.getId());
                        supervisorVO.setName(user.getName());
                        supervisorVO.setSupervisorName1(user.getSupervisorName1());
                        supervisorVO.setSupervisorName2(user.getSupervisorName2());
                        supervisorVO.setSupervisorName3(user.getSupervisorName3());
                        supervisorVO.setSupervisorName4(user.getSupervisorName4());
                        supervisorVO.setHrName(user.getHrName());
                        supervisorList.add(supervisorVO);
                    }
                }
            }


            //进行主管id的导设置
            for (int index = 0; index < supervisorList.size(); index++ ){
                SupervisorVO supervisorVO = supervisorList.get(index);
                Integer id = supervisorVO.getId();
                String supervisorName1 = supervisorVO.getSupervisorName1();
                String supervisorName2 = supervisorVO.getSupervisorName2();
                String supervisorName3 = supervisorVO.getSupervisorName3();
                String supervisorName4 = supervisorVO.getSupervisorName4();
                String hrName = supervisorVO.getHrName();

                User supervisor1 = userMapper.findValuedUserByName(supervisorName1);
                if(supervisor1 != null){
                    userMapper.updateSupervisor1ById(id, supervisor1.getId());
                }
                User supervisor2 = userMapper.findValuedUserByName(supervisorName2);
                if(supervisor2 != null){
                    userMapper.updateSupervisor2ById(id, supervisor2.getId());
                }
                User supervisor3 = userMapper.findValuedUserByName(supervisorName3);
                if(supervisor3 != null){
                    userMapper.updateSupervisor3ById(id, supervisor3.getId());
                }
                User supervisor4 = userMapper.findValuedUserByName(supervisorName4);
                if(supervisor4 != null){
                    userMapper.updateSupervisor4ById(id, supervisor4.getId());
                }
                User hrNameUser = userMapper.findValuedUserByHrName(hrName);
                if(hrNameUser != null){
                    userMapper.updateHrById(id, hrNameUser.getId());
                }

            }

            workbook.close();


        } catch (IOException e) {
            log.error("用户导入数据有误!");
            throw new CustomException(CustomExceptionType.USER_INPUT_ERROR, "用户导入数据有误");
        }catch (IllegalStateException e){
            log.error("文件格式或内容不符合要求!");
            throw new CustomException(CustomExceptionType.USER_INPUT_ERROR, "文件格式或内容不符合要求");
        }
        return 1;
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

        //将跟该用户有关的从关系矩阵中删除
        relationshipMapper.deleteRelationshipById(userId);
        //若该用户是某人的主管，则删去这种关系
        userMapper.updateSupervisor(userId);
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
        user.preHandle();
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


}
