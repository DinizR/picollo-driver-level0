/*
* CredentialProcessor.java
*/

package org.picollo.config.api;

import org.picollo.config.credential.CredentialFactory;
import org.picollo.config.credential.CredentialManager;
import org.picollo.config.credential.Credentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Properties;
import java.util.regex.Pattern;

/**
 * This is a properties processor that matches the pattern "{{CredentialManager...}}"
 * on to process specific keys for safe credentials storage.
 *
 * @author rodrigo
 * @since 2024-04
 */
public class CredentialProcessor {
    private static final Logger log = LoggerFactory.getLogger(CredentialProcessor.class);

    public void processProperties(final Properties properties) {
        final HashMap<String,String> credentials = new HashMap<>();
        final Pattern pattern = Pattern.compile("\\{\\{.*?}}");

        properties
            .entrySet()
            .stream()
            .filter(e -> pattern.matcher(e.getKey().toString()).find())
            .forEach(e -> processCredential(properties, e.getKey().toString(), e.getValue().toString()));
    }

    private String removeCurlyBrackets(final String value) {
        final StringBuilder sb = new StringBuilder(value);

        sb.delete(0,2);
        sb.delete(sb.length() - 2, sb.length());

        return sb.toString();
    }

    private void processCredential(final Properties properties, final String key, final String value) {
        final int CREDENTIAL_PROVIDER = 0;
        final int CREDENTIAL_KEY = 1;
        final int CREDENTIAL_VALUE = 2;
        final String CREDENTIAL_MANAGER = "CredentialManager";
        final String[] workingValue = removeCurlyBrackets(value).split("\\.");
        final CredentialManager credentialManager = CredentialFactory.getCredentialManager();

        log.info("Processing properties key {}.", key);

        if (workingValue[CREDENTIAL_PROVIDER].equals(CREDENTIAL_MANAGER)) {
            final Credentials credentials = credentialManager.getCredential(workingValue[CREDENTIAL_KEY]);

            if (workingValue[CREDENTIAL_VALUE].equals("userName"))
                properties.setProperty(key, credentials.getUserName());
            else if (workingValue[CREDENTIAL_VALUE].equals("password"))
                properties.setProperty(key, credentials.getPassword());
            else
                log.error("Credential value {} was is invalid. The valid values are [userName, password].", workingValue[CREDENTIAL_VALUE]);
        } else {
            log.error("Property {} was not with a valid credential processor. The value given was {}.", key, value);
        }
    }
}
