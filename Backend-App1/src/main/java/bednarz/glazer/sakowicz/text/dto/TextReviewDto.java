package bednarz.glazer.sakowicz.text.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TextReviewDto {
    @NotNull
    private Long reviewedTextId;
    @NotNull
    @NotBlank
    private String content;
}
