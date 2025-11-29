package com.edurican.flint.core.api.controller.v1;

import com.edurican.flint.core.api.controller.v1.response.TopicResponse;
import com.edurican.flint.core.domain.Topic;
import com.edurican.flint.storage.TopicRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/topics")
public class TopicController {

    private final TopicRepository topicRepository;

    //토픽 드롭다운에서 사용
    @GetMapping
    public List<TopicResponse> getTopics() {
        return topicRepository.findAllByOrderByIdAsc().stream()
                .map(TopicResponse::from)
                .toList();
    }

}