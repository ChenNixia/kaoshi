package com.ruoyi.evaluation.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("record_detail")
public class RecordDetail {
    @TableId(type = IdType.AUTO)
    private Integer id;

    private Integer questionId;

    private String answer;

    private String recordId;

    private Double scope;
}
