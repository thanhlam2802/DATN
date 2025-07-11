package backend.backend.dto;

import backend.backend.entity.Review; 
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewDto {
    private Integer id;
    private String author;
    private Integer rating;
    private String date;
    private String content;
    private List<String> images;

    public static ReviewDto fromEntity(Review review) {
        return ReviewDto.builder()
                .id(review.getId())
                .author(review.getUser() != null ? review.getUser().getEmail(): "Anonymous")
                .rating(review.getRating() != null ? review.getRating().intValue() : null)
                .date(review.getCreatedAt() != null ? review.getCreatedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : null)
                .content(review.getContent())
                .images(List.of())
                .build();
    }
}