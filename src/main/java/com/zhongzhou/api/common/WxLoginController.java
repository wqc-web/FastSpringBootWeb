package com.zhongzhou.api.common;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zhongzhou.api.entity.SysUser;
import com.zhongzhou.api.mapper.SysUserMapper;
import com.zhongzhou.api.service.ISysUserService;
import com.zhongzhou.api.service.IWxLoginService;
import com.zhongzhou.common.bean.ReturnEntity;
import com.zhongzhou.common.bean.ReturnEntityError;
import com.zhongzhou.common.bean.ReturnEntitySuccess;
import com.zhongzhou.common.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashMap;

@Slf4j
@RestController
@RequestMapping("/wx")
public class WxLoginController {

    @Resource
    private IWxLoginService wxLoginService;
    @Resource
    private ISysUserService userService;
    @Resource
    private SysUserMapper userMapper;
    @Resource
    private TokenController tokenController;


    @GetMapping("/login")
    public ReturnEntity wxLogin(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        try {
            //先获取请求里的code
            String code = request.getParameter("code");
            //拿token
            String token = WxConstant.WX_TOKEN_URL + "?corpid="
                    + WxConstant.WX_APP_ID + "&corpsecret=" + WxConstant.WX_SECRET;
            //请求接口
            HttpClientResult result = HttpClientUtils.getMethod(token);
            JSONObject jsonObject = JSON.parseObject(result.getResult());
            log.info("获取access_token中...{}" + jsonObject);
            //再根据code和token拿userId
            //获取用户详细信息
            String accessToken = jsonObject.getString("access_token");
            String userInfoUrl = WxConstant.WX_USER_INFO_URL + "?access_token=" +
                    accessToken + "&code=" + code;
            //请求接口
            HttpClientResult userInfoResult = HttpClientUtils.getMethod(userInfoUrl);
            JSONObject userJsonobject = JSON.parseObject(userInfoResult.getResult());
            log.info("正在获取用户id{}" + userJsonobject);
            //定义用户对象
            SysUser sysUser = null;
            //获取userId
            String unionId = userJsonobject.getString("UserId");
            if (!StringUtils.isEmpty(unionId)) {
                String getInfoUrl = WxConstant.WX_GET_USER_URL + "?access_token=" +
                        accessToken + "&userid=" + unionId;
                //请求接口
                HttpClientResult userInfo = HttpClientUtils.getMethod(getInfoUrl);
                JSONObject jsonObject1 = JSON.parseObject(userInfo.getResult());
                //根据unionId查用户信息
                sysUser = userMapper.findDetailByUnionId(unionId);
                if (sysUser == null) {
                    sysUser = new SysUser();
                    //注册用户，并且返回sysUser

                    //uuid转换为openId
//                    String uTO0penUrl = WxConstant.WX_UID_TO_OPENID + "access_token=" + accessToken;
//                    String json = "{\"userid\": \"%s\"}";
//                    String format = String.format(json, unionId);
//                    HttpClientResult result1 = HttpClientUtils.postMethod(uTO0penUrl, format);
//                    JSONObject jsonObject2 = JSON.parseObject(result1.getResult());
//                    String openId = jsonObject2.getString("openid");
//                    sysUser.setOpenId(openId);

                    sysUser.setRealName(jsonObject1.getString("name"));
                    sysUser.setPhone(jsonObject1.getString("mobile"));
                    sysUser.setSex(jsonObject1.getInteger("gender"));
                    sysUser.setHeadImg(jsonObject1.getString("avatar"));
                    sysUser.setUnionId(unionId);
                    userMapper.insert(sysUser);
                    //赋角色 TODO
                }
                //查询用户详细信息
                sysUser = userService.findDetailById(sysUser.getId());
                //存token
                String authorization = JwtUtil.generateToken(sysUser.getId().toString());
                tokenController.set(authorization, sysUser.getId().toString(), Constants.DEFAULT_EXPIRE_SECOND);
                //jump url
                response.sendRedirect(Constants.DOMAIN_NAME + "/" + Constants.MOBILE_PROJECT_NAME + "?token=" + authorization);
                //return data
                HashMap<String, Object> resultMap = new HashMap<>();
                resultMap.put("authorization", authorization);
                resultMap.put("sysUser", sysUser);
                return new ReturnEntitySuccess(Constants.MSG_FIND_SUCCESS, resultMap);
            } else {
                return new ReturnEntityError("获取UserId失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ReturnEntityError(e.getMessage());
        }
    }

    @GetMapping("/sendTpMessage")
    public ReturnEntity sendTpMessage(@RequestParam("wxId") String wxId, @RequestParam("content") String content) {
        wxLoginService.sendTpMessage(wxId, content,null,null);
        return new ReturnEntitySuccess("发送模板消息成功");
    }

}
