package cc.starapp.bootapp.core.boot.jarfile;

import cc.starapp.bootapp.core.boot.BootContext;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class FileListener extends FileAlterationListenerAdaptor {

    private Logger logger = LoggerFactory.getLogger(FileListener.class);

    public void onFileCreate(File file) {
        logger.info("[新建文件]:" + file.getAbsolutePath());
        fireJarFileChange();
    }

    public void onFileChange(File file) {
        logger.info("[修改文件]:" + file.getAbsolutePath());
        fireJarFileChange();
    }


    public void onFileDelete(File file) {
        logger.info("[删除文件]:" + file.getAbsolutePath());
    }

    public void onDirectoryCreate(File directory) {
        logger.info("[新建目录]:" + directory.getAbsolutePath());
    }

    public void onDirectoryChange(File directory) {
        logger.info("[修改目录]:" + directory.getAbsolutePath());
    }

    private void fireJarFileChange(){
        try {
            boolean b = BootContext.instance().reloadDynamicApp();
            if(b){
                return;
            }
            int retry = 5;
            while(retry-- > 0){
                b = BootContext.instance().reloadDynamicApp();
                if(b){
                    break;
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void onDirectoryDelete(File directory) {
        logger.info("[删除目录]:" + directory.getAbsolutePath());
    }

    public void onStart(FileAlterationObserver observer) {
        super.onStart(observer);
    }

    public void onStop(FileAlterationObserver observer) {
        super.onStop(observer);
    }

}
