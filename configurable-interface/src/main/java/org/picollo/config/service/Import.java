/*
* Import.java
 */
package org.picollo.config.service;

import lombok.Data;
import lombok.ToString;
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
@Data
@ToString
class Import {
    private String version;
    private List<String> packages;
}