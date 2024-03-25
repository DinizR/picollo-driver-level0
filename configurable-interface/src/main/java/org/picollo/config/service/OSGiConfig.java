/*
 * OSGiConfig.java
 */
package org.picollo.config.service;

import org.picollo.driver.DriverInterface;
import org.osgi.framework.*;
import org.osgi.framework.launch.Framework;
import org.osgi.framework.launch.FrameworkFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * OSGi framework configuration class.
 * @author rod
 * @since 2018-06-06
 * Included new category of drivers called Services
 * @author rod
 * @since 2021-10-06
 */
@Component
public class OSGiConfig {
    private static final Logger log = LoggerFactory.getLogger(OSGiConfig.class);
    @Value("${application.core-modules}")
    private String coreModules;
    @Value("${application.custom-modules}")
    private String customModules;
    @Value("${application.service-modules}")
    private String serviceModules;
    @Autowired
    private Export export;
    public static Framework osgi;
    public static final String APPLICATION_CORE_MODULES = "APPLICATION_CORE-MODULES";
    public static final String APPLICATION_CUSTOM_MODULES = "APPLICATION_CUSTOM-MODULES";
    public static final String APPLICATION_SERVICE_MODULES = "APPLICATION_SERVICE-MODULES";

    @PostConstruct
    public void init() {
        final Map configMap = new HashMap();
        final Iterator<FrameworkFactory> iterator = ServiceLoader.load(FrameworkFactory.class).iterator();
        final FrameworkFactory frameworkFactory = ServiceLoader.load(FrameworkFactory.class).iterator().next();

        log.info("Starting OSGi framework, using export config={}... ",export);
        if (System.getProperty(APPLICATION_CORE_MODULES) != null) {
            coreModules = System.getProperty(APPLICATION_CORE_MODULES);
        }
        if (System.getProperty(APPLICATION_SERVICE_MODULES) != null) {
            serviceModules = System.getProperty(APPLICATION_SERVICE_MODULES);
        }
        if (System.getProperty(APPLICATION_CUSTOM_MODULES) != null) {
            customModules = System.getProperty(APPLICATION_CUSTOM_MODULES);
        }

        if (!iterator.hasNext()) {
            log.error("Error starting OSGi framework message = Unable to locate OSGi framework factory.");
            throw new IllegalStateException("Unable to locate OSGi framework factory.");
        }
        configMap.put(Constants.FRAMEWORK_STORAGE_CLEAN, Constants.FRAMEWORK_STORAGE_CLEAN_ONFIRSTINIT);
        configMap.put(Constants.FRAMEWORK_SYSTEMPACKAGES_EXTRA, String.join(",",export.getPackages())+";version=\""+export.getVersion()+"\"");
//        configMap.put(Constants.IMPORT_PACKAGE,String.join(",",anImport.getPackages())+";version=\""+anImport.getVersion()+"\"");
        osgi = frameworkFactory.newFramework(configMap);

        try {
            osgi.start();
            Files.list(Paths.get(coreModules))
                .filter(path -> !path.getFileName().toString().equalsIgnoreCase("README.md"))
                .forEach( i -> {
                    try {
                        Bundle b = osgi.getBundleContext().installBundle(i.toString());
                        b.start();
                    } catch (BundleException e) {
                        log.error("Error loading core bundle: {}, message: {}",i,e.getMessage(),e);
                    }
                });
            Files.list(Paths.get(serviceModules))
                .filter(path -> !path.getFileName().toString().equalsIgnoreCase("README.md"))
                .forEach( i -> {
                   try {
                       Bundle b = osgi.getBundleContext().installBundle(i.toString());
                       b.start();
                   } catch (BundleException e) {
                       log.error("Error loading service bundle: {}, message: {}",i,e.getMessage(),e);
                   }
               });
            Files.list(Paths.get(customModules))
                .filter(path -> !path.getFileName().toString().equalsIgnoreCase("README.md"))
                .forEach( i -> {
                    try {
                        Bundle b = osgi.getBundleContext().installBundle(i.toString());
                        b.start();
                    } catch (BundleException e) {
                        log.error("Error loading custom bundle: {}, message: {}",i,e.getMessage(),e);
                    }
                });
        } catch (BundleException e) {
            log.error("Error starting OSGi framework message = {}",e.getMessage(),e);
            throw new IllegalStateException("Unable to start OSGi Framework.");
        } catch (IOException e) {
            log.error("I/O Error starting OSGi framework message = {}",e.getMessage(),e);
            throw new IllegalStateException("Unable to start OSGi Framework.");
        }
        log.info("OSGi framework started successfully...");
    }

    public static <T extends Optional,K extends DriverInterface> T getDriver(Class driverClass, String driverName) {
        ServiceReference<K>[] serviceReferences;
        K ret = null;

        try {
            serviceReferences = (ServiceReference <K>[]) osgi.getBundleContext().getServiceReferences(driverClass.getName(), null);
            if( serviceReferences != null ) {
                for( ServiceReference<K> sr : serviceReferences ) {
                    if( osgi.getBundleContext().getService(sr).getName().equals(driverName) ) {
                        ret = osgi.getBundleContext().getService(sr);
                    }
                }
            } else {
                log.error("Driver not found. Details: driverClass={},driverName={}",driverClass.getName(),driverName);
            }
        } catch (InvalidSyntaxException e) {
            log.error("Problems capturing the driver. Details: driverClass={},driverName={},errorMessage={}",driverClass.getName(),driverName,e.getMessage(),e);
        }

        return (T) Optional.ofNullable(ret);
    }

    public static <T extends List,K> T getDrivers(Class clazz) {
        final List<K> ret = new ArrayList<>();
        final ServiceReference<K>[] serviceReferences;

        try {
            serviceReferences = (ServiceReference <K>[]) osgi.getBundleContext().getServiceReferences(clazz.getName(), null);
            if( serviceReferences != null ) {
                for( ServiceReference<K> sr : serviceReferences ) {
                    ret.add(osgi.getBundleContext().getService(sr));
                }
            }
        } catch (InvalidSyntaxException e) {
        }

        return (T) ret;
    }
}