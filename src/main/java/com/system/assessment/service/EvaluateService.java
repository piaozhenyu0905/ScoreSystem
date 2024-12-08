package com.system.assessment.service;

import com.system.assessment.pojo.EvaluateProcess;
import com.system.assessment.pojo.EvaluateTable;
import com.system.assessment.vo.*;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public interface EvaluateService {
    public Integer setVisible();

    public Integer findNewEpoch();

    public Integer findNewEnableProcess();

    public List<AllStaff> findAll();

    public List<EvaluateProcess> findEvaluateStep();

    public Integer evaluateTick( EvaluateTickSetVO evaluateTickSetVO);

    public Integer editEvaluateStep(ProcessStepVO processStepVO);

    public EvaluateTable evaluateTableInfo();

    public Integer updateTableInfo(EvaluateTable evaluateTable);

    public List<SelfEvaluatorVO> findSelfEvaluator(Integer userId);

    public List<SelfEvaluatedVO> findSelfEvaluated(Integer userId);
}
