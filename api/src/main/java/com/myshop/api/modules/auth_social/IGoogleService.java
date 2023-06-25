package com.myshop.api.modules.auth_social;

import org.apache.http.client.ClientProtocolException;

import java.io.IOException;

public interface IGoogleService {
    public String getToken(final String code)throws ClientProtocolException, IOException;

    public GooglePojo getUserInfo(final String accessToken) throws ClientProtocolException, IOException;
}
