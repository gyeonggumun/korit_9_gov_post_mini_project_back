package com.korit.post_mini_project_back.service;

import com.korit.post_mini_project_back.entity.User;
import com.korit.post_mini_project_back.mapper.UserMapper;
import com.korit.post_mini_project_back.security.PrincipalUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;

    public User findUserByOauth2Id(String oauth2Id) {
        return userMapper.findByOauth2Id(oauth2Id);
    }

    public User createUser(Authentication authentication) {
        PrincipalUser principalUser = (PrincipalUser) authentication.getPrincipal();
        User user = principalUser.getUser();
        user.setNickname(createNickname());
//        userMapper.findByOauth2Id();
        userMapper.insert(user);
        return user;
    }

    // 닉네임이 기존에 있는 지 검사하여 랜덤 닉네임을 생성해주는 로
    // 직
    public String createNickname() {
        String newNickname = null;
        while (true) {
            newNickname =  userMapper.createNickname();
            if(userMapper.findByNickName(newNickname) == null) {
                break;
            }
        }
        return newNickname;
    }
}
