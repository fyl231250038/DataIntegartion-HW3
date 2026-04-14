# 集成服务器启动与项目结构说明

本文档用于说明：

- 当前集成服务器如何启动
- 为什么 `IntegrationServerApplication` 是启动类
- Spring Boot 如何把 `controller`、`service` 等组件启动起来
- 当前项目的目录结构和各部分职责

## 1. 如何启动当前集成服务器

推荐在项目根目录执行：

```powershell
.\mvnw.cmd "-Dmaven.repo.local=.m2/repository" spring-boot:run
```

启动成功后：

- 默认端口：`8090`
- 健康检查接口：`http://localhost:8090/api/integration/health`

端口配置位置：

- `src/main/resources/application.yml:1`

如果想先打包再运行，也可以执行：

```powershell
.\mvnw.cmd "-Dmaven.repo.local=.m2/repository" package
java -jar target/integration-server-1.0.0.jar
```

## 2. `IntegrationServerApplication` 启动类

启动类位置：

- `src/main/java/com/hw3/integration/IntegrationServerApplication.java:1`

核心代码如下：

```java
@SpringBootApplication
@EnableConfigurationProperties(IntegrationProperties.class)
public class IntegrationServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(IntegrationServerApplication.class, args);
    }
}
```

它之所以是启动类，是因为它同时满足下面几个条件：

### 2.1 `main` 方法

位置：

- `src/main/java/com/hw3/integration/IntegrationServerApplication.java:12`

### 2.2 调用了 `SpringApplication.run(...)`

这句代码会启动整个 Spring Boot 应用。

位置：

- `src/main/java/com/hw3/integration/IntegrationServerApplication.java:13`

### 2.3  `@SpringBootApplication`

这个注解表示：

- 这是 Spring Boot 主配置类
- 从当前包开始做组件扫描
- 启用 Spring Boot 自动配置

因为启动类所在包是 `com.hw3.integration`，所以 Spring 会默认扫描它下面所有子包中的组件。

## 3. 启动 `controller`、`service` 等其它文件

可以按下面的顺序理解：

### 3.1 执行 `main`

程序启动后，先执行：

```java
SpringApplication.run(IntegrationServerApplication.class, args);
```

### 3.2 创建 Spring 容器

Spring Boot 会创建应用上下文，可以理解为一个“对象管理容器”。

### 3.3 扫描当前包及其子包

因为启动类在 `com.hw3.integration` 包下，所以会扫描：

- `com.hw3.integration.controller`
- `com.hw3.integration.service`
- `com.hw3.integration.config`
- `com.hw3.integration.exception`
- `com.hw3.integration.util`

### 3.4 找到带注解的类并注册为 Bean

例如：

- `@RestController`
  - `src/main/java/com/hw3/integration/controller/IntegrationController.java:12`
- `@Service`
  - `src/main/java/com/hw3/integration/service/IntegrationOrchestrator.java:20`
  - `src/main/java/com/hw3/integration/service/XmlIntegrationEngine.java:33`
- `@Component`
  - `src/main/java/com/hw3/integration/service/HttpDepartmentGateway.java:13`
  - `src/main/java/com/hw3/integration/util/XmlMessageBuilder.java:8`
  - `src/main/java/com/hw3/integration/util/XmlEnvelopeParser.java:21`
- `@Configuration`
  - `src/main/java/com/hw3/integration/config/RestTemplateConfig.java:8`
- `@RestControllerAdvice`
  - `src/main/java/com/hw3/integration/exception/GlobalExceptionHandler.java:12`

这些类会被 Spring 自动创建并纳入容器管理。

### 3.5 自动注入依赖

比如：

- `IntegrationController` 依赖 `IntegrationOrchestrator`
- `IntegrationOrchestrator` 又依赖：
  - `XmlIntegrationEngine`
  - `DepartmentGateway`
  - `XmlEnvelopeParser`
  - `XmlMessageBuilder`
  - `IntegrationProperties`

Spring 会根据构造器参数，把这些对象自动组装起来。

### 3.6 启动 Web 服务器并注册路由

由于项目引入了 Web 依赖，Spring Boot 会自动启动内嵌 Tomcat，并把控制器中的接口注册成 HTTP 路由。

例如：

- `GET /api/integration/health`
- `POST /api/integration/requests`

对应代码：

- `src/main/java/com/hw3/integration/controller/IntegrationController.java:13`

