/**
 * SessionValidationStrategy
 * User: GU003DU
 * Date: 12/20/13
 * Time: 12:29 PM
 */
package com.pb.stratus.controller.strategy;

import javax.servlet.http.HttpServletRequest;

public interface SessionValidationStrategy {
    boolean invalidateSession(HttpServletRequest request);
}
