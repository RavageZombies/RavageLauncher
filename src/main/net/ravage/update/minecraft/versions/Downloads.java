package net.ravage.update.minecraft.versions;

import com.google.gson.annotations.SerializedName;

public class Downloads
{
    private Download client;
    private Download server;
    public Download artifact;
    
    public Download getClient() {
        return this.client;
    }
    
    public void setClient(Download client) {
        this.client = client;
    }
    
    public Download getServer() {
        return this.server;
    }
    
    public void setServer(Download server) {
        this.server = server;
    }
    
    public void setArtifact(Download artifact) {
        this.artifact = artifact;
    }
    
    public Download getArtifact() {
        return this.artifact;
    }
    
    public class Classifier
    {
        @SerializedName("natives-linux")
        private Download linux;
        @SerializedName("natives-windows")
        private Download windows;
        @SerializedName("natives-macos")
        private Download macos;
        
        public Download getLinux() {
            return this.linux;
        }
        
        public void setLinux(Download linux) {
            this.linux = linux;
        }
        
        public Download getWindows() {
            return this.windows;
        }
        
        public void setWindows(Download windows) {
            this.windows = windows;
        }
        
        public Download getMacos() {
            return this.macos;
        }
        
        public void setMacos(Download macos) {
            this.macos = macos;
        }
    }
}
