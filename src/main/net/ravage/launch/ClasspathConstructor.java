package net.ravage.launch;

import java.io.File;
import java.util.List;
import net.ravage.util.FileList;

public class ClasspathConstructor extends FileList
{
    public ClasspathConstructor() {
    }
    
    public ClasspathConstructor(final List<File> classPath) {
        super(classPath);
    }
    
    public String make() {
        String classPath = "";
        for (int i = 0; i < this.files.size(); ++i) {
            classPath = classPath + this.files.get(i).getAbsolutePath() + ((i + 1 == this.files.size()) ? "" : File.pathSeparator);
        }
        return classPath;
    }
}
