package bednarz.glazer.sakowicz.backendapp1.text.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TextDto {
    private Long id;
    private String author;
    private String content;
}
