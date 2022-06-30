package com.preview.preview.domain.service.user;

import com.preview.preview.domain.authority.Authority;
import com.preview.preview.domain.user.User;
import com.preview.preview.domain.user.UserRepository;
import com.preview.preview.domain.web.dto.user.UserDto;
import com.preview.preview.domain.web.dto.user.UserLoginDto;
import com.preview.preview.global.util.SecurityUtil;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public UserDto signup(UserDto userDto){
        if (userRepository.findOneWithAuthoritiesByName(userDto.getUsername()).orElse(null) != null){
            throw new RuntimeException("이미 가입되어 있는 유저입니다.");
        }

        Authority authority = Authority.builder()
                .authorityName("ROLE_USER")
                .build();

        User user = User.builder()
                .name(userDto.getUsername())
                .password(passwordEncoder.encode(userDto.getPassword()))
                .nickname(userDto.getNickname())
                .authorities(Collections.singleton(authority))
                .activated(true)
                .build();

        return UserDto.from(userRepository.save(user));
    }

    @Transactional
    public UserLoginDto save(UserLoginDto userLoginDto){
        if (userRepository.findOneWithAuthoritiesByKakaoId(userLoginDto.getKakaoId()).orElse(null) != null){
            throw new RuntimeException("이미 가입되어 있는 유저입니다.");
        }

        Authority authority = Authority.builder()
                .authorityName("ROLE_USER")
                .build();

        User user = User.builder()
                .password(passwordEncoder.encode("xxxx")) // 추 후 확장, 지금 서비스에선 필요 x
                .nickname(userLoginDto.getNickname())
                .authorities(Collections.singleton(authority))
                .kakaoId(userLoginDto.getKakaoId())
                .activated(true)
                .build();

        return UserLoginDto.from(userRepository.save(user));
    }

    @Transactional(readOnly = true)
    public UserDto getUserWithAuthorities(String username){
        return UserDto.from(userRepository.findOneWithAuthoritiesByName(username).orElse(null));
    }

    @Transactional(readOnly = true)
    public UserDto getMyUserWithAuthorities(){
        return UserDto.from(SecurityUtil.getCurrentUsername().flatMap(userRepository::findOneWithAuthoritiesByName).orElse(null));
    }

    @Override
    public Optional<User> findByIdPw(String id) {
        return userRepository.findById(id);
    }
}
