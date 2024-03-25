/*
* DriverInterface.java
 */
package org.picollo.driver;

/**
 * Root interface for Drivers exists to facilitate polymorphism.
 * @author rod
 * @since 2018-10-09
 */
public interface DriverInterface {
    DriverType getType();
    String getName();
    String getDescription();
    DriverState getState();
    void setState(DriverState driverState);
}