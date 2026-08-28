package com.jycloud.web.setting.controller;

import com.jycloud.tool.common.constant.ServiceConstant;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author jycloud
 * @since 2026-07-24
 */
@RestController
@RequestMapping("/system/region")
public class RegionController {

    private static final String PERM_PREFIX = ServiceConstant.SETTING_NAME + ":region:";

}
