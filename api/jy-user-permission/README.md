# jy-user-permission

业务服务接入 `@RequirePermission` / `@RequireRole` 的远程权限桥接模块。

## 职责

- 通过 `UserAuthorityClient`（Spring `@HttpExchange`）调用 jy-user。
- 自动注册远程 `PermissionProvider` 和 `RoleProvider`。
- 权限或角色查询失败时返回空集合，采用 fail-close 语义，避免越权放行。
- 业务服务已提供自定义 Provider 时不覆盖现有 Bean。

## 接入

```xml
<dependency>
    <groupId>com.jycloud</groupId>
    <artifactId>jy-user-permission</artifactId>
    <version>1.0.0</version>
</dependency>
```

业务应用引入依赖并启用自动配置后，无需额外代码即可使用权限注解：

```java
@RequirePermission("biz:order:create")
@PostMapping("/orders")
public ServerResponse<Void> createOrder() {
    return ServerResponse.successMsg("创建成功");
}
```

客户端地址与超时通过 `jy.user-authority.client.*` 配置。
