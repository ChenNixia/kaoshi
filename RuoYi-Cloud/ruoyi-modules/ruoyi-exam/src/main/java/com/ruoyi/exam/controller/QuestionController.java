package com.ruoyi.exam.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.core.web.domain.AjaxResult;
import com.ruoyi.common.core.web.page.TableDataInfo;
import com.ruoyi.exam.mapper.QuestionMapper;
import com.ruoyi.exam.pojo.Question;
import com.ruoyi.exam.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/question")
public class QuestionController extends BaseController {

    @Autowired
    private QuestionService questionService;

    @GetMapping("/getAllQuestions")
    public TableDataInfo getAllQuestions(Question question) {
        startPage();
        LambdaQueryWrapper<Question> wrapper =new LambdaQueryWrapper<>();
        wrapper.like(StrUtil.isNotBlank(question.getQuestionText()),Question::getQuestionText,question.getQuestionText());
        List<Question> questions = questionService.list(wrapper);
        return getDataTable(questions);
    }

    @PostMapping(value = "save")
    public AjaxResult save(@RequestBody Question question) {
        questionService.saveOrUpdate(question);
        return AjaxResult.success();
    }

    @PostMapping(value = "del/{id}")
    public AjaxResult del(@PathVariable List<Long> id) {
        questionService.removeBatchByIds(id);
        return AjaxResult.success();
    }

    @GetMapping(value = "findAll")
    public AjaxResult findAll() {
        return AjaxResult.success(questionService.findAll());
    }
}
