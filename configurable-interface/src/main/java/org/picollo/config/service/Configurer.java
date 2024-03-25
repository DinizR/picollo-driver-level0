/*
* Configurer.java
*/

package org.picollo.config.service;

import org.picollo.config.api.ConfigurableInterface;
import org.picollo.config.api.ConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author rod
 * @since 2018-08-08
 */
@Component
public class Configurer {
    private static final Logger log = LoggerFactory.getLogger(Configurer.class);

    @Scheduled(initialDelay = 30000, fixedDelay = 30000)
    public void config() {
        List<ConfigurableInterface> workers = OSGiConfig.<List<ConfigurableInterface>,ConfigurableInterface>getDrivers(ConfigurableInterface.class);

        workers
        .parallelStream()
        .filter( ConfigurableInterface::isConfigEnabled )
        .forEach( c -> {
            try {
                c.loadConfig();
            } catch (ConfigurationException e) {
                log.error("Error Running Configuration Loader.",e);
            }
        } );
    }
}
