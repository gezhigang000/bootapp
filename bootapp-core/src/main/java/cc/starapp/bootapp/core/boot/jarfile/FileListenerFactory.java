package cc.starapp.bootapp.core.boot.jarfile;

import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;

import java.io.File;

public class FileListenerFactory {

    private static final String  JAR_FILE_SUFFIX = ".jar";

    public static FileAlterationMonitor registerJarMonitor(File directory, FileListener fileListener, long intervalInMiliseconds) {
        IOFileFilter suffixFileFilter = FileFilterUtils.suffixFileFilter(JAR_FILE_SUFFIX);
        FileAlterationObserver observer = new FileAlterationObserver(directory, suffixFileFilter);
        observer.addListener(fileListener);
        FileAlterationMonitor fileAlterationMonitor = new FileAlterationMonitor(intervalInMiliseconds, observer);
        try {
            fileAlterationMonitor.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return fileAlterationMonitor;
    }


}
