/*
* ConfigurableInterface.java
 */
package org.picollo.config.api;

import org.picollo.driver.DriverInterface;

/**
 * Interface exposed to develop configurable drivers.
 * @author rod
 * @since 2018-09
 */
public interface ConfigurableInterface extends DriverInterface {
    // Configuration handlers
    boolean isConfigEnabled();
    void loadConfig() throws ConfigurationException;
    String getConfigValue(String key);
}