package com.korit.post_mini_project_back.controller;

import com.korit.post_mini_project_back.dto.request.CreatePostReqDto;
import com.korit.post_mini_project_back.dto.request.GetFeedListReqDto;
import com.korit.post_mini_project_back.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    // 기본으로는 데이터를 Json으로 받기 때문에 FormData로 받도록 type 세팅
    // value는 위에서 주소를 설정해주었기 때문에 따로 설정해주지 않아도 괜찮다
    // formData형식으로 데이터를 받을 때는 @ModelAttribute 언어테이션을 달아줌 (dto사용한정)
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createPost(@ModelAttribute CreatePostReqDto dto) {
        System.out.println(dto);
        postService.createPost(dto);
        return ResponseEntity.ok(null);
    }

    @GetMapping("/feeds")
    public ResponseEntity<?> getFeedList(GetFeedListReqDto dto) {
        return ResponseEntity.ok(postService.getFeeds(dto));
    }
}
