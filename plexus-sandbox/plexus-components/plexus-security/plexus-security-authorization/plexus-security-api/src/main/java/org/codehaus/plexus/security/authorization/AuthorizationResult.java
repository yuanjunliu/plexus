package org.codehaus.plexus.security.authorization;
/*
 * Copyright 2005 The Codehaus.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * AuthorizationResult: wrapper object for results from the authorization system
 *
 * @author: Jesse McConnell <jesse@codehaus.org>
 * @version: $ID:$
 */
public class AuthorizationResult
{
    /**
     * the name of the authorization store that generated this result
     */
    private String authorizationStore;

    /**
     * boolean for authorized or not behavior
     */
    private boolean isAuthorized = false;

    /**
     * the principal actor on the system, most commonly the username, but some systems may use this as a different value
     */
    private Object principal;

    /**
     * optional informational message from the authorization system
     */
    private String message;

    /**
     * optional exception that might be thrown from the authorization system
     */
    private Exception exception;

    /**
     * optional payload object containing implementation specific information
     */
    private Object payload;
    
    private AuthorizationResult() {}

    public AuthorizationResult( String authorizationStore )
    {
        this.authorizationStore = authorizationStore;
    }

    public boolean isAuthorized()
    {
        return isAuthorized;
    }

    public void setAuthorized( boolean authorized )
    {
        isAuthorized = authorized;
    }

    public Object getPrincipal()
    {
        return principal;
    }

    public void setPrincipal( Object principal )
    {
        this.principal = principal;
    }

    public String getMessage()
    {
        return message;
    }

    public void setMessage( String message )
    {
        this.message = message;
    }

    public Exception getException()
    {
        return exception;
    }

    public void setException( Exception exception )
    {
        this.exception = exception;
    }

    public Object getPayload()
    {
        return payload;
    }

    public void setPayload( Object payload )
    {
        this.payload = payload;
    }

    public String getAuthorizationStore()
    {
        return authorizationStore;
    }

    public void setAuthorizationStore( String authorizationStore )
    {
        this.authorizationStore = authorizationStore;
    }
}
