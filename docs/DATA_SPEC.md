# 集成教务系统数据规范文档

本文档说明项目里用到的三层 XML 数据规范：

- 集成请求 / 响应信封
- 统一格式（集成服务器内部中间格式）
- A / B / C 三个院系本地格式

## 1. 目录总览

### 1.1 信封规范

- 请求：`src/main/resources/schemas/integration-request.xsd:3`
- 响应：`src/main/resources/schemas/integration-response.xsd:3`

### 1.2 统一格式规范

- 学生：`src/main/resources/schemas/unified/formatStudent.xsd:2`
- 课程：`src/main/resources/schemas/unified/formatClass.xsd:2`
- 选课：`src/main/resources/schemas/unified/formatClassChoice.xsd:2`

### 1.3 本地格式规范

- A 系学生：`src/main/resources/schemas/local/studentA.xsd:2`
- A 系课程：`src/main/resources/schemas/local/classA.xsd:2`
- A 系选课：`src/main/resources/schemas/local/choiceA.xsd:2`
- B 系学生：`src/main/resources/schemas/local/studentB.xsd:2`
- B 系课程：`src/main/resources/schemas/local/classB.xsd:2`
- B 系选课：`src/main/resources/schemas/local/choiceB.xsd:2`
- C 系学生：`src/main/resources/schemas/local/studentC.xsd:2`
- C 系课程：`src/main/resources/schemas/local/classC.xsd:2`
- C 系选课：`src/main/resources/schemas/local/choiceC.xsd:2`

### 1.4 转换规则

- 本地 / 统一互转规则在：`src/main/resources/xsl`

## 2. 统一格式定义

统一格式只用于集成服务器内部转换、聚合、路由。

### 2.1 学生统一格式

根节点：`students`

单个学生节点：`student`

字段：

| 字段 | 必填 | 说明 |
| --- | --- | --- |
| `id` | 是 | 学号 / 学生编号 |
| `name` | 是 | 姓名 |
| `sex` | 否 | 性别 |
| `major` | 否 | 院系 / 专业 |

示例：

```xml
<students>
    <student>
        <id>2024001</id>
        <name>张三</name>
        <sex>男</sex>
        <major>计算机学院</major>
    </student>
</students>
```

### 2.2 课程统一格式

根节点：`classes`

单个课程节点：`class`

字段：

| 字段 | 必填 | 说明 |
| --- | --- | --- |
| `id` | 是 | 课程编号 |
| `name` | 是 | 课程名称 |
| `time` | 否 | 课时 / 上课时间 |
| `score` | 是 | 学分 |
| `teacher` | 是 | 教师 |
| `location` | 是 | 地点 |
| `share` | 否 | 是否共享 |

示例：

```xml
<classes>
    <class>
        <id>B001</id>
        <name>数据集成</name>
        <time>32</time>
        <score>2</score>
        <teacher>李老师</teacher>
        <location>教一101</location>
        <share>Y</share>
    </class>
</classes>
```

### 2.3 选课统一格式

根节点：`choices`

单个选课节点：`choice`

字段：

| 字段 | 必填 | 说明 |
| --- | --- | --- |
| `sid` | 是 | 学号 / 学生编号 |
| `cid` | 是 | 课程编号 |
| `score` | 否 | 成绩 |

示例：

```xml
<choices>
    <choice>
        <sid>2024001</sid>
        <cid>B001</cid>
        <score>95</score>
    </choice>
</choices>
```

## 3. A / B / C 本地格式对照

## 3.1 学生字段映射

| 语义 | 统一格式 | A 系 | B 系 | C 系 |
| --- | --- | --- | --- | --- |
| 学号 | `id` | `学号` | `学号` | `Sno` |
| 姓名 | `name` | `姓名` | `姓名` | `Snm` |
| 性别 | `sex` | `性别` | `性别` | `Sex` |
| 院系/专业 | `major` | `院系` | `专业` | `Sde` |

根节点：

| 类型 | 统一格式 | A / B / C 本地 |
| --- | --- | --- |
| 学生集合 | `students` | `Students` |

## 3.2 课程字段映射

