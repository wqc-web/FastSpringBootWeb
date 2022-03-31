package com.zhongzhou.api.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zhongzhou.api.service.IWxLoginService;
import com.zhongzhou.common.dto.Articles;
import com.zhongzhou.common.dto.News;
import com.zhongzhou.common.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;

@Slf4j
@Service
public class WxLoginServiceImpl implements IWxLoginService {

    @Resource
    private RedisUtil redisUtil;

    @Override
    public void sendTpMessage(String touser, String content, Long id, Long type) {
        //先去缓存找token
        String wxToken = redisUtil.get(WxConstant.WX_TOKEN);
        if (StringUtils.isEmpty(wxToken)) {
            //去拿token
            wxToken = getWxToken();
        }

        //推送消息
        String requestUrl = "https://qyapi.weixin.qq.com/cgi-bin/message/send?access_token=" + wxToken;
        System.out.println(requestUrl);
        HashMap<String, Object> map = new HashMap<>();
        map.put("touser", touser);
        map.put("agentid", WxConstant.WX_AGENT_ID);
        map.put("msgtype", "news");
        News news = new News();
        ArrayList<Articles> articleList = new ArrayList<>();

        Articles articles = new Articles();
        articles.setTitle(content);

        if (type == 0L) {
            articles.setDescription("备勤调换通知");
            String suffix = "/standBy/applicationInfo/" + id;
            articles.setUrl(Constants.DOMAIN_NAME + "/ServiceReportApp/index.html?url=" + suffix);

        } else {
            articles.setDescription("备勤调换通知");
            String suffix = "/standBy/leaderApproval/" + id;
            articles.setUrl(Constants.DOMAIN_NAME + "/ServiceReportApp/index.html?url=" + suffix);
        }

        articleList.add(articles);
        news.setArticles(articleList);
        map.put("news", news);

        String outputStr = JSONObject.toJSONString(map);
//        String postJson = "{\"touser\": \"%s\",\"agentid\":%s,\"msgtype\": \"%s\",\"news\":{\"content\": \"%s\"},\"safe\":0}";
//        String outputStr = String.format(postJson, touser, WxConstant.WX_AGENT_ID, "news", content);
        HttpClientResult httpClientResult = HttpClientUtils.postMethod(requestUrl, outputStr);
        String result = httpClientResult.getResult();
        JSONObject jsonObject = JSON.parseObject(result);
        if (jsonObject.getInteger("errcode") != 0) {
            throw new RuntimeException("发送推送消息失败");
        }
    }

    private String getWxToken() {
        String url = "https://qyapi.weixin.qq.com/cgi-bin/gettoken" +
                "?corpid=" + WxConstant.WX_APP_ID + "&corpsecret=" + WxConstant.WX_SECRET;    //企业微信
        try {
            HttpClientResult jsonResult = HttpClientUtils.getMethod(url);
            String result = jsonResult.getResult();
            JSONObject jsonObject = JSON.parseObject(result);
            String accessToken = jsonObject.getString("access_token");
            //存token
            redisUtil.set(WxConstant.WX_TOKEN, accessToken, WxConstant.WX_TOKEN_EXPIRE_TIME);

            return accessToken;
        } catch (Exception e) {
            log.error("获取微信access_token异常" + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }
}
