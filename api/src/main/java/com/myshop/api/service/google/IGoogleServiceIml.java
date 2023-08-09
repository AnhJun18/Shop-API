package com.myshop.api.service.google;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.fluent.Form;
import org.apache.http.client.fluent.Request;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.io.IOException;
@Service
public class IGoogleServiceIml implements IGoogleService{
    @Autowired
    private Environment env;

    public String getToken(final String code) throws ClientProtocolException, IOException {
        String response = Request.Post(env.getProperty("GOOGLE_LINK_GET_TOKEN"))
                .bodyForm(Form.form().add("client_id",env.getProperty("GOOGLE_CLIENT_ID"))
                        .add("client_secret",env.getProperty("GOOGLE_CLIENT_SECRET") )
                        .add("redirect_uri",env.getProperty("GOOGLE_REDIRECT_URI"))
                        .add("code", code)
                        .add("grant_type","authorization_code").build())
                .execute().returnContent().asString();

        JsonObject job = new Gson().fromJson(response, JsonObject.class);
        String accessToken = job.get("access_token").toString().replaceAll("\"", "");
        return accessToken;
    }

    public GooglePojo getUserInfo(final String accessToken) throws ClientProtocolException, IOException {
        String link = env.getProperty("GOOGLE_LINK_GET_USER") + accessToken;
        String response = Request.Get(link).execute().returnContent().asString();
        GooglePojo googlePojo = new Gson().fromJson(response, GooglePojo.class);
        return googlePojo;
    }


}
