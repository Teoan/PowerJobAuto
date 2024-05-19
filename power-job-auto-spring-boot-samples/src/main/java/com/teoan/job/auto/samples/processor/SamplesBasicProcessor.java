package com.teoan.job.auto.samples.processor;

import com.teoan.job.auto.core.annotation.PowerJobAutoRegister;
import tech.powerjob.worker.core.processor.ProcessResult;
import tech.powerjob.worker.core.processor.TaskContext;
import tech.powerjob.worker.core.processor.sdk.BasicProcessor;
import tech.powerjob.worker.log.OmsLogger;

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
