package com.jycloud.web.setting.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.jycloud.api.user.dto.ClientDTO;
import com.jycloud.api.user.dto.ClientQueryDTO;
import com.jycloud.api.user.entity.Client;
import com.jycloud.api.user.vo.ClientVO;
import com.jycloud.service.user.service.ClientService;
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

/**
 * 业务系统管理 Controller
 *
 * @author jycloud
 * @since 2026-07-24
 */
@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/system/client")
@Tag(name = "业务系统管理", description = "业务系统相关接口")
public class ClientController {

    private static final String PERM_PREFIX = ServiceConstant.SETTING_NAME + ":client:";

    private final ClientService clientService;

    @Operation(summary = "新增业务系统")
    @PostMapping("/create")
    @RequirePermission(PERM_PREFIX + "create")
    public ServerResponse<ClientVO> create(@Validated(ValidationGroups.Insert.class) @RequestBody ClientDTO dto) {
        Client client = DtoEntityUtil.trans(dto, Client.class);
        Client result = clientService.create(client);
        ClientVO vo = DtoEntityUtil.trans(result, ClientVO.class);
        return ServerResponse.successData(vo);
    }

    @Operation(summary = "修改业务系统")
    @PostMapping("/update")
    @RequirePermission(PERM_PREFIX + "update")
    public ServerResponse<ClientVO> update(@Validated(ValidationGroups.Update.class) @RequestBody ClientDTO dto) {
        Client client = DtoEntityUtil.trans(dto, Client.class);
        Client result = clientService.update(client);
        ClientVO vo = DtoEntityUtil.trans(result, ClientVO.class);
        return ServerResponse.successData(vo);
    }

    @Operation(summary = "根据ID查询业务系统")
    @GetMapping("/{id}")
    public ServerResponse<Client> getById(@PathVariable Long id) {
        Client client = clientService.getClientById(id);
        if (client == null) {
            return ServerResponse.errorMsg("业务系统不存在");
        }
        // 详情返回完整实体（含 AppKey/AppSecret），供前端详情查看与复制
        return ServerResponse.successData(client);
    }

    @Operation(summary = "条件分页查询业务系统列表")
    @GetMapping("/page")
    public ServerResponse<IPage<ClientVO>> page(@ParameterObject @Validated ClientQueryDTO dto) {
        IPage<ClientVO> page = clientService.pageByCondition(dto);
        return ServerResponse.successData(page);
    }

    @Operation(summary = "删除业务系统")
    @PostMapping("/delete/{id}")
    @RequirePermission(PERM_PREFIX + "delete")
    public ServerResponse<Void> delete(@PathVariable Long id) {
        clientService.deleteClient(id);
        return ServerResponse.successMsg("删除成功");
    }

    @Operation(summary = "重置应用秘钥")
    @PostMapping("/reset-secret/{id}")
    @RequirePermission(PERM_PREFIX + "resetSecret")
    public ServerResponse<Client> resetSecret(@PathVariable Long id) {
        Client client = clientService.resetSecret(id);
        return ServerResponse.successData(client);
    }
}
