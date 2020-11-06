package net.ravage.update.minecraft.versions;

import java.util.Collections;
import java.util.ArrayList;
import java.util.List;

public class ExtractRules
{
    private List<String> exclude;
    
    public ExtractRules() {
        this.exclude = new ArrayList<String>();
    }
    
    public ExtractRules(String... exclude) {
        this.exclude = new ArrayList<String>();
        if (exclude != null) {
            Collections.<String>addAll(this.exclude, exclude);
        }
    }
    
    public ExtractRules(ExtractRules rules) {
        this.exclude = new ArrayList<String>();
        for (String exclude : rules.exclude) {
            this.exclude.add(exclude);
        }
    }
    
    public List<String> getExcludes() {
        return this.exclude;
    }
    
    public boolean shouldExtract(String path) {
        if (this.exclude != null) {
            for (String rule : this.exclude) {
                if (path.startsWith(rule)) {
                    return false;
                }
            }
        }
        return true;
    }
}
