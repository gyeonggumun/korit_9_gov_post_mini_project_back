package com.korit.post_mini_project_back.service;

import com.korit.post_mini_project_back.entity.User;
import com.korit.post_mini_project_back.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;

    public User createUser() {

    }

    // 닉네임이 기존에 있는 지 검사하여 랜덤 닉네임을 생성해주는 로직
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
