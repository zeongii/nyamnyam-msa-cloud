package kr.gateway.config;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class UserDetailsImpl implements UserDetails {

    private final String username;
    private final String password;
    private final Collection<? extends GrantedAuthority> authorities;  // 역할 목록

    public UserDetailsImpl(String username, String password, Collection<? extends GrantedAuthority> authorities) {
        this.username = username;
        this.password = password;
        this.authorities = authorities;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;  // 사용자의 역할을 반환
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;  // 계정이 만료되지 않음
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;  // 계정이 잠기지 않음
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;  // 자격 증명이 만료되지 않음
    }

    @Override
    public boolean isEnabled() {
        return true;  // 계정이 활성화됨
    }
}
