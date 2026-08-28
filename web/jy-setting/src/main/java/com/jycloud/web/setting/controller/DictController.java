package com.jycloud.web.setting.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.jycloud.api.user.dto.DictDTO;
import com.jycloud.api.user.dto.DictQueryDTO;
import com.jycloud.api.user.entity.Dict;
import com.jycloud.api.user.vo.DictVO;
import com.jycloud.service.user.service.DictService;
import com.jycloud.tool.common.api.ServerResponse;
import com.jycloud.tool.common.base.ValidationGroups;
import com.jycloud.tool.common.constant.ServiceConstant;
import com.jycloud.tool.common.util.DtoEntityUtil;
import com.jycloud.tool.permission.annotation.RequirePermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 字典管理 Controller
 *
 * @author jycloud
 * @since 2026-07-24
 */
@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/system/dict")
@Tag(name = "字典管理", description = "字典类型与字典项相关接口")
public class DictController {

    private static final String PERM_PREFIX = ServiceConstant.SETTING_NAME + ":dict:";

    private final DictService dictService;

    @Operation(summary = "新增字典类型/字典项")
    @PostMapping("/create")
    @RequirePermission(PERM_PREFIX + "create")
    public ServerResponse<DictVO> create(@Validated(ValidationGroups.Insert.class) @RequestBody DictDTO dto) {
        Dict dict = DtoEntityUtil.trans(dto, Dict.class);
        Dict result = dictService.create(dict);
        return ServerResponse.successData(DtoEntityUtil.trans(result, DictVO.class));
    }

    @Operation(summary = "修改字典类型/字典项")
    @PostMapping("/update")
    @RequirePermission(PERM_PREFIX + "update")
    public ServerResponse<DictVO> update(@Validated(ValidationGroups.Update.class) @RequestBody DictDTO dto) {
        Dict dict = DtoEntityUtil.trans(dto, Dict.class);
        Dict result = dictService.update(dict);
        return ServerResponse.successData(DtoEntityUtil.trans(result, DictVO.class));
    }

    @Operation(summary = "根据ID查询字典")
    @GetMapping("/{dictId}")
    public ServerResponse<DictVO> getById(@PathVariable Long dictId) {
        Dict dict = dictService.getDictById(dictId);
        if (dict == null) {
            return ServerResponse.errorMsg("字典不存在");
        }
        return ServerResponse.successData(DtoEntityUtil.trans(dict, DictVO.class));
    }

    @Operation(summary = "条件分页查询字典列表")
    @GetMapping("/page")
    public ServerResponse<IPage<DictVO>> page(@ParameterObject @Validated DictQueryDTO dto) {
        return ServerResponse.successData(dictService.pageByCondition(dto));
    }

    @Operation(summary = "根据字典码查询字典(含父子集)")
    @GetMapping("/get-by-code/{code}")
    public ServerResponse<DictVO> getByCode(@PathVariable String code) {
        DictVO vo = dictService.getDictByCode(code);
        if (vo == null) {
            return ServerResponse.errorMsg("字典不存在");
        }
        return ServerResponse.successData(vo);
    }

    @Operation(summary = "删除字典(级联删除子字典项)")
    @PostMapping("/delete/{dictId}")
    @RequirePermission(PERM_PREFIX + "delete")
    public ServerResponse<Void> delete(@PathVariable Long dictId) {
        dictService.deleteDict(dictId);
        return ServerResponse.successMsg("删除成功");
    }
}
