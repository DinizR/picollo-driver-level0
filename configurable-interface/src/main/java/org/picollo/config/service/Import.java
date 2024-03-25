/*
* Import.java
 */
package org.picollo.config.service;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.List;

/**
 * Model class to support configuration of package imports.
 * @author rod
 * @since 2018-10-02
 */
@Configuration
@PropertySource("${ext.properties.dir:classpath:}/import.properties")
@ConfigurationProperties("import")
class Import {
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
        return "Import{" +
                "version='" + version + '\'' +
                ", packages=" + packages +
                '}';
    }
}