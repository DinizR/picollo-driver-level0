/*
* CredentialManager.java
*/

package org.picollo.config.credential;

/**
 * This interface provides the abstraction needed for multi OS Credential Managers
 * (*)Credential Managers = vaults for secure credential configurations (Win, Mac, Linux)
 *
 * @author rodrigo
 * @since 2024-04
 */
public interface CredentialManager {
    String WINDOWS_OS = "windows";
    String MAC_OS = "mac";
    String LINUX_OS = "linux";
    String UNIX_OS = "unix";

    Credentials getCredential(String credentialKey);
    void setCredential(String credentialKey, Credentials credentials);
    void removeCredential(String credentialKey);
}
