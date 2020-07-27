package com.pb.stratus.onpremsecurity.interceptors;

import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.apache.ws.security.util.Base64;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by gu003du on 10-Sep-15.
 */
public class BasicAuthenticationOutInterceptor extends AbstractPhaseInterceptor<Message> {

    private static final String BASIC_AUTHENTICATION_HEADER = "Authorization";

    private String userName;
    private String password;

    /**
     * Instantiates the interceptor to live in a specified phase.
     */
    public BasicAuthenticationOutInterceptor() {
        this(Phase.PRE_PROTOCOL);
    }

    /**
     * Instantiates the interceptor to live in a specified phase. The
     * interceptor's id will be set to the name of the implementing class.
     *
     * @param phase the interceptor's phase
     */
    public BasicAuthenticationOutInterceptor(String phase) {
        super(phase);
    }

    /**
     * Intercepts a message.
     * Interceptors should NOT invoke handleMessage or handleFault
     * on the next interceptor - the interceptor chain will
     * take care of this.
     *
     * @param message
     */
    @Override
    public void handleMessage(Message message) throws Fault {
        String authString = getUserName() + ":" + getPassword();
        Map<String, List<?>> headers = (Map<String, List<?>>) message.get(Message.PROTOCOL_HEADERS);
        headers.put(BASIC_AUTHENTICATION_HEADER, Collections.singletonList("Basic " + new String(Base64.encode(authString.getBytes()))));
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
