package bednarz.glazer.sakowicz.text;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/text")
public class TextController {

    @GetMapping
    public ResponseEntity<List<Text>> getReviewedTexts() {
        final List<Text> testResult = List.of(Text.builder()
                        .id(1L)
                        .author("Author username")
                        .title("Test title")
                        .content("TEST TEST TEST TEST TEST TEST")
                        .build(),
                Text.builder()
                        .id(2L)
                        .author("Author username 2")
                        .title("Test title 2")
                        .content("TEST2 TEST2 TEST2 TEST2 TEST2 TEST2")
                        .build()
        );
        return ResponseEntity.ok(testResult);
    }

    @PostMapping
    public ResponseEntity<Void> createText(@Valid @RequestBody NewTextDto newTextDto) {
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteText(@RequestParam Long id) {
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/review")
    public ResponseEntity<List<Text>> getTextsToReview() {
        final List<Text> testResult = List.of(Text.builder()
                        .id(3L)
                        .author("Author username 3")
                        .title("Test title 3")
                        .content("TEXT TO REVIEW 1")
                        .build(),
                Text.builder()
                        .id(4L)
                        .author("Author username 4")
                        .title("Test title 4")
                        .content("TEXT TO REVIEW 2")
                        .build()
        );
        return ResponseEntity.ok(testResult);
    }

    @PostMapping("/review")
    public ResponseEntity<Void> reviewText(@Valid @RequestBody TextReviewDto textReviewDto) {
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
}
