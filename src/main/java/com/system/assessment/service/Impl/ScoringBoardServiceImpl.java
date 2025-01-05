package com.system.assessment.service.Impl;

import com.system.assessment.constants.*;
import com.system.assessment.constants.Role;
import com.system.assessment.exception.CustomException;
import com.system.assessment.exception.CustomExceptionType;
import com.system.assessment.mapper.EvaluateMapper;
import com.system.assessment.mapper.RelationshipMapper;
import com.system.assessment.mapper.TodoListMapper;
import com.system.assessment.mapper.UserMapper;
import com.system.assessment.pojo.*;
import com.system.assessment.service.EmailService;
import com.system.assessment.service.ProcessService;
import com.system.assessment.service.ScoringBoardService;
import com.system.assessment.service.TodoService;
import com.system.assessment.utils.AuthenticationUtil;
import com.system.assessment.utils.MathUtils;
import com.system.assessment.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class ScoringBoardServiceImpl implements ScoringBoardService {

    @Autowired
    public TodoListMapper todoListMapper;

    @Autowired
    public EvaluateMapper evaluateMapper;

    @Autowired
    public RelationshipMapper relationshipMapper;

    @Autowired
    public UserMapper userMapper;

    @Autowired
    public EmailService emailService;

    @Autowired
    public ProcessService processService;


    public List<Double> sumScoreWorking(List<SingleScore> singleScoreList){
        List<Double> scoreList = new ArrayList<>(Collections.nCopies(Guideline.values().length, 0.0));

        singleScoreList.forEach(singleScore -> {
            List<ScoreVO> scores = singleScore.getScores();
            scores.forEach(score -> {
                Integer index = score.getDimensionId() - 1;
                Double rawScore = scoreList.get(index);
                scoreList.set(index, rawScore + score.getScore());
            });
        });

        for(int i = 0; i < scoreList.size(); i++){
            Double newScore = scoreList.get(i) / singleScoreList.size();
            BigDecimal bd = BigDecimal.valueOf(newScore);
            bd = bd.setScale(1, RoundingMode.HALF_UP); // 保留两位小数，四舍五入
            Double newScoreScale = bd.doubleValue();
            scoreList.set(i, newScoreScale);
        }

        return  scoreList;
    };

    public List<Double> sumGettingScore(List<SingleScoreWithSupervisor> singleScoreList){
        List<Double> scoreList = new ArrayList<>(Collections.nCopies(Guideline.values().length, 0.0));
        List<Double> scoreDenominator = new ArrayList<>(Collections.nCopies(Guideline.values().length, 0.0));
        List<SpecialScoreVO> specialScoreList = new ArrayList<>();
        Integer employeeCount = 0;
        Integer supervisorCount = 0;

        //1.先计算非使命必达维度的平均分
        for(int idx = 0; idx < singleScoreList.size(); idx++){
            SingleScoreWithSupervisor singleScore = singleScoreList.get(idx);
            List<ScoreVO> scores = singleScore.getScores();
            Double confidenceLevel = singleScore.getSingleConfidenceLevel();
            //不等于-1证明评估人是被评估人的主管且权重有效
            if(singleScore.getIsSupervisor() != -1.0){
                supervisorCount++;
            }else {
                employeeCount++;
            }
            scores.forEach(score -> {
                //不是使命必达的分按正常加权平均分算
                if(score.getDimensionId() != Guideline.Execution.getCode()){
                    Integer index = score.getDimensionId() - 1;
                    Double rawScore = scoreList.get(index);
                    Double newScore = rawScore + score.getScore() * confidenceLevel;
                    scoreList.set(index, newScore);
                    Double oldDenominator = scoreDenominator.get(index);
                    Double newDenominator = oldDenominator + confidenceLevel;
                    scoreDenominator.set(index, newDenominator);
                }else {
                    if(singleScore.getIsSupervisor() != -1.0){
                        specialScoreList.add(new SpecialScoreVO(singleScore.getIsSupervisor(), score.getScore(), confidenceLevel));
                    }
                    else {
                        specialScoreList.add(new SpecialScoreVO(-1.0, score.getScore(), confidenceLevel));
                    }
                }
            });
        }
        for(int i = 0; i < scoreList.size(); i++){
            //计算非使命必达的加权平均分
            if(i != (Guideline.Execution.getCode() - 1)){
                Double newScore = scoreList.get(i) / (scoreDenominator.get(i) + Double.MIN_VALUE);
                BigDecimal bd = BigDecimal.valueOf(newScore);
                bd = bd.setScale(1, RoundingMode.HALF_UP); // 保留两位小数，四舍五入
                Double newScoreScale = bd.doubleValue();
                scoreList.set(i, newScoreScale);
            }
        }

        //下面计算使命必达维度的平均分
        // 确定基本权重
        double employeeBasicWeight;

        if (employeeCount > 0 && supervisorCount > 0) {
            // 既有员工又有主管
            employeeBasicWeight = 0.1 / employeeCount;
        } else {
            // 只有员工或只有主管
            employeeBasicWeight = 1.0 / (employeeCount + supervisorCount);
        }
        // 遍历计算最终加权分数和总权重
        double weightedSum = 0.0;
        double totalWeight = 0.0;
        for(SpecialScoreVO specialScore: specialScoreList){
            Double isSupervisor = specialScore.getIsSupervisor();
            double basicWeight;
            if(isSupervisor == -1.0){
                //普通员工
                basicWeight = employeeBasicWeight;
            }else {
                //主管
                basicWeight = isSupervisor;
            }

            double finalWeight = basicWeight * specialScore.getWeight();
            weightedSum += specialScore.getScore() * finalWeight;
            totalWeight += finalWeight;
        }
        Double specialTotalScore = totalWeight == 0 ? 0 : weightedSum / totalWeight;
        specialTotalScore = MathUtils.transformer(specialTotalScore);
        scoreList.set(Guideline.Execution.getCode() - 1,specialTotalScore);
        return scoreList;
    };

    public AverageGettingScoringVO sumSingleGettingAverageByConfidence(ScoreGettingDetailVO scoreDetailVO){
        if(scoreDetailVO == null){
            return null;
        }
        AverageGettingScoringVO averageGettingScoringVO = new AverageGettingScoringVO();
        averageGettingScoringVO.setDepartment(scoreDetailVO.getDepartment());
        averageGettingScoringVO.setLxyz(scoreDetailVO.getLxyz());
        averageGettingScoringVO.setEvaluatedName(scoreDetailVO.getEvaluatedName());
        averageGettingScoringVO.setBusiness(scoreDetailVO.getBusiness());
        averageGettingScoringVO.setUserId(scoreDetailVO.getEvaluatedId());
        List<SingleScoreWithSupervisor> singleScoreList = scoreDetailVO.getSingleScore();
        averageGettingScoringVO.setScoreList(sumGettingScore(singleScoreList));
        averageGettingScoringVO.setTotalScore(sumTotalScore(averageGettingScoringVO.getScoreList())); //计算所有维度平均分的总分
        return  averageGettingScoringVO;
    }

    public AverageGettingScoringVO sumSingleGettingAverage(ScoreGettingDetailVO scoreDetailVO){
        if(scoreDetailVO == null){
            return null;
        }
        AverageGettingScoringVO averageGettingScoringVO = new AverageGettingScoringVO();
        averageGettingScoringVO.setDepartment(scoreDetailVO.getDepartment());
        averageGettingScoringVO.setLxyz(scoreDetailVO.getLxyz());
        averageGettingScoringVO.setEvaluatedName(scoreDetailVO.getEvaluatedName());
        averageGettingScoringVO.setBusiness(scoreDetailVO.getBusiness());
        averageGettingScoringVO.setUserId(scoreDetailVO.getEvaluatedId());
        List<SingleScoreWithSupervisor> singleScoreList = scoreDetailVO.getSingleScore();
        averageGettingScoringVO.setScoreList(sumGettingScore(singleScoreList));
        averageGettingScoringVO.setTotalScore(sumTotalScore(averageGettingScoringVO.getScoreList())); //计算所有维度平均分的总分
        return  averageGettingScoringVO;
    }


    public List<AverageGettingScoringVO> sumGettingAverage(List<ScoreGettingDetailVO> rawList){
        //计算每个被评估人的各维度平均得分以及总得分
        ArrayList<AverageGettingScoringVO> postList = new ArrayList<>();
        if(rawList == null || rawList.isEmpty()){
            return null;
        }
        rawList.forEach(scoreDetailVO -> {
            AverageGettingScoringVO averageGettingScoringVO = new AverageGettingScoringVO();
            averageGettingScoringVO.setDepartment(scoreDetailVO.getDepartment());
            averageGettingScoringVO.setLxyz(scoreDetailVO.getLxyz());
            averageGettingScoringVO.setEvaluatedName(scoreDetailVO.getEvaluatedName());
            averageGettingScoringVO.setBusiness(scoreDetailVO.getBusiness());
            averageGettingScoringVO.setConfidence(scoreDetailVO.getConfidence());
            averageGettingScoringVO.setUserId(scoreDetailVO.getEvaluatedId());
            List<SingleScoreWithSupervisor> singleScoreList = scoreDetailVO.getSingleScore();
            averageGettingScoringVO.setScoreList(sumGettingScore(singleScoreList));
            averageGettingScoringVO.setTotalScore(sumTotalScore(averageGettingScoringVO.getScoreList())); //计算所有维度平均分的总分
            postList.add(averageGettingScoringVO);
        });

        return postList;
    }


    public List<AverageScoringVO> sumAverage(List<ScoreDetailVO> rawList){
        ArrayList<AverageScoringVO> postList = new ArrayList<>();
        if(rawList == null || rawList.isEmpty()){
            return null;
        }

        rawList.forEach(scoreDetailVO -> {
            AverageScoringVO averageScoringVO = new AverageScoringVO();
            averageScoringVO.setDepartment(scoreDetailVO.getDepartment());
            averageScoringVO.setConfidenceLevel(scoreDetailVO.getConfidenceLevel());
            averageScoringVO.setEvaluatorName(scoreDetailVO.getEvaluatorName());
            averageScoringVO.setUserId(scoreDetailVO.getUserId());
            List<SingleScore> singleScoreList = scoreDetailVO.getSingleScore();
            averageScoringVO.setScoreList(sumScoreWorking(singleScoreList));
            //只有全部都确认状态才是已确认，否则是未确认
            Integer enableCode = 1;
            for (int index = 0; index < singleScoreList.size(); index++){
                SingleScore singleScore = singleScoreList.get(index);
                if(singleScore.getEnable() != ScoringOperationType.Confirmed.getCode()){
                    enableCode = 0;
                    break;
                }
            }
            String enable = ScoringOperationType.getDescriptionByCode(enableCode);
            averageScoringVO.setState(enable);
            postList.add(averageScoringVO);
        });

        return postList;
    }

    public List<AverageScoringVO> orderBy(List<AverageScoringVO> dataList, Integer orderAt){
        if(dataList == null || dataList.isEmpty()){
            return null;
        }
        if(orderAt == null ||orderAt == 0 ){
            dataList.sort((o1, o2) -> {
                // 优先将 state 为 "待确认" 的对象排到前面
                if ("待确认".equals(o1.getState()) && !"待确认".equals(o2.getState())) {
                    return -1;  // o1 排在前面
                } else if (!"待确认".equals(o1.getState()) && "待确认".equals(o2.getState())) {
                    return 1;   // o2 排在前面
                } else {
                    return 0;   // 相等，保持原来的顺序
                }
            });

            return dataList;
        }else {
            int index = orderAt - 1;
            dataList.sort((o1, o2) -> {
                // 获取 o1 和 o2 的 scores 指定位置的分数
                Double score1 = o1.getScoreList() != null && o1.getScoreList().size() > index ? o1.getScoreList().get(index) : 0.0;
                Double score2 = o2.getScoreList() != null && o2.getScoreList().size() > index ? o2.getScoreList().get(index) : 0.0;

                // 从大到小排序
                return score2.compareTo(score1);
            });
            return  dataList;
        }
    }

    public List<AverageGettingScoringVO> orderByAtGetting(List<AverageGettingScoringVO> dataList, Integer orderAt){
        if(dataList == null || dataList.isEmpty()){
            return null;
        }
        if(orderAt == null){
            return dataList;
        }else if(orderAt == 0){
            dataList.sort((o1, o2) -> {
                // 获取 o1 和 o2 的 scores 指定位置的分数
                Double score1 = o1.getTotalScore() != null  ? o1.getTotalScore() : 0.0;
                Double score2 = o2.getTotalScore() != null  ? o2.getTotalScore() : 0.0;

                // 从大到小排序
                return score2.compareTo(score1);
            });
            return  dataList;
        }
        else {
            int index = orderAt - 1;
            dataList.sort((o1, o2) -> {
                // 获取 o1 和 o2 的 scores 指定位置的分数
                Double score1 = o1.getScoreList() != null && o1.getScoreList().size() > index ? o1.getScoreList().get(index) : 0.0;
                Double score2 = o2.getScoreList() != null && o2.getScoreList().size() > index ? o2.getScoreList().get(index) : 0.0;

                // 从大到小排序
                return score2.compareTo(score1);
            });
            return  dataList;
        }
    }


    public  <T> List<T> paginateList(List<T> dataList, int pageSize, int pageNum){
        if (dataList == null || dataList.isEmpty() || pageSize <= 0 || pageNum <= 0) {
            return new ArrayList<>(); // 返回空列表
        }

        // 计算分页的起始索引和结束索引
        int fromIndex = (pageNum - 1) * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, dataList.size());

        // 如果起始索引超出列表长度，返回空列表
        if (fromIndex >= dataList.size()) {
            return new ArrayList<>();
        }

        // 返回指定范围的子列表
        return dataList.subList(fromIndex, toIndex);
    };

    @Override
    public GroupAverageVO getAverageScoreBoardAll(String condition) {
        GroupAverageVO groupAverage = new GroupAverageVO();
        ArrayList<AverageScoreByCondition> dataList = new ArrayList<>();

        if(condition.equals("业务")){
            GetByStyleCondition RandD = new GetByStyleCondition(); //研发
            RandD.setCondition("业务");
            RandD.setContent(0);
            AverageScoreByCondition RandDResult = getAverageScoreBoardByCondition(RandD);
            dataList.add(RandDResult);

            GetByStyleCondition NonRandD = new GetByStyleCondition(); //研发
            NonRandD.setCondition("业务");
            NonRandD.setContent(1);
            AverageScoreByCondition NonRandDResult = getAverageScoreBoardByCondition(NonRandD);
            dataList.add(NonRandDResult);
        }else {
            GetByStyleCondition LGroup = new GetByStyleCondition(); //L
            LGroup.setCondition("lxyz");
            LGroup.setContent(0);
            AverageScoreByCondition LGroupResult = getAverageScoreBoardByCondition(LGroup);
            dataList.add(LGroupResult);

            GetByStyleCondition XandYGroup = new GetByStyleCondition(); //X+Y
            XandYGroup.setCondition("lxyz");
            XandYGroup.setContent(1);
            AverageScoreByCondition XandYGroupResult = getAverageScoreBoardByCondition(XandYGroup);
            dataList.add(XandYGroupResult);

            GetByStyleCondition ZGroup = new GetByStyleCondition(); //Z
            ZGroup.setCondition("lxyz");
            ZGroup.setContent(2);
            AverageScoreByCondition ZGroupResult = getAverageScoreBoardByCondition(ZGroup);
            dataList.add(ZGroupResult);
        }
        groupAverage.setDataList(dataList);
        return groupAverage;
    }

    @Override
    @Transactional
    public void assessorConfidenceLevel(AssessorConfidenceLevelVO assessorConfidenceLevelVO) {
        Integer newEpoch = evaluateMapper.findNewEpoch();
        Double confidenceLevel = assessorConfidenceLevelVO.getConfidenceLevel();
        Integer userId = assessorConfidenceLevelVO.getUserId();
        evaluateMapper.assessorConfidenceLevel(confidenceLevel, userId);
        processService.countAverageScoreByEpoch(newEpoch);
    }

    @Override
    public ScoreProcessVO getScoreProcess() {
        Integer newEpoch = evaluateMapper.findNewEpoch();
        ScoreProcessVO scoreProcessVO = new ScoreProcessVO();
        //1.计算所有该打分的人
        Integer total = todoListMapper.sumTotalPeople(newEpoch);
        //2.计算所有已完成打分的人
        Integer Completed = todoListMapper.sumCompletedPeople(newEpoch);
        scoreProcessVO.setTotalPeople(total);
        scoreProcessVO.setCompletedPeople(Completed);
        return scoreProcessVO;
    }

    public List<ScoreDetailVO> filterData(List<ScoreDetailVO> scoreDetailList){
        if (scoreDetailList == null || scoreDetailList.isEmpty()){
            return  null;
        }
        ArrayList<ScoreDetailVO> filterList = new ArrayList<>();

        for (int index = 0; index < scoreDetailList.size(); index++) {
            ScoreDetailVO scoreSingleDetail = scoreDetailList.get(index);
            List<SingleScore> singleScoreList = scoreSingleDetail.getSingleScore();
            Boolean enable = true;
            //根据某个评议人当前轮次所有的评议任务id，判断所有评议任务是否都做完
            for(int i = 0; i < singleScoreList.size(); i++){
                SingleScore singleScore = singleScoreList.get(i);
                //该评议人的某个评议任务没有做完，则该评议人无法显示在“评分看板”上，从数据中过滤掉
                if(singleScore.getOperation() != OperationType.FINISHED.getCode()){
                    enable = false;
                    break;
                }
                //根据评议任务id查找出对应的打分情况
                SingleScore SingleScoreDetail = todoListMapper.findSingleScoreByTodoId(singleScore.getTodoId());
                singleScore.setScores(SingleScoreDetail.getScores());
            };
            //设置置信度
            scoreSingleDetail.setConfidenceLevel(singleScoreList.get(0).getSingleConfidenceLevel());
            //该评议人的所有评议任务都打分完成，则保留在结果集
            if(enable){
                filterList.add(scoreSingleDetail);
            }
        }

        return filterList;
    }

    public Double sumTotalScore(List<Double> scoreList){
        if(scoreList == null || scoreList.size() == 0){
            return 0.0;
        }
        Double totalScore = 0.0;
        for (int index = 0; index < scoreList.size(); index++){
            totalScore = totalScore + scoreList.get(index);
        }
        BigDecimal bd = BigDecimal.valueOf(totalScore);
        bd = bd.setScale(1, RoundingMode.HALF_UP); // 保留一位小数，四舍五入
        Double newTotalScore = bd.doubleValue();
        return newTotalScore;
    }


    public void noticeReject(String to, String content, String title){
        emailService.sendMessage(to, title, content);
    };

    @Override
    @Transactional
    public Integer reject(RejectVO rejectVO) {
        Integer newEpoch = evaluateMapper.findNewEpoch();
        List<Long> tasksToReject = rejectVO.getTasksToReject();
        List<String> reasonsToReject = rejectVO.getReasonsToReject();

        if(tasksToReject == null && reasonsToReject == null){
            return 1;
        }
        if(tasksToReject.size() != reasonsToReject.size()){
            throw new CustomException(CustomExceptionType.USER_INPUT_ERROR, "参数传递有误!");
        }

        for (int index = 0; index < tasksToReject.size(); index++){
            Long taskId = tasksToReject.get(index); //任务id
            TaskEvaluateInfo taskEvaluateInfo = todoListMapper.findTaskEvaluateInfo(taskId);
            String reason = reasonsToReject.get(index);
            String evaluatorName = taskEvaluateInfo.getEvaluatorName();
            String evaluatedName = taskEvaluateInfo.getEvaluatedName();
            Integer evaluatedId = taskEvaluateInfo.getEvaluatedId();
            Integer evaluatorId = taskEvaluateInfo.getEvaluatorId();

            //将该条任务的enable置为驳回
            todoListMapper.rejectSingle(reason, taskId, ScoringOperationType.Rejected.getCode());

            //1.先判断评估人和被评估人是否还存在关系
            Integer singleRelationship = relationshipMapper.findSingleRelationship(evaluatorId, evaluatedId);

            //2.已不存在关系则跳入下一个循环
            if(singleRelationship == null || singleRelationship == 0){
                continue;
            }
            TodoListVO todoListIsExist = todoListMapper.findTodoListIsExist(evaluatorId, evaluatedId, newEpoch);
            //3.任务存在则进入下个循环
            if(todoListIsExist != null){
                continue;
            }
            //4.否则创建一个新任务
            TodoList todoList = new TodoList();
            todoList.setRejectReason(null);
            todoList.setCompleteTime(null);
            todoList.setOwnerId(evaluatorId);
            todoList.setEvaluatedId(evaluatedId);
            todoList.setEvaluatedName(evaluatedName);
            todoList.setEvaluatorName(evaluatorName);
            todoList.setEvaluatorId(evaluatorId);
            todoList.setConfidenceLevel(1.0);
            todoList.setEpoch(newEpoch);
            todoList.setType(TaskType.SurroundingEvaluation.getDescription());
            todoList.setPresentDate(LocalDateTime.now());
            todoList.setEnable(0);
            todoList.setOperation(0);
            String detail = "请完成对" + evaluatedName +"的评议";
            todoList.setDetail(detail);
            todoListMapper.addTodoList(todoList);
        }

        TaskEvaluateInfo taskEvaluateInfo = todoListMapper.findTaskEvaluateInfo(tasksToReject.get(0)); //根据任务id找到评议人和被评议人的信息
        String reason = reasonsToReject.get(0);  //驳回理由
        // 3.邮件提醒评估人的HRBP
        Integer hrId = userMapper.findHrById(taskEvaluateInfo.getEvaluatorId());
        if(hrId != null && hrId != 0){
            String hrEmail = userMapper.findEmailById(hrId);
            String hrName = userMapper.findNameByUserId(hrId);
            if(hrEmail != null && !hrEmail.equals("")){
                String name = taskEvaluateInfo.getEvaluatorName();
                String subject = "【驳回提醒】"+name+ "的打分已被驳回!";
                String content = "尊敬的" + hrName + "，您好！" + name + "在本轮的评估打分已被管理员驳回" + "。驳回理由:<"+reason +">。请您及时跟进其打分进度！";
                noticeReject(hrEmail, content, subject);
            }
        }
        // 4.邮件提醒评估人
        String email = userMapper.findEmailById(taskEvaluateInfo.getEvaluatorId());
        String name = userMapper.findNameByUserId(taskEvaluateInfo.getEvaluatorId());
        if(email != null && !email.equals("")){
            String subject = "成长评估打分驳回提醒!";
            String content = "尊敬的" + name + "，您好！您在本轮的评估打分已被管理员驳回。"+ "驳回理由:<" + reason +">。请您重新完成打分任务！";
            noticeReject(email, content, subject);
        }

        return 1;
    }

    @Override
    public Integer confidenceLevel(confidenceLevelVO confidenceLevelVO) {
        Integer newEpoch = evaluateMapper.findNewEpoch();
        todoListMapper.confidenceLevel(confidenceLevelVO, newEpoch);
        return 1;
    }

    @Override
    @Transactional
    public Integer confirmScoreMany(ConfirmManyVO confirmManyVO) {
        List<Integer> userIds = confirmManyVO.getUserIds();
        Integer newEpoch = evaluateMapper.findNewEpoch();
        userIds.forEach(userId->{
            todoListMapper.confirmScoreSingle(newEpoch, userId);
        });
        return 1;
    }

    @Override
    public Integer confirmScoreSingle(Integer userId) {
        Integer newEpoch = evaluateMapper.findNewEpoch();
        todoListMapper.confirmScoreSingle(newEpoch, userId);
        return 1;
    }

    @Override
    public List<ScoreDetailIncludeEvaluated> findAverageScoringDetail(Integer userId) {
        Integer newEpoch = evaluateMapper.findNewEpoch();
        List<ScoreDetailIncludeEvaluated> averageScoringDetail = todoListMapper.findAverageScoringDetail(newEpoch, userId);

        averageScoringDetail.forEach(singleScore -> {
            singleScore.postHandle();
            List<ScoreVO> scores = singleScore.getSingleScore();
            Double allScore = 0.0;
            for(int index = 0; index < scores.size(); index++){
                ScoreVO scoreVO = scores.get(index);
                allScore = allScore + scoreVO.getScore();
            }
            BigDecimal bd = BigDecimal.valueOf(allScore);
            bd = bd.setScale(1, RoundingMode.HALF_UP); // 保留两位小数，四舍五入
            Double newScoreScale = bd.doubleValue();
            singleScore.setAllScore(newScoreScale);
        });

        return averageScoringDetail;
    }

    public Double sumTotalByType(AverageSum averageSum){
        List<Double> scoreList = averageSum.getScoreList();
        Double totalScore = 0.0;
        for (int index = 0; index < scoreList.size(); index++){
            totalScore += scoreList.get(index);
        }
        totalScore = MathUtils.transformer(totalScore);
        return totalScore;
    };


    @Override
    public PanelScoreBoardVO selfPanel(Integer userId) {
        PanelScoreBoardVO panelScoreBoardVO = new PanelScoreBoardVO();
        User user = userMapper.findBasicInfoBySelfId(userId);
        String lxyz = user.getLxyz();
        String businessType = user.getBusinessType();
        Integer epoch = evaluateMapper.findNewEpoch();
        //1.先查自己得分的九维图
        ScoreGettingDetailVO singleGettingNewRound = todoListMapper.findSingleGettingNewRound(epoch, userId);
        if(singleGettingNewRound != null){
            List<SingleScoreWithSupervisor> singleScoreList = singleGettingNewRound.getSingleScore();
            for(int i = 0; i < singleScoreList.size(); i++){
                SingleScoreWithSupervisor singleScore = singleScoreList.get(i);
                //根据评议任务id查找出对应的打分情况
                SingleScore SingleScoreDetail = todoListMapper.findSingleScoreByTodoId(singleScore.getTodoId());
                singleScore.setScores(SingleScoreDetail.getScores());
            };
        }
        //2.计算平均值
        Double totalScore;
        List<Double> scoreList;
        AverageGettingScoringVO averageGettingScoringVO = sumSingleGettingAverage(singleGettingNewRound);
        if(averageGettingScoringVO == null){
            totalScore = 0.0;
            scoreList = new ArrayList<>(Collections.nCopies(Guideline.values().length, 0.0));
        }else {
            totalScore =  averageGettingScoringVO.getTotalScore();
            scoreList = averageGettingScoringVO.getScoreList();
            Double confidence = singleGettingNewRound.getConfidence();
            //2.1 总分和各维度得分都要乘以置信度
            totalScore = totalScore * confidence;
            scoreList.replaceAll(n -> n * confidence);
        }

        panelScoreBoardVO.setTotalScore(totalScore);
        panelScoreBoardVO.setSelfList(scoreList);

        //3.查lxyz类型的九维图 （因存入数据库的级联平局分计算已经考虑了置信度，故下面的代码不做额外处理）
        AverageSum lxyzAverageSum;
        AverageSum businessAverageSum;
        if(lxyz.equals("LP") || lxyz.equals("IP")){
            panelScoreBoardVO.setLxyz("LP+IP");
            lxyzAverageSum = evaluateMapper.findAverageSum("lxyz", 0, epoch);
            lxyzAverageSum.postHandle();

        }else if(lxyz.equals("中坚") || lxyz.equals("精英")) {
            panelScoreBoardVO.setLxyz("中坚+精英");
            lxyzAverageSum = evaluateMapper.findAverageSum("lxyz", 1, epoch);
            lxyzAverageSum.postHandle();
        }else {
            panelScoreBoardVO.setLxyz("成长");
            lxyzAverageSum = evaluateMapper.findAverageSum("lxyz", 2, epoch);
            lxyzAverageSum.postHandle();
        }

        Double lxyzTotalScore = sumTotalByType(lxyzAverageSum);
        panelScoreBoardVO.setLxyzTotalScore(lxyzTotalScore);
        panelScoreBoardVO.setLxyzList(lxyzAverageSum.getScoreList());

        if(businessType.equals(BusinessType.RAD.getDescription())){
            panelScoreBoardVO.setBusiness("研发");
            businessAverageSum = evaluateMapper.findAverageSum("业务", 0, epoch);
            businessAverageSum.postHandle();
        }else {
            panelScoreBoardVO.setBusiness("非研发");
            businessAverageSum = evaluateMapper.findAverageSum("业务", 1, epoch);
            businessAverageSum.postHandle();
        }
        Double businessTotalScore = sumTotalByType(businessAverageSum);
        panelScoreBoardVO.setBusinessTotalScore(businessTotalScore);
        panelScoreBoardVO.setBusinessList(businessAverageSum.getScoreList());

        return panelScoreBoardVO;
    }

    @Override
    public List<ScoreGettingDetailIncludeEvaluated> findAverageGettingDetail(Integer userId) {
        Integer newEpoch = evaluateMapper.findNewEpoch();
        List<ScoreGettingDetailIncludeEvaluated> averageGettingScoringDetail = todoListMapper.findAverageGettingScoringDetail(newEpoch, userId);
        averageGettingScoringDetail.forEach(singleScore -> {
            singleScore.postHandle();
            List<ScoreVO> scores = singleScore.getSingleScore();
            Double allScore = 0.0;
            for(int index = 0; index < scores.size(); index++){
                ScoreVO scoreVO = scores.get(index);
                allScore = allScore + scoreVO.getScore();
            }
            allScore = MathUtils.transformer(allScore);
            singleScore.setAllScore(allScore);
        });

        return averageGettingScoringDetail;
    }

    @Override
    @Transactional
    public void countAverageScoreByEpoch(Integer newEpoch) {
        String type;
        type = "lxyz";
        for (int content = 0; content < 3; content++){
            AverageScoreByCondition averageScoreByCondition = countAverageConditional(newEpoch, type, content);
            insertAverageConditional(newEpoch, content, type, averageScoreByCondition);
        }
        type = "业务";
        for (int content = 0; content < 2; content++){
            AverageScoreByCondition averageScoreByCondition = countAverageConditional(newEpoch, type, content);
            insertAverageConditional(newEpoch, content, type, averageScoreByCondition);
        }
    }

    @Override
    @Transactional
    public void updateAverageScoreBoardByCondition(UpdateAverageScoreByConditionalVO getScoreConditionalVO) {
        Integer newEpoch = evaluateMapper.findNewEpoch();
        String condition = getScoreConditionalVO.getCondition();
        Integer content = getScoreConditionalVO.getContent();
        if(getScoreConditionalVO.getWeight() == null){
            return;
        }
        Double weight = getScoreConditionalVO.getWeight();
        evaluateMapper.updateAverageScore(condition, content, newEpoch, weight);
    }

    @Transactional
    @Override
    public void  insertAverageConditional(Integer epoch, Integer content, String type, AverageScoreByCondition averageScoreByCondition){
        if(averageScoreByCondition == null ){
            for(int index = 0; index < Guideline.values().length; index++) {
                AverageSumTable averageSumTable = new AverageSumTable();
                averageSumTable.setAverageScore(0.0);
                averageSumTable.setContent(content);
                averageSumTable.setWeight(1.0);
                averageSumTable.setType(type);
                averageSumTable.setEpoch(epoch);
                averageSumTable.setDimensionId(index + 1);
                evaluateMapper.insertAverageSum(averageSumTable);
            }
            return;
        }
        List<Double> scoreList = averageScoreByCondition.getScoreList();
        for(int index = 0; index < scoreList.size(); index++){
            Double score = scoreList.get(index);
            AverageSumTable averageSumTable = new AverageSumTable();
            averageSumTable.setAverageScore(score);
            averageSumTable.setContent(content);
            averageSumTable.setType(type);
            averageSumTable.setWeight(1.0);
            averageSumTable.setEpoch(epoch);
            averageSumTable.setDimensionId(index + 1);
            evaluateMapper.insertAverageSum(averageSumTable);
        }
    }

    public AverageScoreByCondition countAverageConditional(Integer epoch, String type, Integer content){
        List<ScoreGettingDetailVO> averageGettingNewRoundByCondition = todoListMapper.findAverageGettingNewRoundByCondition(epoch, type, content);
        if(averageGettingNewRoundByCondition == null || averageGettingNewRoundByCondition.size() == 0){
            return null;
        }
        for (int index = 0; index < averageGettingNewRoundByCondition.size(); index++) {
            ScoreGettingDetailVO scoreGettingDetailVO = averageGettingNewRoundByCondition.get(index);
            List<SingleScoreWithSupervisor> singleScoreList = scoreGettingDetailVO.getSingleScore();
            for(int i = 0; i < singleScoreList.size(); i++){
                SingleScoreWithSupervisor singleScore = singleScoreList.get(i);
                //根据评议任务id查找出对应的打分情况
                SingleScore SingleScoreDetail = todoListMapper.findSingleScoreByTodoId(singleScore.getTodoId());
                singleScore.setScores(SingleScoreDetail.getScores());
            };
        }

        AverageScoreByCondition averageScoreByCondition = new AverageScoreByCondition();
        if(type.equals("lxyz")){
            String lxyz;
            if(content.equals(0)){
                lxyz = "IP+LP";
            }else if(content.equals(1)){
                lxyz = "中坚+精英";
            }else {
                lxyz = "成长";
            }
            averageScoreByCondition.setLxyz(lxyz);
        }else {
            String business;
            if(content.equals(0)){
                business = "研发";
            }else{
                business = "非研发";
            }
            averageScoreByCondition.setBusiness(business);
        }
        //计算平均值
        List<AverageGettingScoringVO> averageScoringList = sumGettingAverage(averageGettingNewRoundByCondition);
        sumAverageScoreByCondition(averageScoringList, averageScoreByCondition);
        return averageScoreByCondition;
    };

    @Override
    public AverageScoreByCondition getAverageScoreBoardByCondition(GetByStyleCondition getScoreConditionalVO) {
        Integer newEpoch = evaluateMapper.findNewEpoch();
        //先去数据库中查找是否该种类型
        AverageSum averageSum = evaluateMapper.findAverageSum(getScoreConditionalVO.getCondition(), getScoreConditionalVO.getContent(), newEpoch);
        if(averageSum != null){
            averageSum.postHandle();
            AverageScoreByCondition result = new AverageScoreByCondition();
            Double totalScore = sumTotalScore(averageSum.getScoreList());
            result.setTotalScore(totalScore);
            result.setLxyz(averageSum.getLxyz());
            result.setBusiness(averageSum.getBusiness());
            result.setScoreList(averageSum.getScoreList());
            return result;
        }

        return countAverageConditional(newEpoch,getScoreConditionalVO.getCondition(), getScoreConditionalVO.getContent());
    }

    public void sumAverageScoreByCondition(List<AverageGettingScoringVO> scoreList, AverageScoreByCondition averageScoreByCondition){
         List<Double> resultList = new ArrayList<>(Collections.nCopies(Guideline.values().length, 0.0));
         Double totalScore = 0.0;

         for(int index = 0; index < scoreList.size(); index++){
             AverageGettingScoringVO averageGettingScoringVO = scoreList.get(index);
             List<Double> scoreSingleList = averageGettingScoringVO.getScoreList();
             Double confidence = averageGettingScoringVO.getConfidence(); //得分者的置信度

             for(int idx = 0; idx < scoreSingleList.size(); idx++){
                 Double oldScore = resultList.get(idx);
                 Double newScore = oldScore + scoreSingleList.get(idx) * confidence; //新得分 = 旧得分 + 分数 * 置信度
                 resultList.set(idx, newScore);
             }
             Double totalScoreSingle = averageGettingScoringVO.getTotalScore() * confidence;
             totalScore = totalScoreSingle + totalScore;
         }
        //加权平均计算
        for(int index = 0; index < resultList.size(); index++){
            Double rawAverage = resultList.get(index);
            Double newAverage = rawAverage / scoreList.size(); // 总得分/总人数
            Double transformerScore = MathUtils.transformer(newAverage, 2);
            resultList.set(index, transformerScore);
        }
        totalScore = totalScore / scoreList.size();
        totalScore =  MathUtils.transformer(totalScore);
        averageScoreByCondition.setScoreList(resultList);
        averageScoreByCondition.setTotalScore(totalScore);
    };


    @Override
    public DataListResult getAverageScoreBoard(GetScoreConditionalVO getScoreConditionalVO) {
        Integer userId = AuthenticationUtil.getUserId();
        Integer newEpoch = evaluateMapper.findNewEpoch();
        List<ScoreGettingDetailVO> averageGettingNewRound = null;

        //新方案，只要查询者（userId）属于用户的一级或二级或超级管理员中的一个就可以查看到，同时也要能查看到自己的得分情况
        averageGettingNewRound = todoListMapper.findAverageGettingNewRoundByAdmin(newEpoch, getScoreConditionalVO, userId);

        if(averageGettingNewRound == null || averageGettingNewRound.size() == 0){
            return new DataListResult<>(0,new ArrayList<>());
        }
        for (int index = 0; index < averageGettingNewRound.size(); index++) {
            ScoreGettingDetailVO scoreGettingDetailVO = averageGettingNewRound.get(index);
            List<SingleScoreWithSupervisor> singleScoreList = scoreGettingDetailVO.getSingleScore();
            for(int i = 0; i < singleScoreList.size(); i++){
                SingleScoreWithSupervisor singleScore = singleScoreList.get(i);
                //根据评议任务id查找出对应的打分情况
                SingleScore SingleScoreDetail = todoListMapper.findSingleScoreByTodoId(singleScore.getTodoId());
                singleScore.setScores(SingleScoreDetail.getScores());
            };
        }
        //2.计算平均值
        List<AverageGettingScoringVO> averageScoringList = sumGettingAverage(averageGettingNewRound);
        //3.判断是否需要根据某个指标进行排序
        List<AverageGettingScoringVO> orderedList = orderByAtGetting(averageScoringList, getScoreConditionalVO.getOrderAt());
        //4.进行分页
        List<AverageGettingScoringVO> scoreResults = paginateList(orderedList, getScoreConditionalVO.getPageSize(), getScoreConditionalVO.getPageNum());

        return new DataListResult<>(orderedList.size(), scoreResults);
    }


    @Override
    public DataListResult findAverageScoring(AverageScoringConditionVO averageScoringConditionVO) {
        Integer newEpoch = evaluateMapper.findNewEpoch();
        //1.1.查找出本轮所有打分情况的数据（带条件查询的）
        List<ScoreDetailVO> ScoreDetailList = todoListMapper.findAverageScoringNewRound(newEpoch, averageScoringConditionVO);

        //1.2 过滤数据
        List<ScoreDetailVO> filterList = filterData(ScoreDetailList);
        if(filterList == null || filterList.size() == 0){
            return new DataListResult<>(0,new ArrayList<>());
        }

        //2.计算平均值
        List<AverageScoringVO> averageList = sumAverage(filterList);

        //3.判断是否需要根据某个指标进行排序
        List<AverageScoringVO> orderedList = orderBy(averageList, averageScoringConditionVO.getOrderAt());

        //4.进行分页
        List<AverageScoringVO> scoreResults = paginateList(orderedList, averageScoringConditionVO.getPageSize(), averageScoringConditionVO.getPageNum());

        return new DataListResult<>(orderedList.size(), scoreResults);
    }
}
