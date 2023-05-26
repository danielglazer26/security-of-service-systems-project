package bednarz.glazer.sakowicz.backendapp2.text.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class NewTextDto {
    @NotNull
    @NotBlank
    private String content;
}
