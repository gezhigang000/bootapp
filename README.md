# Bootapp

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Java Version](https://img.shields.io/badge/Java-17+-blue.svg)](https://openjdk.org/)
[![Jetty](https://img.shields.io/badge/Jetty-11.0.11-green.svg)](https://www.eclipse.org/jetty/)

**Bootapp** is a lightweight hot-reload framework for Java web applications. It enables rapid iteration by allowing you to update business logic JARs (typically < 1MB) without restarting the entire application, eliminating the need to redeploy bloated full application packages (often > 100MB).

**Bootapp** 是一个轻量级的 Java Web 应用热更新框架。它通过支持单独更新业务 JAR 包（通常 < 1MB）来实现快速迭代，无需重启整个应用，避免了重新部署臃肿的完整应用包（通常 > 100MB）。

---

## Features | 特性

- **Hot Reload** - Update business JARs without server restart
  **热更新** - 无需重启服务器即可更新业务 JAR

- **Lightweight Deployment** - Deploy only business code (< 1MB) instead of full packages (> 100MB)
  **轻量部署** - 仅部署业务代码（< 1MB），而非完整包（> 100MB）

- **File Monitoring** - Automatic reload when JAR files change
  **文件监控** - JAR 文件变更时自动重载

- **Admin Console** - Web UI for JAR management and real-time logs
  **管理控制台** - 用于 JAR 管理和实时日志的 Web UI

- **ClassLoader Isolation** - Business code isolated from framework code
  **类加载器隔离** - 业务代码与框架代码隔离

- **Spring Boot Integration** - Seamless integration with Spring Boot 3.x
  **Spring Boot 集成** - 与 Spring Boot 3.x 无缝集成

---

## Architecture | 架构

```
┌─────────────────────────────────────────────────────────┐
│                    Bootapp Server                        │
├─────────────────────────────────────────────────────────┤
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────────┐  │
│  │ Admin       │  │ File        │  │ WebSocket       │  │
│  │ Console     │  │ Monitor     │  │ Log Streaming   │  │
│  └─────────────┘  └─────────────┘  └─────────────────┘  │
├─────────────────────────────────────────────────────────┤
│                   BootContext (Reload Manager)           │
├─────────────────────────────────────────────────────────┤
│  ┌─────────────────────────────────────────────────┐    │
│  │            AppClassLoader (Isolated)             │    │
│  │  ┌─────────┐  ┌─────────┐  ┌─────────┐         │    │
│  │  │ JAR 1   │  │ JAR 2   │  │ JAR N   │  ...    │    │
│  │  └─────────┘  └─────────┘  └─────────┘         │    │
│  └─────────────────────────────────────────────────┘    │
├─────────────────────────────────────────────────────────┤
│                 Jetty Server (Modified)                  │
└─────────────────────────────────────────────────────────┘
```

---

## Quick Start | 快速开始

### Requirements | 环境要求

- Java 17+
- Maven 3.6+

### Build | 构建

```bash
git clone https://github.com/your-org/bootapp.git
cd bootapp
mvn clean install
```

### Run | 运行

```java
public static void main(String[] args) {
    // Configure JAR directories | 配置 JAR 目录
    EmbeddedAppConfig embeddedAppConfig = new EmbeddedAppConfig();
    embeddedAppConfig.setLibAbsoluteDirs(new String[]{
        "/path/to/your/business-jars"
    });

    // Create server config | 创建服务器配置
    ServerConfig serverConfig = new ServerConfig(
        AppRunMode.embedded,
        embeddedAppConfig
    );
    serverConfig.setSpringConfigLocations(new String[]{
        "cc.starapp.bootapp.core.admin"
    });
    serverConfig.setSpringProfileActive("daily");

    // Start server | 启动服务器
    final BootServer server = new BootServer(serverConfig);
    server.start();
}
```

---

## Creating a Business JAR | 创建业务 JAR

### 1. Implement HowInstall Interface | 实现 HowInstall 接口

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

### 2. Register via ServiceLoader | 通过 ServiceLoader 注册

Create file | 创建文件: `META-INF/services/cc.starapp.bootapp.core.HowInstall`

```
com.example.myapp.MyAppInstall
```

### 3. Package and Deploy | 打包部署

```bash
mvn clean package
cp target/my-business-app.jar /path/to/monitored/directory/
```

The server will automatically detect and reload the new JAR.
服务器将自动检测并重载新的 JAR。

---

## Configuration | 配置

### ServerConfig Options | 服务器配置选项

| Property | Default | Description |
|----------|---------|-------------|
| `port` | 8080 | Server port / 服务端口 |
| `host` | 0.0.0.0 | Bind address / 绑定地址 |
| `contextPath` | / | Context path / 上下文路径 |
| `adminMapping` | /admin/* | Admin console URL / 管理控制台 URL |
| `minThreads` | 100 | Min thread pool size / 最小线程数 |
| `maxThreads` | 500 | Max thread pool size / 最大线程数 |
| `appRunMode` | embedded | Run mode: embedded/standalone / 运行模式 |

### EmbeddedAppConfig Options | 嵌入式应用配置选项

| Property | Description |
|----------|-------------|
| `libAbsoluteDirs` | Directories to scan for JARs / JAR 扫描目录 |
| `includeByNameRegex` | Regex to include JARs / 包含 JAR 的正则 |
| `excludeByNameRegex` | Regex to exclude JARs / 排除 JAR 的正则 |

---

## Admin Console | 管理控制台

Access the admin console at `http://localhost:8080/admin/`
访问管理控制台：`http://localhost:8080/admin/`

### Features | 功能

- **View Loaded JARs** - See all currently loaded business JARs
  **查看已加载 JAR** - 查看所有当前加载的业务 JAR

- **Upload JAR** - Upload new JAR files for hot-reload
  **上传 JAR** - 上传新的 JAR 文件进行热更新

- **Manual Reload** - Trigger reload manually
  **手动重载** - 手动触发重载

- **Real-time Logs** - View application logs via WebSocket
  **实时日志** - 通过 WebSocket 查看应用日志

### REST API

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/admin/app` | GET | List loaded JARs / 列出已加载的 JAR |
| `/admin/jar/upload` | POST | Upload JAR file / 上传 JAR 文件 |
| `/admin/reInstall` | GET | Trigger reload / 触发重载 |
| `/admin/hi` | GET | Health check / 健康检查 |

---

## Project Structure | 项目结构

```
bootapp/
├── bootapp-core/        # Core framework / 核心框架
│   ├── boot/            # Server & context management / 服务器和上下文管理
│   ├── app/             # ClassLoader & JAR management / 类加载器和 JAR 管理
│   └── admin/           # Admin console / 管理控制台
├── bootapp-starter/     # Application entry point / 应用入口
├── bootapp-support/     # Feign & utilities / Feign 及工具类
├── bootapp-example/     # Example application / 示例应用
└── bootapp-example-service/  # Example service / 示例服务
```

---

## How It Works | 工作原理

1. **Initialization** - BootServer starts Jetty with custom configuration
   **初始化** - BootServer 使用自定义配置启动 Jetty

2. **JAR Loading** - AppJarHolder scans configured directories for JARs
   **JAR 加载** - AppJarHolder 扫描配置目录中的 JAR

3. **ClassLoader Creation** - AppClassLoader loads business classes in isolation
   **类加载器创建** - AppClassLoader 隔离加载业务类

4. **Spring Context** - EmbeddedApplicationContext creates Spring context for business code
   **Spring 上下文** - EmbeddedApplicationContext 为业务代码创建 Spring 上下文

5. **File Monitoring** - FileListener watches for JAR changes
   **文件监控** - FileListener 监控 JAR 变更

6. **Hot Reload** - On change, BootContext destroys old context and creates new one
   **热更新** - 检测到变更时，BootContext 销毁旧上下文并创建新上下文

---

## Dependencies | 依赖

- Jetty 11.0.11
- Spring Boot 3.0.5
- FastJSON
- Apache Commons IO
- Logback 1.4.5
- OpenFeign 12.1

---

## License | 许可证

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
本项目采用 MIT 许可证 - 详见 [LICENSE](LICENSE) 文件。

---

## Contributing | 贡献

Contributions are welcome! Please feel free to submit a Pull Request.
欢迎贡献！请随时提交 Pull Request。

1. Fork the repository / Fork 仓库
2. Create your feature branch / 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. Commit your changes / 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch / 推送分支 (`git push origin feature/AmazingFeature`)
5. Open a Pull Request / 提交 Pull Request
