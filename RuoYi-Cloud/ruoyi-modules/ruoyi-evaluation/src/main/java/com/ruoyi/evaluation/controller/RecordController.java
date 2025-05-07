package com.ruoyi.evaluation.controller;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.servlet.ServletUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.text.Convert;
import com.ruoyi.common.core.web.domain.AjaxResult;
import com.ruoyi.common.security.utils.SecurityUtils;
import com.ruoyi.evaluation.pojo.Record;
import com.ruoyi.evaluation.pojo.RecordDetail;
import com.ruoyi.evaluation.service.RecordDetailService;
import com.ruoyi.evaluation.service.RecordService;
import com.ruoyi.system.api.RemoteSvcService;
import io.seata.spring.annotation.GlobalTransactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/record")
public class RecordController {

    @Autowired
    private RecordService recordService;

    @Autowired
    private RemoteSvcService remoteSvcService;

    @Autowired
    private RecordDetailService recordDetailService;

    @RequestMapping("/submit/{realId}")
    @GlobalTransactional
    public AjaxResult submit(@PathVariable Integer realId, HttpServletRequest request) {
        int a = 4;
        int b = 14;
        String body = ServletUtil.getBody(request);
        JSONArray jsonArray = new JSONArray(body);
        double score = 0;
        List<RecordDetail> list = new ArrayList<>();
        String id = IdWorker.getIdStr();
        for (Object obj : jsonArray) {
            JSONObject jsonObject = new JSONObject(obj);
            String answer = jsonObject.getStr("answer");
            String answerA = jsonObject.getStr("answerA");
            RecordDetail detail = new RecordDetail();
            detail.setQuestionId(jsonObject.getInt("id"));
            detail.setAnswer(answerA);
            detail.setRecordId(id);
            detail.setScope(0d);
            if ("选择题".equals(jsonObject.getStr("type"))) {
                if (answer.equals(answerA)) {
                    score += a;
                    detail.setScope(4d);
                }
            } else {
                if (StrUtil.isNotBlank(answerA)) {
                    R<Integer> pf = remoteSvcService.pf(answer, answerA);
                    score += pf.getData();
                    detail.setScope(Convert.toDouble(pf.getData()));
                }
            }
            recordDetailService.save(detail);
        }
        Record record = new Record();
        record.setId(id);
        record.setUserId(Convert.toInt(SecurityUtils.getUserId()));
        record.setRateId(realId);
        record.setScope(score);
        record.setDate(new Date());
        recordService.save(record);
        return AjaxResult.success(record);
    }

    @RequestMapping("sel")
    public AjaxResult sel(){
        return AjaxResult.success(recordService.sel());
    }

    @RequestMapping("findAll/{recordId}")
    public AjaxResult findAll(@PathVariable String recordId){
        return AjaxResult.success(recordService.findAll(recordId));
    }

    @RequestMapping("findUser")
    public AjaxResult findUser(){
        return AjaxResult.success(recordService.findUser());
    }
}
