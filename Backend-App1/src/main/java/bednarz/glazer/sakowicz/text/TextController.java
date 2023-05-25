package bednarz.glazer.sakowicz.text;

import bednarz.glazer.sakowicz.userinfo.UserInfo;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/text")
@RequiredArgsConstructor
public class TextController {
    private final TextService textService;

    @GetMapping
    public ResponseEntity<List<TextDto>> getReviewedTexts(Authentication authentication) {
        UserInfo user = (UserInfo) authentication.getPrincipal();
        var reviewedTexts = textService.getReviewedTextsFor(user).stream()
                .map(Text::toTextDto)
                .toList();
        return ResponseEntity.ok(reviewedTexts);
    }

    @PostMapping
    public ResponseEntity<Void> createText(@Valid @RequestBody NewTextDto newTextDto, Authentication authentication) {
        UserInfo user = (UserInfo) authentication.getPrincipal();
        textService.createText(newTextDto, user);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteText(@RequestParam Long id) {
        textService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/review")
    public ResponseEntity<List<TextDto>> getTextsToReview() {
        var textsToReview = textService.getTextsToReview().stream()
                .map(Text::toTextDto)
                .toList();
        return ResponseEntity.ok(textsToReview);
    }

    @PostMapping("/review")
    public ResponseEntity<Void> reviewText(@Valid @RequestBody TextReviewDto textReviewDto) {
        textService.reviewText(textReviewDto);
        return ResponseEntity.noContent().build();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }

    @ExceptionHandler(TextNotFoundException.class)
    public ResponseEntity<String> handleTextNotFoundException(TextNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ex.getMessage());
    }
}
