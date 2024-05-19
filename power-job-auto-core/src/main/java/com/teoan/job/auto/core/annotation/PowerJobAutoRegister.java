package com.teoan.job.auto.core.annotation;

import org.springframework.stereotype.Component;
import tech.powerjob.common.enums.DispatchStrategy;
import tech.powerjob.common.enums.LogLevel;
import tech.powerjob.common.enums.LogType;
import tech.powerjob.common.enums.TimeExpressionType;
import tech.powerjob.common.model.LifeCycle;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Teoan
 * @since 2024/5/9 上午11:50
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface PowerJobAutoRegister {

    /**
     * 任务名称
     */
    String jobName() default "";


    /**
     * 任务描述
     */
    String jobDescription() default "";


    /**
     * 任务参数
     */
    String jobParams() default "";


    /**
     * 定时信息
     */
    TimeExpressionType timeExpressionType() default TimeExpressionType.CRON;


    /**
     * CRON 填写 CRON 表达式，秒级任务填写整数，API 无需填写
     */
    String timeExpression();


    /**
     * 生命周期 JSON
     */
    String lifeCycle() default "";


    /**
     * 运行配置
     */
    DispatchStrategy dispatchStrategy() default DispatchStrategy.HEALTH_FIRST;


    /**
     * 最大实例数
     */
    int maxInstanceNum() default 0;


    /**
     * 并发数
     */
    int concurrency () default 5;

    /**
     * 运行时间限制(毫秒)
     */
    long instanceTimeLimit() default 0;


    /**
     * Instance 重试次数
     */
    int instanceRetryNum() default 0;

    /**
     * Task 重试次数
     */
    int taskRetryNum() default 0;


    /**
     *  执行机器地址（可选，不指定代表全部；多值英文逗号分割）
     */
    String designatedWorkers() default "";


    /**
     * 最大执行机器数量
     */
    int maxWorkerCount() default 0;


    /**
     * 日志类型
     */
    LogType logType() default LogType.ONLINE;


    /**
     * 日志级别
     */
    LogLevel logLevel() default LogLevel.INFO;

    /**
     * Logger名称
     */
    String loggerName() default "";
}
