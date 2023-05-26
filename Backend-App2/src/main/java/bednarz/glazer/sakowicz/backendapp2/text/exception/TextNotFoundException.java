package bednarz.glazer.sakowicz.backendapp2.text.exception;

public class TextNotFoundException extends RuntimeException {
    public TextNotFoundException(Long id) {
        super("Text with id = %d does not exist".formatted(id));
    }
}
