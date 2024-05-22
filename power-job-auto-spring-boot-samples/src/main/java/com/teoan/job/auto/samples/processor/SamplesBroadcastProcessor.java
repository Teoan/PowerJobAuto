package com.teoan.job.auto.samples.processor;

import com.teoan.job.auto.core.annotation.PowerJobAutoRegister;
import tech.powerjob.common.enums.LogLevel;
import tech.powerjob.common.enums.TimeExpressionType;
import tech.powerjob.worker.core.processor.ProcessResult;
import tech.powerjob.worker.core.processor.TaskContext;
import tech.powerjob.worker.core.processor.TaskResult;
import tech.powerjob.worker.core.processor.sdk.BroadcastProcessor;
import tech.powerjob.worker.log.OmsLogger;

import java.util.List;

/**
 * @author Teoan
 * @since 2024/5/19 下午10:38
 */
@PowerJobAutoRegister(jobName = "SamplesBroadcastProcessor",
        jobDescription = "BroadcastProcessor",
        timeExpressionType = TimeExpressionType.FIXED_DELAY,
        timeExpression = "10000",
        jobParams="{\"test1\":\"11111\"}",
lifeCycle = "{\"start\":1714492800000,\"end\":1717776000000}",
maxInstanceNum = 1,
instanceRetryNum = 2,
instanceTimeLimit = 3,
maxWorkerCount = 4,
logLevel = LogLevel.ERROR)
public class SamplesBroadcastProcessor implements BroadcastProcessor {

    @Override
    public ProcessResult preProcess(TaskContext taskContext) throws Exception {
        // 预执行，会在所有 worker 执行 process 方法前调用
        OmsLogger omsLogger = taskContext.getOmsLogger();
        omsLogger.info("SamplesBroadcastProcessor start to process, current JobParams is {}.", taskContext.getJobParams());
        return new ProcessResult(true, "init success ");
    }

    @Override
    public ProcessResult process(TaskContext context) throws Exception {
        // 撰写整个worker集群都会执行的代码逻辑
        return new ProcessResult(true, "release resource success");
    }

    @Override
    public ProcessResult postProcess(TaskContext taskContext, List<TaskResult> taskResults) throws Exception {

        // taskResults 存储了所有worker执行的结果（包括preProcess）

        // 收尾，会在所有 worker 执行完毕 process 方法后调用，该结果将作为最终的执行结果
        return new ProcessResult(true, "process success");
    }

}
