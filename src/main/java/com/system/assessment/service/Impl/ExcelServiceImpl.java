package com.system.assessment.service.Impl;

import com.system.assessment.constants.RelationType;
import com.system.assessment.constants.Role;
import com.system.assessment.exception.CustomException;
import com.system.assessment.exception.CustomExceptionType;
import com.system.assessment.mapper.EvaluateMapper;
import com.system.assessment.mapper.RelationshipMapper;
import com.system.assessment.mapper.UserMapper;
import com.system.assessment.pojo.EvaluateRelationship;
import com.system.assessment.pojo.User;
import com.system.assessment.service.ExcelService;
import com.system.assessment.vo.NameAndWorkNum;
import com.system.assessment.vo.SupervisorVO;
import com.system.assessment.vo.UserVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class ExcelServiceImpl implements ExcelService {
    @Autowired
    public UserMapper userMapper;

    @Autowired
    public RelationshipMapper relationshipMapper;

    @Autowired
    public EvaluateMapper evaluateMapper;

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


    public Double extractPercent(String weight){
        if(weight == null || weight.equals("")){
            return null;
        }
        String numberString = weight.replace("%", "");
        numberString = numberString.trim();
        double value = Double.parseDouble(numberString);
        value = value / 100;
        return value;
    };


    @Override
    @Transactional
    public void addUserExcel(Row row, Map<String, Integer> columnIndexMap, List<SupervisorVO> supervisorList) {
        User user = new User();
        // 根据列名获取对应列的值，去掉前后空格
        String name = getCellValue(row, columnIndexMap.get("姓名"));
        if(name == null || name.equals("")){
            return;
        }
        String workNum = getCellValue(row, columnIndexMap.get("工号"));
        if(workNum == null || workNum.equals("")){
            return;
        }
        user.setName(name);
        user.setWorkNum(workNum);
        User userByName = userMapper.findValuedUserByNameAndWorkNum(name, workNum);

        // 数据库中该用户已被导入过,则对用户数据进行更新
        if (userByName != null) {
            UserVO userVO = new UserVO();

            userVO.setId(userByName.getId()); //id
            userVO.setName(name); //姓名
            userVO.setWorkNum(workNum);//工号
            userVO.setDepartment(getCellValue(row, columnIndexMap.get("部门"))); //部门

            SupervisorVO supervisorVO = new SupervisorVO();
            supervisorVO.setId(userVO.getId());
            supervisorVO.setWorkNum(userVO.getWorkNum());
            supervisorVO.setName(userVO.getName());

            String superAdmin = getCellValue(row, columnIndexMap.get("超级管理员"));
            NameAndWorkNum superAdminContent = extractCell(superAdmin);
            if(superAdminContent != null){
                userVO.setSuperAdminName(superAdminContent.getName());
                supervisorVO.setSuperAdminName(superAdminContent.getName());
                supervisorVO.setSuperAdminWorkNum(superAdminContent.getWorkNum());
            }

            String firstAdmin = getCellValue(row, columnIndexMap.get("一级管理员"));
            NameAndWorkNum firstAdminContent = extractCell(firstAdmin);
            if(firstAdminContent != null){
                userVO.setFirstAdminName(firstAdminContent.getName());
                supervisorVO.setFirstAdminName(firstAdminContent.getName());
                supervisorVO.setFirstAdminWorkNum(firstAdminContent.getWorkNum());
            }

            String secondAdmin = getCellValue(row, columnIndexMap.get("二级管理员"));
            NameAndWorkNum secondAdminContent = extractCell(secondAdmin);
            if(secondAdminContent != null){
                userVO.setSecondAdminName(secondAdminContent.getName());
                supervisorVO.setSecondAdminName(secondAdminContent.getName());
                supervisorVO.setSecondAdminWorkNum(secondAdminContent.getWorkNum());
            }

            String hr = getCellValue(row, columnIndexMap.get("HRBP"));
            NameAndWorkNum hrContent = extractCell(hr);
            if(hrContent != null){
                userVO.setHrName(hrContent.getName());
                supervisorVO.setHrName(hrContent.getName());
                supervisorVO.setHrWorkNum(hrContent.getWorkNum());
            }

            String superVisor1 = getCellValue(row, columnIndexMap.get("主管1"));
            NameAndWorkNum superVisor1Content = extractCell(superVisor1);
            if(superVisor1Content != null){
                userVO.setSupervisorName1(superVisor1Content.getName());
                supervisorVO.setSupervisorName1(superVisor1Content.getName());
                supervisorVO.setSupervisorWorkNum1(superVisor1Content.getWorkNum());
            }

            String superVisor2 = getCellValue(row, columnIndexMap.get("主管2"));
            NameAndWorkNum superVisor2Content = extractCell(superVisor2);
            if(superVisor2Content != null){
                userVO.setSupervisorName2(superVisor2Content.getName());
                supervisorVO.setSupervisorName2(superVisor2Content.getName());
                supervisorVO.setSupervisorWorkNum2(superVisor2Content.getWorkNum());
            }

            String superVisor3 = getCellValue(row, columnIndexMap.get("主管3"));
            NameAndWorkNum superVisor3Content = extractCell(superVisor3);
            if(superVisor3Content != null){
                userVO.setSupervisorName3(superVisor3Content.getName());
                supervisorVO.setSupervisorName3(superVisor3Content.getName());
                supervisorVO.setSupervisorWorkNum3(superVisor3Content.getWorkNum());
            }

            userVO.setSupervisor1(0);
            userVO.setSupervisor2(0);
            userVO.setSupervisor3(0);
            userVO.setFirstAdmin(0);
            userVO.setSecondAdmin(0);
            userVO.setSuperAdmin(0);
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

            String lxyz = getCellValue(row, columnIndexMap.get("LXYZ类型"));
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

            userMapper.updateUserInfoInit(userVO);
            supervisorList.add(supervisorVO);

        }else {
            user.setUsername(getCellValue(row, columnIndexMap.get("姓名")));
            user.setDepartment(getCellValue(row, columnIndexMap.get("部门")));

            SupervisorVO supervisorVO = new SupervisorVO();
            supervisorVO.setName(user.getName());
            supervisorVO.setWorkNum(user.getWorkNum());

            String superAdmin = getCellValue(row, columnIndexMap.get("超级管理员"));
            NameAndWorkNum superAdminContent = extractCell(superAdmin);
            if(superAdminContent != null){
                user.setSuperAdminName(superAdminContent.getName());
                supervisorVO.setSuperAdminName(superAdminContent.getName());
                supervisorVO.setSuperAdminWorkNum(superAdminContent.getWorkNum());
            }

            String firstAdmin = getCellValue(row, columnIndexMap.get("一级管理员"));
            NameAndWorkNum firstAdminContent = extractCell(firstAdmin);
            if(firstAdminContent != null){
                user.setFirstAdminName(firstAdminContent.getName());
                supervisorVO.setFirstAdminName(firstAdminContent.getName());
                supervisorVO.setFirstAdminWorkNum(firstAdminContent.getWorkNum());
            }

            String secondAdmin = getCellValue(row, columnIndexMap.get("二级管理员"));
            NameAndWorkNum secondAdminContent = extractCell(secondAdmin);
            if(secondAdminContent != null){
                user.setSecondAdminName(secondAdminContent.getName());
                supervisorVO.setSecondAdminName(secondAdminContent.getName());
                supervisorVO.setSecondAdminWorkNum(secondAdminContent.getWorkNum());
            }

            String hr = getCellValue(row, columnIndexMap.get("HRBP"));
            NameAndWorkNum hrContent = extractCell(hr);
            if(hrContent != null){
                user.setHrName(hrContent.getName());
                supervisorVO.setHrName(hrContent.getName());
                supervisorVO.setHrWorkNum(hrContent.getWorkNum());
            }

            String superVisor1 = getCellValue(row, columnIndexMap.get("主管1"));
            NameAndWorkNum superVisor1Content = extractCell(superVisor1);
            if(superVisor1Content != null){
                user.setSupervisorName1(superVisor1Content.getName());
                supervisorVO.setSupervisorName1(superVisor1Content.getName());
                supervisorVO.setSupervisorWorkNum1(superVisor1Content.getWorkNum());
            }

            String superVisor2 = getCellValue(row, columnIndexMap.get("主管2"));
            NameAndWorkNum superVisor2Content = extractCell(superVisor2);
            if(superVisor2Content != null){
                user.setSupervisorName2(superVisor2Content.getName());
                supervisorVO.setSupervisorName2(superVisor2Content.getName());
                supervisorVO.setSupervisorWorkNum2(superVisor2Content.getWorkNum());
            }

            String superVisor3 = getCellValue(row, columnIndexMap.get("主管3"));
            NameAndWorkNum superVisor3Content = extractCell(superVisor3);
            if(superVisor3Content != null){
                user.setSupervisorName3(superVisor3Content.getName());
                supervisorVO.setSupervisorName3(superVisor3Content.getName());
                supervisorVO.setSupervisorWorkNum3(superVisor3Content.getWorkNum());
            }

            user.setSupervisor1(0);
            user.setSupervisor2(0);
            user.setSupervisor3(0);
            user.setHr(0);
            user.setFirstAdmin(0);
            user.setSecondAdmin(0);
            user.setSuperAdmin(0);

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

            String lxyz = getCellValue(row, columnIndexMap.get("LXYZ类型"));
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
            user.setWeight1(-1.0);
            user.setWeight2(-1.0);
            user.setWeight3(-1.0);
            user.setIsFirstLogin(true); //设置是否为第一次登录，导入后默认为true
            user.setAccountNonExpired(true);
            user.setCredentialsNonExpired(true);
            user.setEnabled(true);
            user.setAccountNonLocked(true);
            user.setIsDelete(false);
            userMapper.addUser(user);

            supervisorVO.setId(user.getId());
            supervisorList.add(supervisorVO);
        }
    }

    @Override
    @Transactional
    //处理表格中的每一行，一旦抛出异常则该列所有的数据均无效
    public void addRelationshipExcel(Row row, Map<String, Integer> columnIndexMap) {
        String evaluatedNameAndWorkNum = getCellValue(row, columnIndexMap.get("被评估人"));
        NameAndWorkNum nameAndWorkNum = extractCell(evaluatedNameAndWorkNum);
        if(nameAndWorkNum == null){
           return;
        }
        String workNum = nameAndWorkNum.getWorkNum();
        String name = nameAndWorkNum.getName();
        Integer evaluatedId = userMapper.findIdByNameAndWorkNum(name, workNum);
        if(evaluatedId == null || evaluatedId == 0){
            log.error("用户"+name+"-"+workNum+"不存在!");
            throw new CustomException(CustomExceptionType.USER_INPUT_ERROR, "用户"+name+"-"+workNum+"不存在!");
        }

        //被评估人有效,则进行主管权重分配， 要求分配之前清空所有主管和权重的数据，主管id置为0，权重置为-1
        userMapper.updateWeightsAndSuperVisor(evaluatedId);

        String superVisor1 = getCellValue(row, columnIndexMap.get("主管1"));
        NameAndWorkNum superVisor1NameAndWorkNum = extractCell(superVisor1);
        //处理主管1和权重1
        if(superVisor1NameAndWorkNum != null){
            String superVisor1Name = superVisor1NameAndWorkNum.getName();
            String superVisor1WorkNum = superVisor1NameAndWorkNum.getWorkNum();
            Integer superVisor1Id = userMapper.findIdByNameAndWorkNum(superVisor1Name, superVisor1WorkNum);
            if (superVisor1Id == null || superVisor1Id == 0){
                log.error("主管"+name+"-"+workNum+"不存在!");
                throw new CustomException(CustomExceptionType.USER_INPUT_ERROR, "主管"+name+"-"+workNum+"不存在!");
            }
            userMapper.setSuperVisor1(evaluatedId, superVisor1Id);
            //主管存在，只进行权重分配
            String weight1Str = getCellValue(row, columnIndexMap.get("权重1"));
            try {
                Double weight1 = Double.parseDouble(weight1Str);
                userMapper.setSuperWeight1(evaluatedId, weight1);
            } catch (NumberFormatException e) {
                log.error("主管"+name+"-"+workNum+"权重数据转换错误!");
                throw new CustomException(CustomExceptionType.USER_INPUT_ERROR, "主管"+name+"-"+workNum+"权重数据转换错误!");
            }
        }
        String superVisor2 = getCellValue(row, columnIndexMap.get("主管2"));
        NameAndWorkNum superVisor2NameAndWorkNum = extractCell(superVisor2);
        //处理主管2和权重2
        if(superVisor2NameAndWorkNum != null){
            String superVisor2Name = superVisor2NameAndWorkNum.getName();
            String superVisor2WorkNum = superVisor2NameAndWorkNum.getWorkNum();
            Integer superVisor2Id = userMapper.findIdByNameAndWorkNum(superVisor2Name, superVisor2WorkNum);
            if (superVisor2Id == null || superVisor2Id == 0){
                log.error("主管"+name+"-"+workNum+"不存在!");
                throw new CustomException(CustomExceptionType.USER_INPUT_ERROR, "主管"+name+"-"+workNum+"不存在!");
            }
            userMapper.setSuperVisor2(evaluatedId, superVisor2Id);
            //主管存在，只进行权重分配
            String weight2Str = getCellValue(row, columnIndexMap.get("权重2"));
            try {
                Double weight2 = Double.parseDouble(weight2Str);
                userMapper.setSuperWeight2(evaluatedId, weight2);
            } catch (NumberFormatException e) {
                log.error("主管"+name+"-"+workNum+"权重数据转换错误!");
                throw new CustomException(CustomExceptionType.USER_INPUT_ERROR, "主管"+name+"-"+workNum+"权重数据转换错误!");
            }

        }
        String superVisor3 = getCellValue(row, columnIndexMap.get("主管3"));
        NameAndWorkNum superVisor3NameAndWorkNum = extractCell(superVisor3);
        //处理主管3和权重3
        if(superVisor3NameAndWorkNum != null){
            String superVisor3Name = superVisor3NameAndWorkNum.getName();
            String superVisor3WorkNum = superVisor3NameAndWorkNum.getWorkNum();
            Integer superVisor3Id = userMapper.findIdByNameAndWorkNum(superVisor3Name, superVisor3WorkNum);
            if (superVisor3Id == null || superVisor3Id == 0){
                log.error("主管"+name+"-"+workNum+"不存在!");
                throw new CustomException(CustomExceptionType.USER_INPUT_ERROR, "主管"+name+"-"+workNum+"不存在!");
            }
            userMapper.setSuperVisor3(evaluatedId, superVisor3Id);
            //主管存在，只进行权重分配
            String weight3Str = getCellValue(row, columnIndexMap.get("权重3"));
            try {
                Double weight3 = Double.parseDouble(weight3Str);
                userMapper.setSuperWeight3(evaluatedId, weight3);
            } catch (NumberFormatException e) {
                log.error("主管"+name+"-"+workNum+"权重数据转换错误!");
                throw new CustomException(CustomExceptionType.USER_INPUT_ERROR, "主管"+name+"-"+workNum+"权重数据转换错误!");
            }
        }

        //添加固定评估人
        String evaluatorContent = getCellValue(row, columnIndexMap.get("相关人"));
        evaluatorContent = evaluatorContent.replaceAll("；", ";");
        relationshipExtract(nameAndWorkNum, evaluatorContent);
    }

    @Transactional
    @Override
    public void relationshipExtract(NameAndWorkNum nameAndWorkNum, String content){

        if (content == null || content.trim().isEmpty()) {
            return;
        }
        String evaluatedName = nameAndWorkNum.getName();
        String evaluatedWorkNum = nameAndWorkNum.getWorkNum();
        Integer evaluatedId = userMapper.findIdByNameAndWorkNum(evaluatedName, evaluatedWorkNum);
        //先删除该被评估人的所有固定评估关系
        relationshipMapper.deleteAllRelationshipByEvaluatedId(evaluatedId);

        content = content.replaceAll("\\s*;\\s*", ";"); // 去掉 `;` 前后的空格
        content = content.replaceAll("\\s*/\\s*", "/"); // 去掉 `/` 前后的空格

        String[] records = content.split(";");
        for (String record : records){
            // 跳过空记录（例如末尾的分号导致的空字符串）
            if (record.trim().isEmpty()) {
                continue;
            }
            // 使用 / 分割姓名和工号
            String[] parts = record.split("/");
            if (parts.length != 2) {
                log.error("格式错误的记录: " + record);
                throw new CustomException(CustomExceptionType.USER_INPUT_ERROR,"格式错误的记录: " + record);
            }

            String name = parts[0].trim();
            String workNum = parts[1].trim();
            // 验证姓名和工号是否为空
            if (name.isEmpty() || workNum.isEmpty()) {
                log.error("姓名或工号为空的记录: " + record);
                throw new CustomException(CustomExceptionType.USER_INPUT_ERROR,"姓名或工号为空的记录: " + record);
            }
            //验证姓名和对应的工号是否正确
            Integer evaluatorId = relationshipMapper.judgeExistByNameAndWorkNum(name, workNum);
            //该名用户在数据库中存在
            if(evaluatorId != null && !evaluatorId.equals(0)){
                //建立评估关系
                log.info("被评估人:"+evaluatedName +";评估人:" + name);
                addFixedRelationshipById(evaluatorId, evaluatedId);
            }else {
                log.error("相关人内容有误!");
                throw new CustomException(CustomExceptionType.USER_INPUT_ERROR,"相关人内容有误!");
            }
        }
    }

    @Override
    public NameAndWorkNum extractCell(String content) {
        NameAndWorkNum nameAndWorkNum = new NameAndWorkNum();
        if (content == null || content.trim().isEmpty()) {
            return null;
        }

        content = content.replaceAll("\\s*/\\s*", "/"); // 去掉 `/` 前后的空格
        content = content.trim();
        String[] parts = content.split("/");
        String name = parts[0].trim();
        String workNum = parts[1].trim();
        nameAndWorkNum.setName(name);
        nameAndWorkNum.setWorkNum(workNum);
        return nameAndWorkNum;
    }

    public Boolean judgeUserExist(String name, String workNum){
        Integer evaluatorId = relationshipMapper.judgeExistByNameAndWorkNum(name, workNum);
        if(evaluatorId != null && !evaluatorId.equals(0)){
            return true;
        }else {
            return false;
        }
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

}
