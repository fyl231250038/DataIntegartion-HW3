# 集成服务器接口文档

本文档用于给 A / B / C 三个院系子系统对接集成服务器时参考。

## 1. 服务信息

- 服务名：`integration-server`
- 默认端口：`8090`
- 基础路径：`/api/integration`
- 数据格式：`application/xml`

相关代码：

- 接口入口：`src/main/java/com/hw3/integration/controller/IntegrationController.java:13`
- 院系回调配置：`src/main/resources/application.yml:8`

## 2. 接口列表

### 2.1 健康检查

- 方法：`GET`
- 路径：`/api/integration/health`
- 返回：

```text
integration-server-up
```

### 2.2 集成请求入口

- 方法：`POST`
- 路径：`/api/integration/requests`
- `Content-Type`：`application/xml` 或 `text/xml`
- 请求体：必须符合 `integrationRequest` 结构
- 响应体：返回 `integrationResponse` XML

请求 / 响应信封规范：

- 请求 XSD：`src/main/resources/schemas/integration-request.xsd:3`
- 响应 XSD：`src/main/resources/schemas/integration-response.xsd:3`

## 3. 支持的动作

### 3.1 `COURSE_SHARE`

用途：

- 源院系向集成服务器查询共享课程
- 集成服务器会向其它院系拉取共享课程，聚合后再转换为源院系本地格式返回

请求要求：

- `sourceSystem`：必须是 `A` / `B` / `C`
- `targetSystem`：
  - 可省略：表示查询除自己外的全部院系
  - 也可指定为 `A` / `B` / `C` 中某一个其它院系
- `body`：可为空

响应特点：

- `status=SUCCESS` 时，`body` 中会返回一个 `classes` / `Classes` 课程集合
- 返回课程格式不是统一格式，而是**源院系自己的本地格式**

### 3.2 `COURSE_SELECT`

用途：

- 源院系学生跨系选课

请求要求：

- `sourceSystem`：发起选课的院系
- `targetSystem`：目标院系，且必须与 `sourceSystem` 不同
- `body` 中必须同时包含：
  - 学生信息 XML
  - 选课信息 XML
- 这两段 XML 必须使用**源院系本地格式**

响应特点：

- 成功时通常只返回 `SUCCESS + message`
- 不强制返回业务数据体

### 3.3 `COURSE_DROP`

用途：

- 源院系学生跨系退课

请求要求：

- 与 `COURSE_SELECT` 相同
- `body` 中也必须同时包含学生信息 XML 和选课信息 XML

### 3.4 `QUERY_SHARED_COURSES`

说明：

- 这是**集成服务器内部向各院系子系统发起**的查询动作
- 外部成员不要直接调用集成服务器发送这个动作
- 集成服务器会直接拒绝外部传入的 `QUERY_SHARED_COURSES`

## 4. 请求头字段说明

所有业务请求都使用如下请求头：

| 字段 | 必填 | 说明 |
| --- | --- | --- |
| `requestId` | 是 | 请求唯一标识，建议用 UUID |
| `sourceSystem` | 是 | 请求发起方，取值：`A` / `B` / `C` / `INTEGRATION` |
| `targetSystem` | 否 | 目标院系，取值：`A` / `B` / `C` / `ALL` / `INTEGRATION` |
| `originSystem` | 否 | 原始来源院系；集成服务器向下游转发时会带上 |
| `action` | 是 | 业务动作 |
| `timestamp` | 是 | ISO-8601 时间，如 `2026-04-14T12:00:00Z` |

## 5. 响应字段说明

| 字段 | 说明 |
| --- | --- |
| `requestId` | 对应原始请求 ID |
| `sourceSystem` | 一般为 `INTEGRATION` |
| `targetSystem` | 一般为原始调用方 |
| `action` | 对应原始动作 |
| `timestamp` | 响应生成时间 |
| `status` | `SUCCESS` / `FAILURE` |
| `message` | 结果说明 |
| `body` | 可能为空；共享课程查询成功时会包含课程 XML |

## 6. 请求示例

### 6.1 查询全部外院共享课程（A 发起）

```xml
<?xml version="1.0" encoding="UTF-8"?>
<integrationRequest>
    <header>
        <requestId>req-share-001</requestId>
        <sourceSystem>A</sourceSystem>
        <action>COURSE_SHARE</action>
        <timestamp>2026-04-14T12:00:00Z</timestamp>
    </header>
    <body/>
</integrationRequest>
```

### 6.2 A 系学生跨系选 B 系课程

```xml
<?xml version="1.0" encoding="UTF-8"?>
<integrationRequest>
    <header>
        <requestId>req-select-001</requestId>
        <sourceSystem>A</sourceSystem>
        <targetSystem>B</targetSystem>
        <action>COURSE_SELECT</action>
        <timestamp>2026-04-14T12:05:00Z</timestamp>
    </header>
    <body>
        <Students>
            <student>
                <学号>2024001</学号>
                <姓名>张三</姓名>
                <性别>男</性别>
                <院系>计算机学院</院系>
            </student>
        </Students>
        <Choices>
            <choice>
                <课程编号>B001</课程编号>
                <学生编号>2024001</学生编号>
            </choice>
        </Choices>
    </body>
</integrationRequest>
```

## 7. 响应示例

### 7.1 成功响应

```xml
<?xml version="1.0" encoding="UTF-8"?>
<integrationResponse>
    <header>
        <requestId>req-select-001</requestId>
        <sourceSystem>INTEGRATION</sourceSystem>
        <targetSystem>A</targetSystem>
        <action>COURSE_SELECT</action>
        <timestamp>2026-04-14T12:05:02Z</timestamp>
    </header>
    <status>SUCCESS</status>
    <message>跨系请求处理成功</message>
    <body/>
</integrationResponse>
```

### 7.2 失败响应

```xml
<?xml version="1.0" encoding="UTF-8"?>
<integrationResponse>
    <header>
        <requestId>req-select-001</requestId>
        <sourceSystem>INTEGRATION</sourceSystem>
        <targetSystem>A</targetSystem>
        <action>COURSE_SELECT</action>
        <timestamp>2026-04-14T12:05:02Z</timestamp>
    </header>
    <status>FAILURE</status>
    <message>跨系选课/退课必须指定合法的目标院系</message>
    <body/>
</integrationResponse>
```

## 8. HTTP 状态码

| HTTP 状态码 | 含义 |
| --- | --- |
| `200` | 业务请求已被集成服务器处理；结果需看 XML 中 `status` |
| `400` | 请求 XML 不合法，或字段 / 动作 / body 缺失 |
| `502` | 调用下游院系系统失败，或下游返回异常 |
| `500` | 集成服务器内部异常 |

异常响应生成位置：

- `src/main/java/com/hw3/integration/exception/GlobalExceptionHandler.java:12`

## 9. 各院系下游接口地址

当前集成服务器默认回调配置如下：

| 院系 | 地址 |
| --- | --- |
| A | `http://localhost:8081/api/external/integration` |
| B | `http://localhost:8082/api/external/integration` |
| C | `http://localhost:8083/api/external/integration` |

如果其它成员本地端口不同，请同步修改 `src/main/resources/application.yml:8`。
