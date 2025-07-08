package backend.backend.dto;

import backend.backend.entity.Review; 
import lombok.Data;

import java.time.format.DateTimeFormatter;
import java.util.List;


@Data 
public class ReviewDto {
    private Integer id;
    private String author;
    private Integer rating;
    private String date;
    private String content;
    private List<String> images;

    public static ReviewDto fromEntity(Review review) {
        ReviewDto dto = new ReviewDto();
        dto.setId(review.getId());
        dto.setAuthor(review.getUser() != null ? review.getUser().getEmail(): "Anonymous");
        dto.setRating(review.getRating() != null ? review.getRating().intValue() : null);
        dto.setDate(review.getCreatedAt() != null ? review.getCreatedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : null);
        dto.setContent(review.getContent());
        dto.setImages(List.of()); 

        return dto;
    }
}