package org.jeecg.modules.stat.ai;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.stat.mapper.StatMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * LlmClient 大模型调用封装（#E-56，任涵艺；A/B组同款规范：指导书任务4特殊任务）
 *
 * 四条硬规则：
 * 1. 密钥配置化：sys_param(psms.llm.apiKey) → 环境变量 LLM_API_KEY 兜底，严禁硬编码/入仓；
 * 2. 超时与重试：连接+读取各3s，失败重试1次；
 * 3. 调用审计：每次调用（含失败）写 bu_ai_call_log（DDL在 db/v2_upgrade.sql §6）；
 * 4. 结构化输出：chatForJson 强制JSON，解析失败→按失败降级，禁止非法JSON透传前端。
 */
@Slf4j
@Component
public class LlmClient {

    private static final int TIMEOUT_MS = 3000;

    @Autowired
    private StatMapper statMapper;

    /** 问答（自然语言输出）；null=调用失败，调用方自行降级 */
    public String chat(String scene, String prompt) {
        return doCall(scene, prompt, false);
    }

    /** 结构化输出（强制JSON）；解析失败返回null（降级） */
    public JSONObject chatForJson(String scene, String prompt) {
        String raw = doCall(scene, prompt, true);
        if (raw == null) {
            return null;
        }
        try {
            return JSON.parseObject(raw);
        } catch (Exception e) {
            audit(scene, prompt, raw, "JSON_PARSE_FAIL");
            return null;
        }
    }

    private String doCall(String scene, String prompt, boolean jsonMode) {
        String apiKey = resolveKey();
        if (apiKey == null || apiKey.isEmpty()) {
            audit(scene, prompt, null, "NO_API_KEY");
            return null;
        }
        String baseUrl = statMapper.selectConfigValue("psms.llm.baseUrl");
        if (baseUrl == null || baseUrl.isEmpty()) {
            audit(scene, prompt, null, "NO_BASE_URL");
            return null;
        }
        RestTemplate rest = buildRest();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + apiKey);

        Map<String, Object> body = new HashMap<>(8);
        body.put("model", statMapper.selectConfigValue("psms.llm.model"));
        Map<String, Object> message = new HashMap<>(4);
        message.put("role", "user");
        message.put("content", prompt + (jsonMode ? "\n严格只输出JSON，不要输出任何其他文字。" : ""));
        body.put("messages", Collections.singletonList(message));
        if (jsonMode) {
            Map<String, Object> fmt = new HashMap<>(4);
            fmt.put("type", "json_object");
            body.put("response_format", fmt);
        }

        for (int attempt = 1; attempt <= 2; attempt++) {
            long start = System.currentTimeMillis();
            try {
                ResponseEntity<String> resp = rest.postForEntity(baseUrl,
                        new HttpEntity<>(JSON.toJSONString(body), headers), String.class);
                long cost = System.currentTimeMillis() - start;
                if (resp.getStatusCode().is2xxSuccessful() && resp.getBody() != null) {
                    String content = extractContent(resp.getBody());
                    if (content != null) {
                        audit(scene, prompt, content, "OK:" + cost + "ms");
                        return content;
                    }
                    audit(scene, prompt, resp.getBody(), "EMPTY_CONTENT");
                } else {
                    audit(scene, prompt, resp.getBody(), "HTTP_" + resp.getStatusCodeValue());
                }
            } catch (Exception e) {
                audit(scene, prompt, e.getMessage(), "ATTEMPT_" + attempt + "_FAIL");
            }
        }
        return null; // 两次均失败，调用方降级
    }

    /** 兼容 OpenAI 风格 choices[0].message.content 与 output_text 两种返回结构 */
    private String extractContent(String raw) {
        try {
            JSONObject root = JSON.parseObject(raw);
            if (root.getJSONArray("choices") != null && root.getJSONArray("choices").size() > 0) {
                JSONObject message = root.getJSONArray("choices").getJSONObject(0).getJSONObject("message");
                return message == null ? null : message.getString("content");
            }
            return root.getString("output_text");
        } catch (Exception e) {
            return null;
        }
    }

    private String resolveKey() {
        String key = statMapper.selectConfigValue("psms.llm.apiKey");
        if (key == null || key.isEmpty()) {
            key = System.getenv("LLM_API_KEY"); // 环境变量兜底（课程密钥由教师发放，严禁入仓）
        }
        return key;
    }

    /** 审计落库（失败也记录；审计失败不影响主流程） */
    private void audit(String scene, String prompt, String output, String status) {
        try {
            statMapper.insertAiCallLog(
                    scene,
                    prompt != null && prompt.length() > 2000 ? prompt.substring(0, 2000) : prompt,
                    output != null && output.length() > 4000 ? output.substring(0, 4000) : output,
                    status);
        } catch (Exception e) {
            log.error("[LlmClient] 审计落库失败（不影响主流程）：{}", e.getMessage());
        }
    }

    private RestTemplate buildRest() {
        SimpleClientHttpRequestFactory f = new SimpleClientHttpRequestFactory();
        f.setConnectTimeout(TIMEOUT_MS);
        f.setReadTimeout(TIMEOUT_MS);
        return new RestTemplate(f);
    }
}
