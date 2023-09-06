package backend.server.service.security.services;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * This interface provides the contract for loading user-specific data. It defines only one method that loads a
 * UserDetails object by the given username. This interface is used by the Spring Security framework to obtain the
 * user information when authenticating a user.
 */
public interface UserDetailsService {
    /**
     * This method loads a UserDetails object containing the user information by the given username.
     *
     * @param username the username of the user to load
     * @return the UserDetails object containing the user information
     * @throws UsernameNotFoundException if the user is not found
     */
    UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;
}