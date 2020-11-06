package net.ravage.login;

import net.ravage.login.model.AuthError;

public class AuthenticationException extends Exception
{
    private AuthError model;
    
    public AuthenticationException(final AuthError model) {
        super(model.getErrorMessage());
        this.model = model;
    }
    
    public AuthError getErrorModel() {
        return this.model;
    }
}
