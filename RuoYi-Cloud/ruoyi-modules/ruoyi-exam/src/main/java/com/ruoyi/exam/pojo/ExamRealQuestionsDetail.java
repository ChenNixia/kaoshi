package com.ruoyi.exam.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("exam_real_questions_detail")
public class ExamRealQuestionsDetail {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private Integer questionId;

    private Integer realId;
}
