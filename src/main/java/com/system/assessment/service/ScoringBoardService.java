package com.system.assessment.service;

import com.system.assessment.vo.*;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface ScoringBoardService {
    public void  exportGetBoard(HttpServletResponse response);

    public GroupAverageVO getAverageScoreBoardAll(String condition);

    public void assessorConfidenceLevel(AssessorConfidenceLevelVO assessorConfidenceLevelVO);

    public ScoreProcessVO getScoreProcess();

    public List<ScoreDetailVO> filterData(List<ScoreDetailVO> scoreDetailList);

    public PanelScoreBoardVO  selfPanel(Integer userId);

    public List<ScoreGettingDetailIncludeEvaluated>  findAverageGettingDetail(Integer userId);

    public void countAverageScoreByEpoch(Integer newEpoch);

    public void updateAverageScoreBoardByCondition(@RequestBody UpdateAverageScoreByConditionalVO getScoreConditionalVO);

    public void  insertAverageConditional(Integer epoch, Integer content, String type, AverageScoreByCondition averageScoreByCondition);

    public  AverageScoreByCondition getAverageScoreBoardByCondition(@RequestBody GetByStyleCondition getScoreConditionalVO);

    public DataListResult getAverageScoreBoard(GetScoreConditionalVO getScoreConditionalVO);

    public Integer reject(RejectVO rejectVO);

    public Integer confidenceLevel(confidenceLevelVO confidenceLevelVO);

    public Integer confirmScoreMany(ConfirmManyVO confirmManyVO);

    public Integer confirmScoreSingle(Integer userId);

    public List<ScoreDetailIncludeEvaluated> findAverageScoringDetail(Integer userId, Integer condition, Integer orderBy);

    public DataListResult findAverageScoring(AverageScoringConditionVO averageScoringConditionVO);
}
