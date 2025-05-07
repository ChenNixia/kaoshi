package com.ruoyi.exam.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.core.web.domain.AjaxResult;
import com.ruoyi.common.core.web.page.TableDataInfo;
import com.ruoyi.exam.mapper.QuestionMapper;
import com.ruoyi.exam.pojo.ExamRealQuestions;
import com.ruoyi.exam.pojo.ExamRealQuestionsDetail;
import com.ruoyi.exam.pojo.Question;
import com.ruoyi.exam.service.ExamRealQuestionsDetailService;
import com.ruoyi.exam.service.ExamRealQuestionsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("examRealQuestions")
public class ExamRealQuestionsController extends BaseController {

    @Autowired
    private ExamRealQuestionsService examRealQuestionsService;

    @Autowired
    private ExamRealQuestionsDetailService examRealQuestionsDetailService;

    @Autowired
    private QuestionMapper questionMapper;

    @GetMapping(value = "list")
    public TableDataInfo list(ExamRealQuestions examRealQuestions) {
        startPage();
        LambdaQueryWrapper<ExamRealQuestions> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StrUtil.isNotBlank(examRealQuestions.getName()), ExamRealQuestions::getName, examRealQuestions.getName());
        List<ExamRealQuestions> list = examRealQuestionsService.list(wrapper);
        return getDataTable(list);
    }

    @PostMapping(value = "save")
    public AjaxResult save(@RequestBody ExamRealQuestions examRealQuestions) {
        examRealQuestions.setDate(new Date());
        boolean save = examRealQuestionsService.saveOrUpdate(examRealQuestions);
        return save ? AjaxResult.success() : AjaxResult.error();
    }

    @PostMapping(value = "del/{id}")
    public AjaxResult del(@PathVariable List<Long> id) {
        boolean b = examRealQuestionsService.removeByIds(id);
        return b? AjaxResult.success() : AjaxResult.error();
    }

    @PostMapping(value = "generateQuestions/{id}")
    public AjaxResult generateQuestions(@PathVariable Integer id) {
        //先删除数据
        examRealQuestionsDetailService.remove(new LambdaQueryWrapper<ExamRealQuestionsDetail>().eq(ExamRealQuestionsDetail::getRealId,id));
        List<Question> all = questionMapper.findAll();
        for (Question question : all) {
            ExamRealQuestionsDetail examRealQuestionsDetail = new ExamRealQuestionsDetail();
            examRealQuestionsDetail.setQuestionId(question.getId());
            examRealQuestionsDetail.setRealId(id);
            examRealQuestionsDetailService.save(examRealQuestionsDetail);
        }
        return AjaxResult.success();
    }

    @PostMapping(value = "sel/{id}")
    public AjaxResult sel(@PathVariable Long id) {
        return AjaxResult.success(questionMapper.sel(id));
    }
}
