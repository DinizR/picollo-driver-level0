/*
* Credentials.java
*/

package org.picollo.config.credential;

import lombok.Builder;
import lombok.Data;

/**
 * This class is a generic Credentials class used to support Credential Managers.
 * it is a model class to fetch user names and passwords from Credential Managers.
 *
 * @author rodrigo
 * @since 2024-04
 */
@Builder
@Data
public class Credentials {
    private String userName;
    private String password;
}
