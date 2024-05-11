/*
* CobraContext.java
 */
package org.picollo.context;

import org.picollo.driver.DriverInterface;
import org.picollo.driver.DriverState;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author rod
 * @since 2019-05
 */
public final class CobraContext {
    public static final Map<String, DriverInterface> installedDrivers = new HashMap<>();

    private CobraContext() {
    }

    public static void registerDriver(String key, DriverInterface driver) {
        installedDrivers.put(key,driver);
    }

    public static Optional<DriverInterface> getDriver(String key) {
        return Optional.ofNullable(installedDrivers.get(key));
    }

    public static boolean isRunning(String key) {
        return Optional.ofNullable(installedDrivers.get(key)).isPresent() ? Optional.ofNullable(installedDrivers.get(key)).get().getState() == DriverState.RUNNING : false;
    }
}
