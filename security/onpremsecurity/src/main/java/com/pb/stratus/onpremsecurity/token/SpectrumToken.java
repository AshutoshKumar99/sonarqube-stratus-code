package com.pb.stratus.onpremsecurity.token;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

/**
 * Created by GU003DU on 14-Aug-18.
 */
public class SpectrumToken implements Serializable {
    public static final int TOKEN_EXPIRY_TIME = 0;

    @SerializedName("access_token")
    private String accessToken;

    @SerializedName("username")
    private String userId;

    @SerializedName("session")
    private String session;

    private Long tokenGenerationTime = System.currentTimeMillis();

    public Long getTokenGenerationTime() {
        return tokenGenerationTime;
    }

    public void setTokenGenerationTime(Long tokenGenerationTime) {
        this.tokenGenerationTime = tokenGenerationTime;
    }

    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public boolean isExpired() {
        //Don't generate before 28 mins giving buffer of 2 mins as response of token to analyst may be delayed
        return TOKEN_EXPIRY_TIME == 0 ? false : (System.currentTimeMillis() - tokenGenerationTime) >= TOKEN_EXPIRY_TIME;
    }
}