# Bootapp

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Java Version](https://img.shields.io/badge/Java-17+-blue.svg)](https://openjdk.org/)
[![Jetty](https://img.shields.io/badge/Jetty-11.0.11-green.svg)](https://www.eclipse.org/jetty/)

[English](README.md)

**Bootapp** 是一个轻量级的 Java Web 应用热更新框架。它通过支持单独更新业务 JAR 包（通常 < 1MB）来实现快速迭代，无需重启整个应用，避免了重新部署臃肿的完整应用包（通常 > 100MB）。

## 截图

![管理控制台](doc/intro.png)

管理控制台提供以下功能：
- **JAR 管理** - 查看所有已加载的业务 JAR，包括修改时间和文件路径
- **热更新** - 点击"替换"按钮上传新的 JAR，系统会自动重新加载
- **实时日志** - 通过 WebSocket 连接实时查看应用日志

---

## 特性

- **热更新** - 无需重启服务器即可更新业务 JAR
- **轻量部署** - 仅部署业务代码（< 1MB），而非完整包（> 100MB）
- **文件监控** - JAR 文件变更时自动重载
- **管理控制台** - 用于 JAR 管理和实时日志的 Web UI
- **类加载器隔离** - 业务代码与框架代码隔离
- **Spring Boot 集成** - 与 Spring Boot 3.x 无缝集成

---

## 架构

```
┌─────────────────────────────────────────────────────────┐
│                    Bootapp Server                        │
├─────────────────────────────────────────────────────────┤
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────────┐  │
│  │ 管理控制台   │  │ 文件监控     │  │ WebSocket      │  │
│  │             │  │             │  │ 日志流          │  │
│  └─────────────┘  └─────────────┘  └─────────────────┘  │
├─────────────────────────────────────────────────────────┤
│                   BootContext (重载管理器)               │
├─────────────────────────────────────────────────────────┤
│  ┌─────────────────────────────────────────────────┐    │
│  │            AppClassLoader (隔离加载)             │    │
│  │  ┌─────────┐  ┌─────────┐  ┌─────────┐         │    │
│  │  │ JAR 1   │  │ JAR 2   │  │ JAR N   │  ...    │    │
│  │  └─────────┘  └─────────┘  └─────────┘         │    │
│  └─────────────────────────────────────────────────┘    │
├─────────────────────────────────────────────────────────┤
│                 Jetty Server (定制版)                    │
└─────────────────────────────────────────────────────────┘
```

---

## 快速开始

### 环境要求

- Java 17+
- Maven 3.6+

### 构建

```bash
git clone https://github.com/gezhigang000/bootapp.git
cd bootapp
mvn clean install
```

### 运行

```java
public static void main(String[] args) {
    // 配置 JAR 目录
    EmbeddedAppConfig embeddedAppConfig = new EmbeddedAppConfig();
    embeddedAppConfig.setLibAbsoluteDirs(new String[]{
        "/path/to/your/business-jars"
    });

    // 创建服务器配置
    ServerConfig serverConfig = new ServerConfig(
        AppRunMode.embedded,
        embeddedAppConfig
    );
    serverConfig.setSpringConfigLocations(new String[]{
        "cc.starapp.bootapp.core.admin"
    });
    serverConfig.setSpringProfileActive("daily");

    // 启动服务器
    final BootServer server = new BootServer(serverConfig);
    server.start();
}
```

---

## 创建业务 JAR

### 1. 实现 HowInstall 接口

```java
public class MyAppInstall implements HowInstall {

    @Override
    public String appName() {
        return "my-business-app";
    }

    @Override
    public String appWebContext() {
        return "/api";
    }

    @Override
    public String[] basePackages() {
        return new String[]{"com.example.myapp"};
    }

    @Override
    public Class<?> appClass() {
        return MyAppConfig.class;
    }
}
```

### 2. 通过 ServiceLoader 注册

创建文件：`META-INF/services/cc.starapp.bootapp.core.HowInstall`

```
com.example.myapp.MyAppInstall
```

### 3. 打包部署

```bash
mvn clean package
cp target/my-business-app.jar /path/to/monitored/directory/
```

服务器将自动检测并重载新的 JAR。

---

## 配置

### ServerConfig 配置选项

| 属性 | 默认值 | 描述 |
|------|--------|------|
| `port` | 8080 | 服务端口 |
| `host` | 0.0.0.0 | 绑定地址 |
| `contextPath` | / | 上下文路径 |
| `adminMapping` | /admin/* | 管理控制台 URL |
| `minThreads` | 100 | 最小线程数 |
| `maxThreads` | 500 | 最大线程数 |
| `appRunMode` | embedded | 运行模式：embedded/standalone |

### EmbeddedAppConfig 配置选项

| 属性 | 描述 |
|------|------|
| `libAbsoluteDirs` | JAR 扫描目录 |
| `includeByNameRegex` | 包含 JAR 的正则表达式 |
| `excludeByNameRegex` | 排除 JAR 的正则表达式 |

---

## 管理控制台

访问管理控制台：`http://localhost:8080/admin/app`

### 功能

- **查看已加载 JAR** - 查看所有当前加载的业务 JAR
- **上传 JAR** - 上传新的 JAR 文件进行热更新
- **手动重载** - 手动触发重载
- **实时日志** - 通过 WebSocket 查看应用日志

### REST API

| 端点 | 方法 | 描述 |
|------|------|------|
| `/admin/app` | GET | 列出已加载的 JAR |
| `/admin/jar/upload` | POST | 上传 JAR 文件 |
| `/admin/reInstall` | GET | 触发重载 |
| `/admin/hi` | GET | 健康检查 |

---

## 项目结构

```
bootapp/
├── bootapp-core/        # 核心框架
│   ├── boot/            # 服务器和上下文管理
│   ├── app/             # 类加载器和 JAR 管理
│   └── admin/           # 管理控制台
├── bootapp-starter/     # 应用入口
├── bootapp-support/     # Feign 及工具类
├── bootapp-example/     # 示例应用
└── bootapp-example-service/  # 示例服务
```

---

## 工作原理

1. **初始化** - BootServer 使用自定义配置启动 Jetty
2. **JAR 加载** - AppJarHolder 扫描配置目录中的 JAR
3. **类加载器创建** - AppClassLoader 隔离加载业务类
4. **Spring 上下文** - EmbeddedApplicationContext 为业务代码创建 Spring 上下文
5. **文件监控** - FileListener 监控 JAR 变更
6. **热更新** - 检测到变更时，BootContext 销毁旧上下文并创建新上下文

---

## 依赖

- Jetty 11.0.11
- Spring Boot 3.0.5
- FastJSON
- Apache Commons IO
- Logback 1.4.5
- OpenFeign 12.1

---

## 许可证

本项目采用 MIT 许可证 - 详见 [LICENSE](LICENSE) 文件。

---

## 贡献

欢迎贡献！请随时提交 Pull Request。

1. Fork 仓库
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送分支 (`git push origin feature/AmazingFeature`)
5. 提交 Pull Request
