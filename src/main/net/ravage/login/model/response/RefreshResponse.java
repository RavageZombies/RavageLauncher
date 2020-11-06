package net.ravage.login.model.response;

import net.ravage.login.model.AuthProfile;

public class RefreshResponse
{
    private String accessToken;
    private String clientToken;
    private AuthProfile selectedProfile;
    
    public RefreshResponse(final String accessToken, final String clientToken, final AuthProfile selectedProfile) {
        this.accessToken = accessToken;
        this.clientToken = clientToken;
        this.selectedProfile = selectedProfile;
    }
    
    public String getAccessToken() {
        return this.accessToken;
    }
    
    public String getClientToken() {
        return this.clientToken;
    }
    
    public AuthProfile getSelectedProfile() {
        return this.selectedProfile;
    }
}
