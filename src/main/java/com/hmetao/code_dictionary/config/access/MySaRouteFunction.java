package com.hmetao.code_dictionary.config.access;

import cn.dev33.satoken.context.model.SaRequest;
import cn.dev33.satoken.context.model.SaResponse;
import cn.dev33.satoken.router.SaRouteFunction;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;

public class MySaRouteFunction implements SaRouteFunction {
    @Override
    public void run(SaRequest request, SaResponse response, Object handler) {
        // 根据路由划分模块，不同模块不同鉴权
        SaRouter.match("/**", r -> StpUtil.checkLogin());
    }
}
