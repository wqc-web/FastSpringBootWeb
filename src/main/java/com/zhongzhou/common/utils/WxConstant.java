package com.zhongzhou.common.utils;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class WxConstant {

    public static String WX_APP_ID;
    public static String WX_SECRET;
    public static String WX_AGENT_ID;

    public final static String WX_TOKEN = "wxToken";
    public final static Integer WX_TOKEN_EXPIRE_TIME = 60 * 60;

    public static final String WX_TOKEN_URL = "https://qyapi.weixin.qq.com/cgi-bin/gettoken";

    public static final String WX_USER_INFO_URL = "https://qyapi.weixin.qq.com/cgi-bin/user/getuserinfo";

    public static final String WX_GET_USER_URL = "https://qyapi.weixin.qq.com/cgi-bin/user/get";

    public static final String WX_UID_TO_OPENID = "https://qyapi.weixin.qq.com/cgi-bin/user/convert_to_openid";

    @Value("${wx.appid}")
    public void setWxAppId(String wxAppId) {
        WX_APP_ID = wxAppId;
    }

    @Value("${wx.secret}")
    public void setWxSecret(String wxSecret) {
        WX_SECRET = wxSecret;
    }

    @Value("${wx.agentId}")
    public void setWxAgentId(String wxAgentId) {
        WX_AGENT_ID = wxAgentId;
    }
}
