/**
 * Decide if session needs to be invalidated or not
 * SessionValidationStrategyImpl
 * User: GU003DU
 * Date: 12/20/13
 * Time: 12:28 PM
 */

package com.pb.stratus.controller.strategy;

import javax.servlet.http.HttpServletRequest;

public class SessionValidationStrategyImpl implements SessionValidationStrategy {

    private String tokenName = "stratusopentoken";

    @Override
    public boolean invalidateSession(HttpServletRequest request) {

        boolean invalidate = false;
        // Check if incoming request has stratus open token. Request will contain open token only in a scenario when
        // a user is authenticated. Invalidate session corresponding to Guest user.
        if (request.getParameter(getTokenName()) != null) {
            invalidate = true;
        }

        return invalidate;
    }

    public String getTokenName() {
        return tokenName;
    }

    public void setTokenName(String tokenName) {
        this.tokenName = tokenName;
    }
}
