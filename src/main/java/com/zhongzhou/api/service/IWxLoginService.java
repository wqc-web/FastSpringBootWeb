package com.zhongzhou.api.service;

public interface IWxLoginService {
    void sendTpMessage(String wxId, String content,Long id,Long type);
}
