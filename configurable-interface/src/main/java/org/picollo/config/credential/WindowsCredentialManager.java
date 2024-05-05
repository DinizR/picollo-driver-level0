/*
* WindowsCredentialManager.java
*/

package org.picollo.config.credential;

import com.microsoft.credentialstorage.SecretStore;
import com.microsoft.credentialstorage.StorageProvider;
import com.microsoft.credentialstorage.model.StoredCredential;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is the Windows OS implementation of Credential Managers
 * (*)Credential Managers = vaults for secure credential configurations (Win, Mac, Linux)
 *
 * @author rodrigo
 * @since 2024-04
 */
class WindowsCredentialManager implements CredentialManager {
    private static final Logger log = LoggerFactory.getLogger(WindowsCredentialManager.class);
    private final SecretStore<StoredCredential> credentialStorage;

    public WindowsCredentialManager() {
        this.credentialStorage = StorageProvider.getCredentialStorage(true, StorageProvider.SecureOption.REQUIRED);
    }

    @Override
    public Credentials getCredential(final String credentialKey) {
        final StoredCredential credential = credentialStorage.get(credentialKey);

        log.debug("Getting credentials for key: {}.",credentialKey);
        return Credentials.builder()
                .userName(credential.getUsername())
                .password(new String(credential.getPassword()))
                .build();
    }

    @Override
    public void setCredential(final String credentialKey, final Credentials credentials) {
        final StoredCredential credential = new StoredCredential(credentials.getUserName(), credentials.getPassword().toCharArray());

        log.debug("Setting credentials for key: {}.",credentialKey);
        credentialStorage.add(credentialKey, credential);
    }

    @Override
    public void removeCredential(String credentialKey) {
        log.debug("Removing credentials for key: {}.",credentialKey);
        credentialStorage.delete(credentialKey);
    }
}
