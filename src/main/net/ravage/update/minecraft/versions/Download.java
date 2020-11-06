package net.ravage.update.minecraft.versions;

public class Download
{
    private String sha1;
    private String url;
    private String size;
    
    public Download(String sha1, String url, String size) {
        this.sha1 = sha1;
        this.url = url;
        this.size = size;
    }
    
    public String getArtifactURL(String name) {
        if (name == null) {
            throw new IllegalStateException("Cannot get artifact dir of empty/blank artifact");
        }
        String[] parts = name.split(":", 4);
        String text = String.format("%s/%s/%s", parts[0].replaceAll("\\.", "/"), parts[1], parts[2]);
        if (parts.length == 4) {
            return String.valueOf(text) + "/" + parts[1] + "-" + parts[2] + "-" + parts[3] + ".jar";
        }
        return String.valueOf(text) + "/" + parts[1] + "-" + parts[2] + ".jar";
    }
    
    public String getSha1() {
        return this.sha1;
    }
    
    public void setSha1(String sha1) {
        this.sha1 = sha1;
    }
    
    public String getUrl() {
        return this.url;
    }
    
    public void setUrl(String url) {
        this.url = url;
    }
    
    public String getSize() {
        return this.size;
    }
    
    public void setSize(String size) {
        this.size = size;
    }
}
