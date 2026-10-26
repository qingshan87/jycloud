# jy-log-client

JYCloud 日志客户端模块，提供登录日志、系统操作日志、API 调用日志和错误日志能力。

## 功能

- `@Log("业务描述")`：记录系统操作日志。
- `@ApiLog(actionId, serviceName)`：记录 API 调用日志。
- 全局异常处理器：发布错误日志事件。
- 日志事件通过异步线程写入 `jy-user`，写入失败仅记录错误，不影响业务。
- 日志客户端使用 Spring `@HttpExchange` 和 `LoadBalanced RestClient`。

## 配置

```yaml
sys:
  log:
    enabled: true

jy:
  log:
    client:
      base-url: http://jy-user
      connect-timeout: 5s
      read-timeout: 30s
```

## 使用

```xml
<dependency>
    <groupId>com.jycloud</groupId>
    <artifactId>jy-log-client</artifactId>
    <version>1.0.0</version>
</dependency>
```

```java
@Log("新增订单")
@PostMapping("/orders")
public ServerResponse<Void> createOrder() {
    return ServerResponse.successMsg("创建成功");
}
```

首次部署前需执行 `sql/log.sql` 创建四张日志表。