## 4. 配置文件加载

主配置文件位置：

- `src/main/resources/application.yml:1`

Spring Boot 启动时会自动读取这个文件。

里面当前定义了：

- 服务端口 `server.port`
- 院系下游接口地址 `integration.departments.*.endpoint`
- 各院系学生 / 课程 / 选课本地 XSD 路径

配置绑定类：

- `src/main/java/com/hw3/integration/config/IntegrationProperties.java:11`

启用绑定的位置：

- `src/main/java/com/hw3/integration/IntegrationServerApplication.java:9`

## 5. 当前项目结构说明

项目核心目录如下：

```text
src
├─ main
│  ├─ java
│  │  └─ com.hw3.integration
│  │     ├─ config
│  │     ├─ controller
│  │     ├─ exception
│  │     ├─ model
│  │     ├─ service
│  │     └─ util
│  └─ resources
│     ├─ application.yml
│     ├─ schemas
│     └─ xsl
└─ test
   └─ java
```

### 5.1 `config`

作用：

- 放配置类和 Bean 定义

主要文件：

- `src/main/java/com/hw3/integration/config/IntegrationProperties.java:11`
  - 负责读取 `application.yml` 中的院系配置
- `src/main/java/com/hw3/integration/config/RestTemplateConfig.java:8`
  - 提供 `RestTemplate` Bean 给网关调用下游院系接口

### 5.2 `controller`

作用：

- 对外暴露 HTTP 接口

主要文件：

- `src/main/java/com/hw3/integration/controller/IntegrationController.java:12`
  - 提供健康检查接口
  - 提供统一 XML 请求入口

### 5.3 `exception`

作用：

- 自定义异常类型
- 统一异常响应

主要文件：

- `src/main/java/com/hw3/integration/exception/GlobalExceptionHandler.java:12`

### 5.4 `model`

作用：

- 放请求 / 响应对象和枚举定义

主要文件：

- `src/main/java/com/hw3/integration/model/ActionType.java:3`
- `src/main/java/com/hw3/integration/model/IntegrationRequest.java:8`
- `src/main/java/com/hw3/integration/model/IntegrationResponse.java:7`
- `src/main/java/com/hw3/integration/model/DepartmentCode.java:5`
- `src/main/java/com/hw3/integration/model/PayloadType.java:3`

### 5.5 `service`

作用：

- 放核心业务逻辑

主要文件：

- `src/main/java/com/hw3/integration/service/IntegrationOrchestrator.java:20`
  - 负责按 `action` 分发请求
  - 负责跨系选课 / 退课 / 共享课程处理
- `src/main/java/com/hw3/integration/service/XmlIntegrationEngine.java:33`
  - 负责 XSD 校验、XSLT 转换、统一格式合并
- `src/main/java/com/hw3/integration/service/HttpDepartmentGateway.java:13`
  - 负责把 XML 请求发给 A / B / C 院系系统

### 5.6 `util`

作用：

- 放 XML 报文构造、解析、通用工具

主要文件：

- `src/main/java/com/hw3/integration/util/XmlMessageBuilder.java:8`
  - 把对象组装成 XML 信封
- `src/main/java/com/hw3/integration/util/XmlEnvelopeParser.java:21`
  - 从 XML 信封中解析请求头、动作和 body 片段
- `src/main/java/com/hw3/integration/util/XmlSupport.java:1`
  - 提供 XML 处理通用能力

### 5.7 `resources/schemas`

作用：

- 存放 XML Schema

包含：

- 请求 / 响应信封 XSD
- 统一格式 XSD
- A / B / C 三个院系本地格式 XSD

### 5.8 `resources/xsl`

作用：

- 存放 XSLT 转换规则

包含：

- 本地格式转统一格式
- 统一格式转 A / B / C 本地格式

### 5.9 `test`

作用：

- 存放测试代码

当前已有：

- `src/test/java/com/hw3/integration/service/XmlIntegrationEngineTest.java:1`

## 6. 启动后的整体工作流程

简化后的运行链路如下：

1. 执行 `IntegrationServerApplication.main`
2. Spring Boot 创建应用上下文
3. 扫描并注册 `controller`、`service`、`component`、`config`
4. 装配依赖对象
5. 启动内嵌 Tomcat
6. 注册 `/api/integration/...` 路由
7. 开始接收 XML 请求

