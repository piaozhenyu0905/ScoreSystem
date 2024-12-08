package com.system.assessment.service.Impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.system.assessment.constants.RelationType;
import com.system.assessment.exception.CustomException;
import com.system.assessment.exception.CustomExceptionType;
import com.system.assessment.exception.ResponseResult;
import com.system.assessment.mapper.EvaluateMapper;
import com.system.assessment.mapper.RelationshipMapper;
import com.system.assessment.mapper.UserMapper;
import com.system.assessment.pojo.EvaluateRelationship;
import com.system.assessment.pojo.User;
import com.system.assessment.service.RelationshipService;
import com.system.assessment.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

@Service
@Slf4j
public class RelationshipServiceImpl implements RelationshipService {

    @Autowired
    public RelationshipMapper relationshipMapper;

    @Autowired
    public EvaluateMapper evaluateMapper;

    @Autowired
    public UserMapper userMapper;

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
        return relationshipMapper.deleteEvaluationMatrix(userId, evaluatorId, RelationType.fixed.getCode());
    }

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
                relationshipMatrixVO.setSupervisorName4(user.getSupervisorName4());
                // 2.查找固定评估人
                List<RelationshipEvaluatorInfo> fixedList = relationshipMapper.findEvaluatorById(user.getId(), RelationType.fixed.getCode());
                relationshipMatrixVO.setRelationshipFixedList(fixedList);
                // 3.查找自主评估人
                List<RelationshipEvaluatorInfo> selfList = relationshipMapper.findEvaluatorById(user.getId(), RelationType.self.getCode());
                relationshipMatrixVO.setRelationshipSelfList(selfList);
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
                relationshipMatrixVO.setSupervisorName4(user.getSupervisorName4());
                // 2.查找固定评估人
                List<RelationshipEvaluatorInfo> fixedList = relationshipMapper.findEvaluatorById(user.getId(), RelationType.fixed.getCode());
                relationshipMatrixVO.setRelationshipFixedList(fixedList);
                // 3.查找自主评估人
                List<RelationshipEvaluatorInfo> selfList = relationshipMapper.findEvaluatorById(user.getId(), RelationType.self.getCode());
                relationshipMatrixVO.setRelationshipSelfList(selfList);
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
    public Integer addRelationship(EvaluateRelationship evaluateRelationship) {
        return relationshipMapper.addRelationship(evaluateRelationship);
    }
}
