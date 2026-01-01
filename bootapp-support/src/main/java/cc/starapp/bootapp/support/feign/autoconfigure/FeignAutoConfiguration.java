package cc.starapp.bootapp.support.feign.autoconfigure;

import cc.starapp.bootapp.support.feign.DefaultTargeter;
import cc.starapp.bootapp.support.feign.FeignClientSpecification;
import cc.starapp.bootapp.support.feign.FeignContext;
import cc.starapp.bootapp.support.feign.Targeter;
import feign.Feign;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(Feign.class)
public class FeignAutoConfiguration {

    @Autowired(required = false)
    private List<FeignClientSpecification> configurations = new ArrayList<>();



    @Bean
    public FeignContext feignContext() {
        FeignContext context = new FeignContext(FeignClientsConfiguration.class);
        context.setConfigurations(this.configurations);
        return context;
    }

    @Configuration
    protected static class DefaultFeignTargeterConfiguration {
        @Bean
        @ConditionalOnMissingBean
        public Targeter feignTargeter() {
            return new DefaultTargeter();
        }
    }

}
