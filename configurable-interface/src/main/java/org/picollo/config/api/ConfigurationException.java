/*
* ConfigurationException.java
 */
package org.picollo.config.api;

/**
 * @author rod
 * @since 2018-08-08
 */
public class ConfigurationException extends Exception {
    public ConfigurationException(String message) {
        super(message);
    }
    public ConfigurationException(String message, Throwable throwable) {
        super(message,throwable);
    }
}
