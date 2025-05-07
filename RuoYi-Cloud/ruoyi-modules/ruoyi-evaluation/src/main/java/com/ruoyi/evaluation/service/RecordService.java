package com.ruoyi.evaluation.service;

import cn.hutool.core.map.MapUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.security.utils.SecurityUtils;
import com.ruoyi.evaluation.mapper.RecordMapper;
import com.ruoyi.evaluation.pojo.Record;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class RecordService extends ServiceImpl<RecordMapper, Record> {

    @Autowired
    private RecordMapper recordMapper;
    public List<Map> sel() {
        return recordMapper.sel(SecurityUtils.getUserId());
    }

    public List<Map> findAll(String recordId) {
        return recordMapper.findAll(recordId);
    }

    public Map findUser() {
        List<Map> user = recordMapper.findUser();
        Map<Object, Object> collect = user.stream().collect(Collectors.toMap(t -> t.get("nick_name"), t -> t.get("scope")));
        Map map = new HashMap();
        map.put("keys",collect.keySet());
        map.put("values",collect.values());
        return map;
    }
}
