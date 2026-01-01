package cc.starapp.bootapp.core.log;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.classic.spi.Configurator;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.spi.ContextAwareBase;
import org.slf4j.helpers.Util;

import java.net.URL;

public class BootappLogbackConfigurator extends ContextAwareBase implements Configurator {


    private static final String DEFAULT_LOGBACK_FILE = "logback-bootapp.xml";

    public ExecutionStatus configure(LoggerContext loggerContext) {
        this.addInfo("Setting up default configuration.");
        loggerContext.reset();
        JoranConfigurator configurator = new JoranConfigurator();
        try {
            URL url = Configurator.class.getClassLoader().getResource(DEFAULT_LOGBACK_FILE);
            configurator.setContext(loggerContext);
            configurator.doConfigure(url);
            return ExecutionStatus.DO_NOT_INVOKE_NEXT_IF_ANY;
        } catch (JoranException e) {
            Util.report("Failed to instantiate [" + LoggerContext.class.getName() + "]", e);
        }
        return ExecutionStatus.INVOKE_NEXT_IF_ANY;
    }
}
