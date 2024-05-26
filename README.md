# PowerJobAuto
PowerJob自动注册优化工具，只需添加注解和简单的配置，即可实现应用的自动注册，Job任务的自动配置。

## 快速开始
1. 添加依赖,使用最新的版本号。
```xml
        <dependency>
            <groupId>io.github.teoan</groupId>
            <artifactId>power-job-auto-spring-boot-starter</artifactId>
            <version>${project.version}</version>
        </dependency>
```

2. 在SpringBoot配置中添加配置。如application.yml,根据自己的需要配置注册任务的创建更新删除策略。
```yaml
powerjob:
  worker:
    enabled: true
    server-address: localhost:7700
    app-name: ${spring.application.name}
    password: 123456
    protocol: http
    port: 27773
    allowLazyConnectServer: true
    auto-register:  #自动注册相关配置
      enabled: true
      create: true
      update: true
      delete: true
    log:
      type: ONLINE  #设置日志类型，ONLINE表示使用PowerJob的日志系统，OFFLINE表示使用SpringBoot的日志系统
```
3. 在powerjob处理器（Processor）使用 **@PowerJobAutoRegister** 注解, 自动注册到powerjob中。如下：
```java
/**
 * @author Teoan
 * @since 2024/5/10 下午5:02
 */
@PowerJobAutoRegister(jobName = "SamplesBasicProcessor",
        jobDescription = "BasicProcessorDemo",
        timeExpression = "*/10 * * * * *",
        jobParams="{\"test1\":\"11111\"}")
public class SamplesBasicProcessor implements BasicProcessor {


    @Override
    public ProcessResult process(TaskContext taskContext) throws Exception {

        // 在线日志功能，可以直接在控制台查看任务日志，非常便捷
        OmsLogger omsLogger = taskContext.getOmsLogger();
        omsLogger.info("SamplesBasicProcessor start to process, current JobParams is {}.", taskContext.getJobParams());

        // TaskContext为任务的上下文信息，包含了在控制台录入的任务元数据，常用字段为
        // jobParams（任务参数，在控制台录入），instanceParams（任务实例参数，通过 OpenAPI 触发的任务实例才可能存在该参数）


        // 返回结果，该结果会被持久化到数据库，在前端页面直接查看，极为方便
        return new ProcessResult(true, "result is xxx");

    }
}
```
具体例子可以查看power-job-auto-spring-boot-samples模块。
PowerJobAutoRegister配置内容可以参考powerjob[官方文档](https://www.yuque.com/powerjob/guidence/ysug77#mNarp:~:text=%E4%B8%BB%E7%95%8C%E9%9D%A2-,%E6%96%B0%E5%A2%9E%E4%BB%BB%E5%8A%A1%E7%95%8C%E9%9D%A2%EF%BC%88%E6%95%99%E7%A8%8B%EF%BC%8C%E6%8E%A8%E8%8D%90%E4%BB%94%E7%BB%86%E9%98%85%E8%AF%BB%EF%BC%89,-%E5%A4%8D%E5%88%B6%E4%BB%BB%E5%8A%A1)。

4. 启动项目，愉快地使用吧！
