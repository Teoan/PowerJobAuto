package com.teoan.job.auto.core;


import com.alibaba.fastjson.JSON;
import com.teoan.job.auto.core.annotation.PowerJobAutoRegister;
import com.teoan.job.auto.core.client.PowerJobAutoClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import tech.powerjob.client.PowerJobClient;
import tech.powerjob.common.enums.ExecuteType;
import tech.powerjob.common.enums.ProcessorType;
import tech.powerjob.common.model.LifeCycle;
import tech.powerjob.common.model.LogConfig;
import tech.powerjob.common.request.http.SaveJobInfoRequest;
import tech.powerjob.common.response.JobInfoDTO;
import tech.powerjob.common.response.ResultDTO;
import tech.powerjob.worker.core.processor.sdk.BasicProcessor;
import tech.powerjob.worker.core.processor.sdk.BroadcastProcessor;
import tech.powerjob.worker.core.processor.sdk.MapProcessor;
import tech.powerjob.worker.core.processor.sdk.MapReduceProcessor;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Teoan
 * @description power-job 自动注册实现
 * @since 2024/05/07 15:57
 */
@Component
@Slf4j
public class JobAutoRegister {

    @Resource
    PowerJobAutoClient jobAutoClient;


    @Resource
    private ApplicationContext applicationContext;

    @Value("${powerjob.worker.auto-register.create}")
    private Boolean create;

    @Value("${powerjob.worker.auto-register.update}")
    private Boolean update;

    @Value("${powerjob.worker.auto-register.delete}")
    private Boolean delete;


    /**
     * 监听事件实现自动注册逻辑
     * 不影响主线程序启动速度，使用异步执行
     */
    @EventListener(ApplicationStartedEvent.class)
    @Async
    @Order(1)
    public void registerApp(ApplicationStartedEvent event) throws IOException {
        if (jobAutoClient.registerApp()) {
            log.info("[PowerJobAuto] register app success");
        } else {
            log.error("[PowerJobAuto] register app fail");
        }
    }


    /**
     * 自动配置Job任务
     */
    @EventListener(ApplicationStartedEvent.class)
    @Async
    @Order(2)
    public void AutoConfigTask(ApplicationStartedEvent event) {
        log.info("[PowerJobAuto] start auto config task");
        PowerJobClient powerJobClient = jobAutoClient.getPowerJobClient();
        List<Object> beanList = new ArrayList<>(applicationContext.getBeansWithAnnotation(PowerJobAutoRegister.class).values());
        SaveJobInfoRequest request = new SaveJobInfoRequest();
        beanList.forEach(bean -> {
            PowerJobAutoRegister powerJobAutoRegister = bean.getClass().getAnnotation(PowerJobAutoRegister.class);
            setJobInfo(request, powerJobAutoRegister, bean);
            if (ObjectUtils.isEmpty(request.getId()) && create) {
                ResultDTO<Long> longResultDTO = powerJobClient.saveJob(request);
                log.info("[PowerJobAuto] save job info:{}", JSON.toJSONString(longResultDTO));
            } else if (!ObjectUtils.isEmpty(request.getId()) && update) {
                powerJobClient.saveJob(request);
            }
        });
        if (delete) {
            List<JobInfoDTO> jobInfoList = jobAutoClient.getJobInfoList();
            // 本地的任务名称列表
            Set<String> localJobNameSet = beanList.stream().map(bean -> bean.getClass().getAnnotation(PowerJobAutoRegister.class).jobName()).collect(Collectors.toSet());
            jobInfoList.forEach(jobInfoDTO -> {
                if (!localJobNameSet.contains(jobInfoDTO.getJobName())) {
                    powerJobClient.deleteJob(jobInfoDTO.getId());
                }
            });
        }
    }


    private ExecuteType getExecuteType(Object bean) {
        Set<Class<?>> classSet = Arrays.stream(bean.getClass().getInterfaces()).collect(Collectors.toSet());
        if (classSet.contains(BasicProcessor.class)) {
            return ExecuteType.STANDALONE;
        } else if (classSet.contains(BroadcastProcessor.class)) {
            return ExecuteType.BROADCAST;
        } else if (classSet.contains(MapProcessor.class)) {
            return ExecuteType.MAP;
        } else if (classSet.contains(MapReduceProcessor.class)) {
            return ExecuteType.MAP_REDUCE;
        }
        throw new RuntimeException("不支持的Processor类型");
    }

    /**
     * 设置job属性
     */
    private void setJobInfo(SaveJobInfoRequest request, PowerJobAutoRegister powerJobAutoRegister, Object bean) {
        request.setEnable(true);
        request.setId(jobAutoClient.getJobId(powerJobAutoRegister.jobName()));
        request.setJobName(powerJobAutoRegister.jobName());
        request.setJobDescription(powerJobAutoRegister.jobDescription());
        request.setTimeExpressionType(powerJobAutoRegister.timeExpressionType());
        request.setTimeExpression(powerJobAutoRegister.timeExpression());
        request.setJobParams(powerJobAutoRegister.jobParams());
        request.setExecuteType(getExecuteType(bean));
        request.setLifeCycle(JSON.parseObject(powerJobAutoRegister.lifeCycle(), LifeCycle.class));
        request.setDispatchStrategy(powerJobAutoRegister.dispatchStrategy());
        request.setMaxInstanceNum(powerJobAutoRegister.maxInstanceNum());
        request.setConcurrency(powerJobAutoRegister.concurrency());
        request.setInstanceTimeLimit(powerJobAutoRegister.instanceTimeLimit());
        request.setInstanceRetryNum(powerJobAutoRegister.instanceRetryNum());
        request.setTaskRetryNum(powerJobAutoRegister.taskRetryNum());
        request.setDesignatedWorkers(powerJobAutoRegister.designatedWorkers());
        request.setMaxWorkerCount(powerJobAutoRegister.maxWorkerCount());
        request.setProcessorInfo(bean.getClass().getName());
        request.setProcessorType(ProcessorType.BUILT_IN);
        LogConfig logConfig = new LogConfig();
        logConfig.setLevel(powerJobAutoRegister.logLevel().getV());
        logConfig.setLoggerName(powerJobAutoRegister.loggerName());
        logConfig.setType(powerJobAutoRegister.logType().getV());
        request.setLogConfig(logConfig);
    }

}
