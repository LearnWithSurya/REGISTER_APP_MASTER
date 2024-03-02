package in.surya.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import in.surya.entity.UserEntity;
import in.surya.repo.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService{

	  @Autowired
	    private UserRepository userRepository;

	    @Override
	    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
	        UserEntity user = userRepository.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException("User with given email not found !!"));
	        return user;
	    }

	

}
