package bednarz.glazer.sakowicz.text;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Text {
    private Long id;
    private String author;
    private String content;
}
