package backend.backend.config;

import org.quartz.spi.TriggerFiredBundle;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;

import java.util.Properties;

/**
 * 🕒 Quartz Configuration
 * Cấu hình Quartz Scheduler cho Bus Management System
 */
@Configuration
@EnableScheduling
public class QuartzConfig {

    /**
     * Custom JobFactory để Spring có thể inject dependencies vào Jobs
     */
    public static class AutowiringSpringBeanJobFactory extends SpringBeanJobFactory {

        private transient AutowireCapableBeanFactory beanFactory;

        @Override
        public void setApplicationContext(ApplicationContext context) {
            beanFactory = context.getAutowireCapableBeanFactory();
        }

        @Override
        protected Object createJobInstance(TriggerFiredBundle bundle) throws Exception {
            Object job = super.createJobInstance(bundle);
            beanFactory.autowireBean(job);
            return job;
        }
    }

    /**
     * SpringBeanJobFactory với auto-wiring support
     */
    @Bean
    public SpringBeanJobFactory springBeanJobFactory() {
        return new AutowiringSpringBeanJobFactory();
    }

    /**
     * Scheduler Factory Bean configuration
     */
    @Bean
    public SchedulerFactoryBean schedulerFactoryBean() {
        SchedulerFactoryBean factory = new SchedulerFactoryBean();
        
        // Set JobFactory để support dependency injection
        factory.setJobFactory(springBeanJobFactory());
        
        // Quartz properties
        Properties quartzProps = new Properties();
        
        // Basic scheduler configuration
        quartzProps.setProperty("org.quartz.scheduler.instanceName", "BusScheduler");
        quartzProps.setProperty("org.quartz.scheduler.instanceId", "AUTO");
        quartzProps.setProperty("org.quartz.scheduler.skipUpdateCheck", "true");
        
        // Thread pool configuration
        quartzProps.setProperty("org.quartz.threadPool.class", "org.quartz.simpl.SimpleThreadPool");
        quartzProps.setProperty("org.quartz.threadPool.threadCount", "5");
        quartzProps.setProperty("org.quartz.threadPool.threadsInheritContextClassLoaderOfInitializingThread", "true");
        
        // Job store configuration (memory-based for now)
        quartzProps.setProperty("org.quartz.jobStore.class", "org.quartz.simpl.RAMJobStore");
        
        // Enable JMX monitoring
        quartzProps.setProperty("org.quartz.scheduler.jmx.export", "true");
        
        factory.setQuartzProperties(quartzProps);
        factory.setStartupDelay(10); // Delay 10 seconds to ensure Spring context is ready
        factory.setAutoStartup(true);
        
        return factory;
    }
}