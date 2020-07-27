/**
 * Apache CXF interceptor to add security header to outgoing SOAP message.
 * User: ar009sh
 * Date: 3/13/14
 * Time: 5:23 PM
 */
package com.pb.stratus.onpremsecurity.interceptors;

import org.apache.cxf.message.Message;
import org.apache.log4j.Logger;


public class AdminAuthenticationOutInterceptor extends AbstractAuthenticationOutInterceptor {
    private static final Logger logger = Logger.getLogger(AdminAuthenticationOutInterceptor.class);

    public AdminAuthenticationOutInterceptor() {
    }

    public AdminAuthenticationOutInterceptor(String phase) {
        super(phase);
    }

    @Override
    public void populateAuthenticationInfo(Message message) {
        addBasicAuthenticationHeader(message);
    }
}