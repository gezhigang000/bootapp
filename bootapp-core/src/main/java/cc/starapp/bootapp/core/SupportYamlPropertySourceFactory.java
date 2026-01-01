package cc.starapp.bootapp.core;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.boot.env.OriginTrackedMapPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertySourceFactory;
import org.springframework.core.io.support.ResourcePropertySource;
import org.springframework.util.StringUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class SupportYamlPropertySourceFactory implements PropertySourceFactory {

    private Logger log = LoggerFactory.getLogger(SupportYamlPropertySourceFactory.class);

    @Override
    public PropertySource<?> createPropertySource(String name, EncodedResource resource) throws IOException {
        if (!resource.getResource().getFilename().endsWith(".yml")) {
            return (name != null ? new ResourcePropertySource(name, resource) : new ResourcePropertySource(resource));
        }
        YamlPropertiesFactoryBean factory = new YamlPropertiesFactoryBean();
        factory.setResources(resource.getResource());
        Properties props = factory.getObject();

        return new OriginTrackedMapPropertySource(resource.getResource().getFilename(), props);


    }


    private String getPublicKey(String path) {

        try (BufferedReader bf = new BufferedReader(new FileReader(path))) {
            String key = bf.readLine();
            if (!StringUtils.hasText(key)) {
                log.error("path={} key is empty", path);
                throw new RuntimeException(String.format("path=%s key is empty.", path));
            }
            if (log.isDebugEnabled()) {
                log.debug("load path={},file key={}", path, key);
            }
            return key;
        } catch (IOException ex) {
            log.error("path={} key file not exists", path, ex);
            throw new RuntimeException(String.format("path=%s key file not exists", path));
        }
    }
}
