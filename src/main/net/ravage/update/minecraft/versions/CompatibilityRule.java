package net.ravage.update.minecraft.versions;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.ravage.update.minecraft.utils.OperatingSystem;

import java.util.Map;

public class CompatibilityRule
{
    private Action action;
    private OSRestriction os;
    private Map<String, Object> features;
    
    public CompatibilityRule() {
        this.action = Action.ALLOW;
    }
    
    public CompatibilityRule(CompatibilityRule compatibilityRule) {
        this.action = Action.ALLOW;
        this.action = compatibilityRule.action;
        if (compatibilityRule.os != null) {
            this.os = new OSRestriction(compatibilityRule.os);
        }
        if (compatibilityRule.features != null) {
            this.features = compatibilityRule.features;
        }
    }
    
    public Action getAppliedAction(FeatureMatcher featureMatcher) {
        if (this.os != null && !this.os.isCurrentOperatingSystem()) {
            return null;
        }
        if (this.features != null) {
            if (featureMatcher == null) {
                return null;
            }
            for (Map.Entry<String, Object> feature : this.features.entrySet()) {
                if (!featureMatcher.hasFeature(feature.getKey(), feature.getValue())) {
                    return null;
                }
            }
        }
        return this.action;
    }
    
    public Action getAction() {
        return this.action;
    }
    
    public OSRestriction getOs() {
        return this.os;
    }
    
    public Map<String, Object> getFeatures() {
        return this.features;
    }
    
    public enum Action
    {
        ALLOW("ALLOW", 0), 
        DISALLOW("DISALLOW", 1);
        
        private Action(String s, int n) {
        }
    }
    
    public class OSRestriction
    {
        private OperatingSystem name;
        private String version;
        private String arch;
        
        public OSRestriction() {
        }
        
        public OperatingSystem getName() {
            return this.name;
        }
        
        public String getVersion() {
            return this.version;
        }
        
        public String getArch() {
            return this.arch;
        }
        
        public OSRestriction(OSRestriction osRestriction) {
            this.name = osRestriction.name;
            this.version = osRestriction.version;
            this.arch = osRestriction.arch;
        }
        
        public boolean isCurrentOperatingSystem() {
            if (this.name != null && this.name != OperatingSystem.getCurrentPlatform()) {
                return false;
            }
            if (this.version != null) {
                try {
                    Pattern pattern = Pattern.compile(this.version);
                    Matcher matcher = pattern.matcher(System.getProperty("os.version"));
                    if (!matcher.matches()) {
                        return false;
                    }
                }
                catch (Throwable t) {}
            }
            if (this.arch != null) {
                try {
                    Pattern pattern = Pattern.compile(this.arch);
                    Matcher matcher = pattern.matcher(System.getProperty("os.arch"));
                    if (!matcher.matches()) {
                        return false;
                    }
                }
                catch (Throwable t2) {}
            }
            return true;
        }
    }
    
    public interface FeatureMatcher
    {
        boolean hasFeature(String p0, Object p1);
    }
}
