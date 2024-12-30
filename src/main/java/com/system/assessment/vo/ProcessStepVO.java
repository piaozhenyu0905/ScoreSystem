package com.system.assessment.vo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ProcessStepVO {

    public StepTimeVO first;

    public StepTimeVO second;

    public StepTimeVO third;

    public StepTimeVO fourth;

    public List<StepTimeVO> stepTimeVOList;

    public Boolean isNew;

    public String title;

    public void PreDo(){
        stepTimeVOList = new ArrayList<>();
        stepTimeVOList.add(first);
        stepTimeVOList.add(second);
        stepTimeVOList.add(third);
        stepTimeVOList.add(fourth);
    }
}
