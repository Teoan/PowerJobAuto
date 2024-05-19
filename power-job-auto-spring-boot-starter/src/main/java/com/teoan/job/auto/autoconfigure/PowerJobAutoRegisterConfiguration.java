package com.teoan.job.auto.autoconfigure;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author Teoan
 * @since 2024/5/7 上午10:51
 */
@Configuration
@ConditionalOnProperty(name = "powerjob.worker.auto-register.enabled",havingValue = "true")
@ComponentScan("com.teoan.job.auto.core")
public class PowerJobAutoRegisterConfiguration {
}
