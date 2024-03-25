/*
* Export.java
 */
package org.picollo.config.service;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.List;

/**
 * Model class to support configuration of package exports.
 * @author rod
 * @since 2018-08-15
 */
@Configuration
@PropertySource("${ext.properties.dir:classpath:}/export.properties")
@ConfigurationProperties("export")
class Export {
    private String version;
    private List<String> packages;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public List<String> getPackages() {
        return packages;
    }

    public void setPackages(List<String> packages) {
        this.packages = packages;
    }

    @Override
    public String toString() {
        return "Export{" +
                "version='" + version + '\'' +
                ", packages=" + packages +
                '}';
    }
}