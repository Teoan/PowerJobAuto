package com.teoan.job.auto.core.client;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import tech.powerjob.client.PowerJobClient;
import tech.powerjob.common.response.JobInfoDTO;
import tech.powerjob.common.utils.HttpUtils;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.teoan.job.auto.core.constant.PowerJobAutoConstant.APP_INFO_LIST;
import static com.teoan.job.auto.core.constant.PowerJobAutoConstant.APP_INFO_SAVE;

/**
 * PowerJob操作客户端
 *
 * @author Teoan
 * @since 2024/5/8 下午10:17
 */
@Slf4j
@Component
public class PowerJobAutoClient {

    /**
     * 注册应用名称
     */
    @Value("${spring.application.name}")
    private String appName;

    /**
     * 注册密码
     */
    @Value("${powerjob.worker.password}")
    private String password;

    /**
     * PowerJob server地址
     */
    @Value("${powerjob.worker.server-address}")
    private String serverAddress;

    /**
     * PowerJob客户端
     */
    private PowerJobClient powerJobClient;

    private List<String> addressList;


    private static final String URL_PATTERN = "http://%s%s";

    @PostConstruct
    void init() {
        this.addressList = Lists.newArrayList(serverAddress);
    }

    /**
     * 注册应用信息
     */
    public Boolean registerApp() throws IOException {
        for (String serverAddress : addressList) {
            if (!isAppRegistered(serverAddress)) {
                String url = String.format(URL_PATTERN, serverAddress, APP_INFO_SAVE);
                Map<String, String> body = new HashMap<>(2);
                body.put("appName", appName);
                body.put("password", password);
                RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"),
                        JSON.toJSONString(body));
                String resultJson = HttpUtils.post(url, requestBody);
                JSONObject result = JSON.parseObject(resultJson);
                if (result.getBoolean("success")) {
                    return true;
                }
            } else {
                log.info("serverAddress:[{}] appName:[{}] is already registered!", serverAddress, appName);
                return true;
            }
        }
        return false;
    }


    /**
     * 判断app是否已注册
     *
     * @return
     */
    private Boolean isAppRegistered(String serverAddress) throws IOException {
        String url = String.format(URL_PATTERN, serverAddress, APP_INFO_LIST) + "?condition=" + appName;
        // 模糊查询
        String resultJson = HttpUtils.get(url);
        JSONObject result = JSON.parseObject(resultJson);
        if (result.getBoolean("success")) {
            JSONArray jsonArray = result.getJSONArray("data");
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject appInfo = jsonArray.getJSONObject(i);
                if (appInfo.getString("appName").equals(appName)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 获取PowerJobClient
     */
    public PowerJobClient getPowerJobClient() {
        if(ObjectUtils.isEmpty(powerJobClient)){
            powerJobClient = new PowerJobClient(serverAddress, appName, password);
            return powerJobClient;
        }
        return powerJobClient;
    }


    /**
     * 获取jobId
     */
    public Long getJobId(String jobName) {
        PowerJobClient powerJobClient1 = getPowerJobClient();
        List<JobInfoDTO> jobInfoDTOS = powerJobClient1.fetchAllJob().getData();
        JobInfoDTO jobInfoDTO = jobInfoDTOS.stream().filter(jobInfo -> jobInfo.getJobName().equals(jobName)).findFirst().orElseGet(JobInfoDTO::new);
        return jobInfoDTO.getId();
    }

    /**
     * 获取已创建的jobName列表
     */
    public List<JobInfoDTO> getJobInfoList() {
        PowerJobClient powerJobClient1 = getPowerJobClient();
        return powerJobClient1.fetchAllJob().getData();
    }






}
