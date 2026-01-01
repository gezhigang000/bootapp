package cc.starapp.bootapp.support.feign.autoconfigure;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import cc.starapp.bootapp.support.feign.DefaultFeignLoggerFactory;
import cc.starapp.bootapp.support.feign.FeignFormatterRegistrar;
import cc.starapp.bootapp.support.feign.FeignLoggerFactory;
import cc.starapp.bootapp.support.feign.message.*;
import feign.*;
import feign.codec.Decoder;
import feign.codec.Encoder;
import feign.okhttp.OkHttpClient;
import feign.optionals.OptionalDecoder;
import okhttp3.ConnectionPool;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.core.convert.ConversionService;
import org.springframework.format.support.DefaultFormattingConversionService;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.http.MediaType;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;


@Configuration
public class FeignClientsConfiguration  {

    @Autowired
    private ObjectFactory<HttpMessageConverters> messageConverters;

    @Autowired(required = false)
    private List<AnnotatedParameterProcessor> parameterProcessors = new ArrayList<>();

    @Autowired(required = false)
    private List<FeignFormatterRegistrar> feignFormatterRegistrars = new ArrayList<>();

    @Autowired(required = false)
    private Logger logger;

    @Bean
    @ConditionalOnMissingBean
    public Decoder feignDecoder() {
        return new OptionalDecoder(new ResponseEntityDecoder(new SpringDecoder(this.messageConverters.getObject().getConverters())));
    }

    @Bean
    @ConditionalOnMissingBean
    public Encoder feignEncoder() {
        return new SpringEncoder(this.messageConverters.getObject().getConverters());
    }

    @Bean
    @ConditionalOnMissingBean
    public Contract feignContract(ConversionService feignConversionService) {
        return new SpringMvcContract(this.parameterProcessors, feignConversionService);
    }

    @Bean
    public FormattingConversionService feignConversionService() {
        FormattingConversionService conversionService = new DefaultFormattingConversionService();
        for (FeignFormatterRegistrar feignFormatterRegistrar : feignFormatterRegistrars) {
            feignFormatterRegistrar.registerFormatters(conversionService);
        }
        return conversionService;
    }

    @Bean
    @ConditionalOnMissingBean
    public Retryer feignRetryer() {
        return Retryer.NEVER_RETRY;
    }

    @Bean
    @Scope("prototype")
    @ConditionalOnMissingBean
    public Feign.Builder feignBuilder(Retryer retryer) {
        return Feign.builder().retryer(retryer);
    }

    @Bean
    @ConditionalOnMissingBean(FeignLoggerFactory.class)
    public FeignLoggerFactory feignLoggerFactory() {
        return new DefaultFeignLoggerFactory(logger);
    }

    @Bean
    @ConditionalOnMissingBean
    public Client client() {
        ConnectionPool connectionPool = new ConnectionPool(500, 10, TimeUnit.SECONDS);
        okhttp3.OkHttpClient.Builder builder = new okhttp3.OkHttpClient.Builder()
                .connectionPool(connectionPool)
                .connectTimeout(60, TimeUnit.SECONDS);
        okhttp3.OkHttpClient okHttpClient = builder.build();
        return new OkHttpClient(okHttpClient);
    }


}
