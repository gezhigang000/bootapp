package cc.starapp.bootapp.support.feign;

import java.util.Arrays;
import java.util.Objects;


public class FeignClientSpecification implements NamedContextFactory.Specification {

    private String name;

    private String className;

    private Class<?>[] configuration;

    public FeignClientSpecification() {
    }

    public FeignClientSpecification(String name, String className, Class<?>[] configuration) {
        this.name = name;
        this.className = className;
        this.configuration = configuration;
    }

    public FeignClientSpecification(String name, Class<?>[] configuration) {
        this.name = name;
        this.configuration = configuration;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Class<?>[] getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Class<?>[] configuration) {
        this.configuration = configuration;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FeignClientSpecification that)) {
            return false;
        }
        return Objects.equals(name, that.name) && Objects.equals(className, that.className)
                && Arrays.equals(configuration, that.configuration);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(name, className);
        result = 31 * result + Arrays.hashCode(configuration);
        return result;
    }

    @Override
    public String toString() {
        return "FeignClientSpecification{" + "name='" + name + "', " + "className='" + className + "', "
                + "configuration=" + Arrays.toString(configuration) + "}";

    }

}
