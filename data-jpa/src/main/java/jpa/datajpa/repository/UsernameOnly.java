package jpa.datajpa.repository;


import org.springframework.beans.factory.annotation.Value;

public interface UsernameOnly {
    @Value("#{target.username + ' ' + target.age}") //open projection, 없으면 close projection
    String getUsername();
}
