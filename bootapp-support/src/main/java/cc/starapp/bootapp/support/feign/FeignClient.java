package cc.starapp.bootapp.support.feign;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;


@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface FeignClient {
    @AliasFor("name")
    String value() default "";

    @AliasFor("value")
    String name() default "";

    String url() default "";

    String path() default "";

    boolean primary() default true;

    Class<?>[] configuration() default {};
}