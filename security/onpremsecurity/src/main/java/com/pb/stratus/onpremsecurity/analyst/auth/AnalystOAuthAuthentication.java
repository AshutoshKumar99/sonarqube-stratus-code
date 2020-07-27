package com.pb.stratus.onpremsecurity.analyst.auth;

/**
 * @author ekiras
 */
public class AnalystOAuthAuthentication {

    private String access_token;
    private String tokenType;
    private String issuedAt;
    private String expiresIn;
    private String clientID;
    private String org;

    public AnalystOAuthAuthentication() {

    }

    public String toString() {
        return "AnalystOAuthAuthentication{access_token=\'" + this.access_token + '\'' + ", tokenType=\'" + this.tokenType + '\'' + ", issuedAt=\'" + this.issuedAt + '\'' + ", expiresIn=\'" + this.expiresIn + '\'' + ", clientID=\'" + this.clientID + '\'' + ", org=\'" + this.org + '\'' + '}';
    }

    public String getAccess_token() {
        return this.access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public String getTokenType() {
        return this.tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public String getIssuedAt() {
        return this.issuedAt;
    }

    public void setIssuedAt(String issuedAt) {
        this.issuedAt = issuedAt;
    }

    public String getExpiresIn() {
        return this.expiresIn;
    }

    public void setExpiresIn(String expiresIn) {
        this.expiresIn = expiresIn;
    }

    public String getClientID() {
        return this.clientID;
    }

    public void setClientID(String clientID) {
        this.clientID = clientID;
    }

    public String getOrg() {
        return this.org;
    }

    public void setOrg(String org) {
            this.org = org;
        }
}
