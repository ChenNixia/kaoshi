package com.ruoyi.exam.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.exam.pojo.Question;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface QuestionMapper extends BaseMapper<Question> {

    @Select("(SELECT * FROM question WHERE type = '选择题' ORDER BY RAND() LIMIT 18)\n" +
            "UNION ALL\n" +
            "(SELECT * FROM question WHERE type = '填空题' ORDER BY RAND() LIMIT 2)\n" +
            "ORDER BY id;")
    List<Question> findAll();

    @Select("select q.* from exam_real_questions_detail d left join question q on d.question_id = q.id where d.real_id = #{id} order by type desc")
    List<Question> sel(Long id);
}