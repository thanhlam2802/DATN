package backend.backend.dto;

import backend.backend.entity.Review; 
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.format.DateTimeFormatter;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonFormat;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewDto {
    private Integer id;
    private String author;
    private Integer rating;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm")
    private String date;
    private String content;
    private List<String> images;

    public static ReviewDto fromEntity(Review review) {

        ReviewDto dto = new ReviewDto();
        dto.setId(review.getId());
        String authorName = null;
        if (review.getUser() != null) {
            authorName = review.getUser().getName();
            if (authorName == null || authorName.isBlank()) {
                authorName = review.getUser().getEmail();
            }
        }
        if (authorName == null || authorName.isBlank()) {
            authorName = "Anonymous";
        }
        dto.setAuthor(authorName);
        dto.setRating(review.getRating() != null ? review.getRating().intValue() : null);
        dto.setDate(review.getCreatedAt() != null ? review.getCreatedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : null);
        dto.setContent(review.getContent());
        dto.setImages(List.of()); 

        return dto;

    }
}
