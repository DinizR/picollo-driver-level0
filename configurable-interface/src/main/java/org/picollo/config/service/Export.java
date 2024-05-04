/*
* Export.java
 */
package org.picollo.config.service;

import lombok.Data;
import lombok.ToString;
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
@Data
@ToString
class Export {
    private String version;
    private List<String> packages;
}