package backend.backend.controller;

import backend.backend.dao.TagDAO;
import backend.backend.dto.TagDto;
import backend.backend.entity.Tag;
import backend.backend.dao.TagDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tags")
@CrossOrigin(origins = "http://localhost:5173")
public class TagController {

    @Autowired
    private TagDAO tagRepository;

    @GetMapping
    public ResponseEntity<List<TagDto>> getAllTags() {
        // 1. Lấy danh sách Entity từ database
        List<Tag> tags = tagRepository.findAll();
        List<TagDto> tagDtos = tags.stream()
                .map(tag -> new TagDto(tag.getId(), tag.getName()))
                .collect(Collectors.toList());

      
        return ResponseEntity.ok(tagDtos);
    }
}