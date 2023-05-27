package bednarz.glazer.sakowicz.backendapp1.text;

import bednarz.glazer.sakowicz.backendapp1.text.dto.TextDto;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "text")
public class Text {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "reviewed", nullable = false)
    private boolean reviewed;

    @Column(name = "author_id", nullable = false)
    private Long authorId;

    @Lob
    @Column(nullable = false)
    private String content;

    public TextDto toTextDto(String author) {
        return TextDto.builder()
                .id(id)
                .author(author)
                .content(content)
                .build();
    }
}
