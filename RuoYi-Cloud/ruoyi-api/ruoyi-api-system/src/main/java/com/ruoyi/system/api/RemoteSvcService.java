package com.ruoyi.system.api;

import com.ruoyi.common.core.constant.SecurityConstants;
import com.ruoyi.common.core.constant.ServiceNameConstants;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.system.api.domain.SysUser;
import com.ruoyi.system.api.factory.RemoteUserFallbackFactory;
import com.ruoyi.system.api.model.LoginUser;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * 用户服务
 * 
 * @author ruoyi
 */
@FeignClient(contextId = "remoteSvcService", value = ServiceNameConstants.SVC_SERVICE)
public interface RemoteSvcService
{
    /**
     * 通过用户名查询用户信息
     *
     * @return 结果
     */
    @GetMapping("/svc/pf")
    public R<Integer> pf(@RequestParam("a") String a,@RequestParam("b") String b);

}
