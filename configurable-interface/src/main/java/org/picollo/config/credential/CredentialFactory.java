/*
* CredentialFactory.java
*/

package org.picollo.config.credential;

/**
 * This class is a Factory of Credential Managers allowing different implementations
 * (*)Credential Managers = vaults for secure credential configurations (Win, Mac, Linux)
 *
 * @author rodrigo
 * @since 2024-04
 */
public class CredentialFactory {
    public static CredentialManager getCredentialManager() {
        final String os = System.getProperty("os.name").toLowerCase();

        if (os.equals(CredentialManager.WINDOWS_OS)) {
            return new WindowsCredentialManager();
        }

        throw new IllegalArgumentException(String.format("The OS %s is not valid or not yet implemented.",os));
    }
}
