package com.renlijia.bootapp.core;

import com.renlijia.keyservice.config.DecryptServerConfig;
import com.renlijia.keyservice.decrypt.DecryptPlaceholder;
import com.renlijia.keyservice.env.DecryptedMapPropertySource;
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

    private static final String PROP_DECRYPY_REQUEST_URL = "decrypt.requestURL";
    private static final String PROP_DECRYPY_REQUEST_URL_OLD = "renlijia.decrypt.endpoint";
    private static final String PROP_DECRYPY_APPID = "decrypt.appId";
    private static final String PROP_DECRYPY_APPID_OLD = "renlijia.decrypt.app-id";
    private static final String PROP_DECRYPY_PUBLIC_KEY = "renlijia.decrypt.public-key";
    private static final String PROP_DECRYPY_PUBLIC_KEY_PATH = "renlijia.decrypt.public-key-path";
    private static final String PROP_DECRYPY_PUBLIC_KEY_PATH_DEFAULT_VALUE = "tmp/.key_pub";
    private static final String PROP_K8s_ENV_PUB_KEY = "KEY_PUB";

    @Override
    public PropertySource<?> createPropertySource(String name, EncodedResource resource) throws IOException {
        if (!resource.getResource().getFilename().endsWith(".yml")) {
            return (name != null ? new ResourcePropertySource(name, resource) : new ResourcePropertySource(resource));
        }
        YamlPropertiesFactoryBean factory = new YamlPropertiesFactoryBean();
        factory.setResources(resource.getResource());
        Properties props = factory.getObject();
        String oldEndpoint = props.getProperty(PROP_DECRYPY_REQUEST_URL, "");
        String endpoint = props.getProperty(PROP_DECRYPY_REQUEST_URL_OLD, oldEndpoint);

        String oldAppId = props.getProperty(PROP_DECRYPY_APPID, "");
        String appId = props.getProperty(PROP_DECRYPY_APPID_OLD, oldAppId);

        String publicKey = props.getProperty(PROP_DECRYPY_PUBLIC_KEY, "");
        String publicKeyPath = props.getProperty(PROP_DECRYPY_PUBLIC_KEY_PATH, PROP_DECRYPY_PUBLIC_KEY_PATH_DEFAULT_VALUE);
        if (publicKey.startsWith("${")) {
            publicKey = System.getenv().get(PROP_K8s_ENV_PUB_KEY);
        }
        boolean hasEncrypt = props.values().stream().anyMatch(s -> String.valueOf(s).startsWith(DecryptPlaceholder.prefix));

        if (hasEncrypt) {
            if (!StringUtils.hasText(endpoint)) {
                throw new RuntimeException("解密服务地址为空");
            }
            if (!StringUtils.hasText(appId)) {
                throw new RuntimeException("解密appid为空");
            }
            if (!StringUtils.hasText(publicKeyPath)) {
                throw new RuntimeException("解密公钥地址不能为空");
            }
            //服务器目录读取key
            if (!StringUtils.hasText(publicKey)) {
                log.info("get public-key from file: {}", publicKeyPath);
                publicKey = getPublicKey(publicKeyPath);
            }
            DecryptServerConfig config = new DecryptServerConfig(endpoint, appId, publicKeyPath, publicKey);
            OriginTrackedMapPropertySource propertiesPropertySource = new OriginTrackedMapPropertySource(resource.getResource().getFilename(), props);
            return new DecryptedMapPropertySource(propertiesPropertySource, config);
        }
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
