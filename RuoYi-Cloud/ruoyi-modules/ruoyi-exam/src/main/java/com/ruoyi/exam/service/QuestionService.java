package com.ruoyi.exam.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.exam.mapper.QuestionMapper;
import com.ruoyi.exam.pojo.Question;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class QuestionService extends ServiceImpl<QuestionMapper, Question> {

    @Autowired
    private QuestionMapper questionMapper;
    public List<Question> findAll() {
        return questionMapper.findAll();
    }
}
