package cc.starapp.bootapp.core;

import cc.starapp.bootapp.core.boot.BootContext;
import jakarta.servlet.DispatcherType;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.FilterMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.DelegatingFilterProxyRegistrationBean;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

import java.util.*;

public class EmbeddedApplicationContext extends AnnotationConfigWebApplicationContext {

    private Logger logger = LoggerFactory.getLogger(EmbeddedApplicationContext.class);

    @Override
    protected void onRefresh() {
        super.onRefresh();
        Map<String, FilterHolder> dynamicFilterHolderMap = new HashMap<>();
        Map<String, FilterMapping> dynamicFilterMappingMap = new HashMap<>();
        findFilterRegistrationBeanAndRegister(dynamicFilterHolderMap,dynamicFilterMappingMap);
        findDelegatingFilterProxyRegistrationBeanAndRegister(dynamicFilterHolderMap,dynamicFilterMappingMap);

        if(dynamicFilterHolderMap.size() > 0) {
            BootContext.instance().registerFilter(dynamicFilterHolderMap, dynamicFilterMappingMap);
        }

        findServletListenerRegistrationBeanAndRegister();

    }

    private void findFilterRegistrationBeanAndRegister(Map<String, FilterHolder> dynamicFilterHolderMap,Map<String, FilterMapping> dynamicFilterMappingMap){
        Map<String, FilterRegistrationBean> beansOfType = getBeansOfType(FilterRegistrationBean.class);
        if(beansOfType == null || beansOfType.size() == 0){
            return;
        }
        Collection<FilterRegistrationBean> values = beansOfType.values();
        List<FilterRegistrationBean> filterRegistrationBeans = new ArrayList<>(values);
        filterRegistrationBeans.sort((o1, o2) -> o1.getOrder() - o2.getOrder());

        for(FilterRegistrationBean filterRegistrationBean:filterRegistrationBeans){
            Collection urlPatterns = filterRegistrationBean.getUrlPatterns();
            if(urlPatterns.isEmpty()){
                logger.warn("register filter error ,bean {}, url pattern is null",filterRegistrationBean);
            }
            if(urlPatterns.size() > 1){
                logger.warn("register filter error ,bean {}, url pattern more than one",filterRegistrationBean);
            }
            Object urlPattern = urlPatterns.iterator().next();

            FilterHolder filterHolder = new FilterHolder();
            filterHolder.setFilter(filterRegistrationBean.getFilter());
            filterHolder.setName(filterRegistrationBean.toString());
            dynamicFilterHolderMap.put(filterRegistrationBean.toString(), filterHolder);

            FilterMapping mapping = new FilterMapping();
            mapping.setFilterName(filterHolder.getName());
            mapping.setPathSpec(String.valueOf(urlPattern));
            mapping.setDispatcherTypes(EnumSet.of(DispatcherType.REQUEST));
            dynamicFilterMappingMap.put(mapping.getFilterName(),mapping);

        }

    }

    private void findDelegatingFilterProxyRegistrationBeanAndRegister(Map<String, FilterHolder> dynamicFilterHolderMap,Map<String, FilterMapping> dynamicFilterMappingMap){
        Map<String, DelegatingFilterProxyRegistrationBean> beansOfType = getBeansOfType(DelegatingFilterProxyRegistrationBean.class);
        if(beansOfType == null || beansOfType.size() == 0){
            return;
        }
        Collection<DelegatingFilterProxyRegistrationBean> values = beansOfType.values();
        List<DelegatingFilterProxyRegistrationBean> delegatingFilterProxyRegistrationBeanList = new ArrayList<>(values);
        delegatingFilterProxyRegistrationBeanList.sort((o1, o2) -> o1.getOrder() - o2.getOrder());
        for(DelegatingFilterProxyRegistrationBean bean:delegatingFilterProxyRegistrationBeanList){
            Collection urlPatterns = bean.getUrlPatterns();
            if(urlPatterns.isEmpty()){
                logger.warn("register DelegatingFilterProxyRegistrationBean filter error ,bean {}, url pattern is null",bean);
            }
            if(urlPatterns.size() > 1){
                logger.warn("register DelegatingFilterProxyRegistrationBean filter error ,bean {}, url pattern more than one",bean);
            }
            Object urlPattern = urlPatterns.iterator().next();
            FilterHolder filterHolder = new FilterHolder();
            filterHolder.setFilter(bean.getFilter());
            filterHolder.setName(bean.toString());
            dynamicFilterHolderMap.put(bean.toString(), filterHolder);

            FilterMapping mapping = new FilterMapping();
            mapping.setFilterName(filterHolder.getName());
            mapping.setPathSpec(String.valueOf(urlPattern));
            mapping.setDispatcherTypes(EnumSet.of(DispatcherType.REQUEST));
            dynamicFilterMappingMap.put(mapping.getFilterName(),mapping);
        }
    }

    private void findServletListenerRegistrationBeanAndRegister(){
        Map<String, ServletListenerRegistrationBean> beansOfType = getBeansOfType(ServletListenerRegistrationBean.class);
        if(beansOfType == null || beansOfType.size() == 0){
            return;
        }
        Collection<ServletListenerRegistrationBean> values = beansOfType.values();
        List<ServletListenerRegistrationBean> servletListenerRegistrationBeanList = new ArrayList<>(values);
        servletListenerRegistrationBeanList.sort((o1, o2) -> o1.getOrder() - o2.getOrder());
        for(ServletListenerRegistrationBean bean:servletListenerRegistrationBeanList){
            bean.getListener();
            BootContext.instance().registerEventListener(bean.getListener());
        }
    }
}
