package com.ruoyi.evaluation.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.evaluation.pojo.Record;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface RecordMapper extends BaseMapper<Record> {

    @Select("select r.*,q.name from record r left join exam_real_questions q on q.id = r.rate_id where r.user_id = #{userId} ")
    List<Map> sel(Long userId);

    @Select("select q.*,d.answer answerA,d.scope from record_detail d left join question q on q.id = d.question_id where d.record_id = #{recordId}")
    List<Map> findAll(String recordId);

    @Select("select u.nick_name,Sum(r.scope) scope from record r left join sys_user u on u.user_id = r.user_id group by r.user_id")
    List<Map> findUser();
}