| 语义 | 统一格式 | A 系 | B 系 | C 系 |
| --- | --- | --- | --- | --- |
| 课程编号 | `id` | `课程编号` | `编号` | `Cno` |
| 课程名称 | `name` | `课程名称` | `名称` | `Cnm` |
| 课时/时间 | `time` | 无 | `课时` | `Ctm` |
| 学分 | `score` | `学分` | `学分` | `Cpt` |
| 教师 | `teacher` | `授课老师` | `老师` | `Tec` |
| 地点 | `location` | `授课地点` | `地点` | `Pla` |
| 共享标记 | `share` | `共享` | `共享` | `Share` |

根节点：

| 类型 | 统一格式 | A / B / C 本地 |
| --- | --- | --- |
| 课程集合 | `classes` | `Classes` |

## 3.3 选课字段映射

| 语义 | 统一格式 | A 系 | B 系 | C 系 |
| --- | --- | --- | --- | --- |
| 学号 | `sid` | `学生编号` | `学号` | `Sno` |
| 课程编号 | `cid` | `课程编号` | `课程编号` | `Cno` |
| 成绩 | `score` | `成绩` | `得分` | `Grd` |

根节点：

| 类型 | 统一格式 | A / B / C 本地 |
| --- | --- | --- |
| 选课集合 | `choices` | `Choices` |

## 4. 转换方向

集成服务器内部转换链路如下：

1. 源院系本地格式
2. 转成统一格式
3. 如需转发到目标院系，再由统一格式转成目标院系本地格式

对应 XSL 文件如下：

### 4.1 本地转统一

- 学生：`src/main/resources/xsl/formatStudent.xsl:1`
- 课程：`src/main/resources/xsl/formatClass.xsl:1`
- 选课：`src/main/resources/xsl/formatClassChoice.xsl:1`

### 4.2 统一转 A

- 学生：`src/main/resources/xsl/studentToA.xsl:1`
- 课程：`src/main/resources/xsl/classToA.xsl:1`
- 选课：`src/main/resources/xsl/choiceToA.xsl:1`

### 4.3 统一转 B

- 学生：`src/main/resources/xsl/studentToB.xsl:1`
- 课程：`src/main/resources/xsl/classToB.xsl:1`
- 选课：`src/main/resources/xsl/choiceToB.xsl:1`

### 4.4 统一转 C

- 学生：`src/main/resources/xsl/studentToC.xsl:1`
- 课程：`src/main/resources/xsl/classToC.xsl:1`
- 选课：`src/main/resources/xsl/choiceToC.xsl:1`

## 5. 各动作的数据要求

| 动作 | body 要求 | 返回 body |
| --- | --- | --- |
| `COURSE_SHARE` | 可为空 | 返回课程集合 |
| `COURSE_SELECT` | 必须包含学生 + 选课 | 通常为空 |
| `COURSE_DROP` | 必须包含学生 + 选课 | 通常为空 |
| `QUERY_SHARED_COURSES` | 仅供集成服务器内部调用 | 返回课程集合 |

## 6. 组员对接建议

### 6.1 对院系后端成员

- 发往集成服务器的 XML 必须先对照本院系本地 XSD
- `COURSE_SELECT` / `COURSE_DROP` 必须一次性传学生 XML 和选课 XML
- 不要把统一格式直接发给集成服务器，除非你们统一改协议

### 6.2 对集成服务器成员

- 收到请求后先按请求信封 XSD 校验
- 再按 `sourceSystem` 选择对应本地 XSD 和 XSL
- 聚合课程时统一用 `classes` 统一格式做中间处理

### 6.3 对前端成员

- 前端不直接处理统一格式
- 前端始终只面对自己院系后端定义的本地数据结构

## 7. 当前结论

项目现在已经具备：

- 可直接对接的 HTTP 入口
- 可校验的 XML 请求 / 响应信封
- A / B / C 三套本地数据规范
- 一套统一中间格式规范
- 完整的本地 / 统一格式转换规则

因此现在可以把 `docs/API.md` 和 `docs/DATA_SPEC.md` 直接发给其它成员作为对照文档。
