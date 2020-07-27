package com.pb.stratus.security.core.authentication;

import com.pb.stratus.security.core.jaxb.User;

/**
 * Created with IntelliJ IDEA.
 * User: si001jy
 * Date: 2/19/14
 * Time: 4:53 PM
 * To change this template use File | Settings | File Templates.
 */
public interface IAuthenticator {
    public String INVALID_CREDENTIALS = "Invalid Credentials";
    public String SERVICE_ERROR = "Service Error";

    public User login(String userName, String password) throws AuthenticationFailureException;
}
