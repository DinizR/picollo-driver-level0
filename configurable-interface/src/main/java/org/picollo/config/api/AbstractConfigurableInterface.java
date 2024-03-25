/*
* AbstractConfigurableInterface.java
 */
package org.picollo.config.api;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.picollo.context.CobraContext;
import org.picollo.driver.DriverState;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

/**
 * Abstract class with high level implementation for ConfigurableInterface.
 * @author rod
 * @since 2018-11
 */
public abstract class AbstractConfigurableInterface implements ConfigurableInterface {
    private static final String PROPERTIES = ".properties";
    private static final String JSON = ".json";
    private static final String LOG_LEVEL = "log.level";
    private static final String LOG_PATTERN = "log.pattern";
    private static final String extConfigDir = System.getProperty("ext.properties.dir") == null ? "./config" : System.getProperty("ext.properties.dir");
    private static final String logConfigDir = System.getProperty("APPLICATION_LOG_PATH") == null ? "./logs" : System.getProperty("APPLICATION_LOG_PATH");
    private static final String LOG = ".log";
    private static final String LOG_ROOT_ADDITIVE = "log.root.additive";
    private Logger connectorLogger;
    private FileTime fileDate;
    private ConfigType configType;
    private Path configPath;
    private Map<String,String> config = new HashMap<>();
    private static org.slf4j.Logger logger = LoggerFactory.getLogger(AbstractConfigurableInterface.class);
    private DriverState driverState = DriverState.STOPPED;

    public AbstractConfigurableInterface() throws ConfigurationException {
        try {
            configPath = scanConfigFile();
            loadConfig();
            CobraContext.registerDriver(getName(),this);
        } catch (IOException e) {
            logger.error("Configuration file for {} module has not been found.",getName(),e);
            throw new ConfigurationException("Configuration file for module has not been found.");
        }
    }

    private Path scanConfigFile() throws IOException {
        final Path configPath = Paths.get(extConfigDir.substring(8));
        final Optional<Path> propertiesPath = Files.walk(configPath).filter(path -> path.endsWith(getName()+PROPERTIES)).findFirst();
        final Optional<Path> jsonPath = Files.walk(configPath).filter(path -> path.endsWith(getName()+JSON)).findFirst();
        final Path ret;

        if( propertiesPath.isPresent() ) {
            configType = ConfigType.PROPERTIES_FILE;
            ret = propertiesPath.get();
        } else if ( jsonPath.isPresent() ) {
            configType = ConfigType.JSON_MAP_FILE;
            ret = jsonPath.get();
        } else {
            throw new IOException("No configuration file (.properties || .json) for "+getName()+" component has been found.");
        }

        return ret;
    }

    public Logger getComponentLogger() {
        return connectorLogger;
    }

    @Override
    public void loadConfig() throws ConfigurationException {
        final ObjectMapper mapper = new ObjectMapper();
        final FileTime fileTime;

        try {
            fileTime = Files.getLastModifiedTime(configPath);

            if( fileDate == null ) {
                fileDate = fileTime;
            } else if( fileDate.toInstant().equals(fileTime.toInstant()) ){
                return;
            } else {
                logger.info("Started changing configuration in file {}...",configPath);
                fileDate = fileTime;
            }
            switch (configType) {
                case JSON_MAP_FILE:
                    config = mapper.readValue(configPath.toString(), HashMap.class);
                    break;
                case PROPERTIES_FILE:
                    Properties props = new Properties();
                    FileInputStream fis = new FileInputStream(configPath.toFile());

                    props.load(fis);
                    fis.close();
                    props.forEach( (k,v) -> config.put((String)k,(String)v) );
                    break;
            }
            if( this.connectorLogger != null ) {
                this.connectorLogger.detachAppender(getName());
            }
            this.connectorLogger = configureLogger();
            postCoreConfig();
            logger.info("Finished configuring system from file {}.",configPath);
        } catch (MalformedURLException e) {
            logger.error("Configurable interface reading properties error. Parsing : {}",configPath,e);
            throw new ConfigurationException(e.getMessage());
        } catch (FileNotFoundException e) {
            logger.error("Configurable interface reading properties error. File not found: {}",configPath,e);
            throw new ConfigurationException(e.getMessage());
        } catch (IOException e) {
            logger.error("Configurable interface reading properties error. I/O error.",e);
            throw new ConfigurationException(e.getMessage());
        }
    }

    private Logger configureLogger() throws IOException {
        final Path logPath = Paths.get(logConfigDir + File.separator + getName());
        final Boolean logRootAdditive = Boolean.valueOf(getConfigValue(LOG_ROOT_ADDITIVE));
        final Logger log;
        final LoggerContext logCtx = (LoggerContext) LoggerFactory.getILoggerFactory();
        final PatternLayoutEncoder logEncoder = new PatternLayoutEncoder();

        if( ! Files.exists(logPath) ) {
            Files.createDirectory(logPath);
        }

        logEncoder.setContext(logCtx);
        logEncoder.setPattern(getConfigValue(LOG_PATTERN));
        logEncoder.start();

        final RollingFileAppender logFileAppender = new RollingFileAppender();
        logFileAppender.setContext(logCtx);
        logFileAppender.setName("logFile");
        logFileAppender.setEncoder(logEncoder);
        logFileAppender.setAppend(true);
        logFileAppender.setFile(logPath.toString()+File.separator+getName()+LOG);

        final TimeBasedRollingPolicy logFilePolicy = new TimeBasedRollingPolicy();
        logFilePolicy.setContext(logCtx);
        logFilePolicy.setParent(logFileAppender);
        logFilePolicy.setFileNamePattern(logPath.toString()+File.separator+getName()+"-%d{yyyy-MM-dd_HH}"+LOG);
        logFilePolicy.setMaxHistory(7);
        logFilePolicy.start();

        logFileAppender.setRollingPolicy(logFilePolicy);
        logFileAppender.start();

        log = logCtx.getLogger(getName());
        log.setAdditive(logRootAdditive);
        log.setLevel(Level.valueOf(getConfigValue(LOG_LEVEL)));
        log.addAppender(logFileAppender);

        return log;
    }

    public String getConfigValue(String key) {
        return config.get(key);
    }

    protected Map<String,String> getConfigMap() {
        return config;
    }

    // Interceptor for the configuration phase, should be overridden when needed by child classes.
    protected void postCoreConfig(){}


    public DriverState getState() {
        return driverState;
    }

    public void setState(DriverState driverState) {
        this.driverState = driverState;
    }
}