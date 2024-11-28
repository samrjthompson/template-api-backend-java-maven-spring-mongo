package org.example.security;

import java.util.Optional;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class InMongoUserDetailsService implements UserDetailsService {

    private final MongoTemplate mongoTemplate;

    public InMongoUserDetailsService(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public User loadUserByUsername(final String username) throws UsernameNotFoundException {
        return Optional.ofNullable(mongoTemplate.findById(username, User.class))
                .orElseThrow(() -> new UsernameNotFoundException(username));
    }
}
