package bednarz.glazer.sakowicz.text;

import bednarz.glazer.sakowicz.userinfo.UserInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TextService {
    private final TextRepository textRepository;
    public List<Text> getReviewedTextsFor(UserInfo user) {
        // TODO
//        if (user.role() == Role.USER) {
//            return textRepository.findAllByAuthorIdAndReviewedIsTrue(user.id());
//        }
        return textRepository.findAllByReviewedIsTrue();
    }

    public void deleteById(Long id) {
        if (!textRepository.existsById(id)) {
            throw new TextNotFoundException(id);
        }
        textRepository.deleteById(id);
    }

    public void createText(NewTextDto newTextDto, UserInfo author) {
        Text newText = Text.builder()
                // TODO .authorId(author.id())
                .authorId(1L)
                .content(newTextDto.getContent())
                .reviewed(false)
                .build();
        textRepository.save(newText);
    }

    public void reviewText(TextReviewDto textReviewDto) {
        Long id = textReviewDto.getReviewedTextId();
        Text textToReview = textRepository.findById(id)
                .orElseThrow(() -> new TextNotFoundException(id));
        textToReview.setContent(textReviewDto.getContent());
        textToReview.setReviewed(true);
        textRepository.save(textToReview);
    }

    public List<Text> getTextsToReview() {
        return textRepository.findAllByReviewedIsFalse();
    }
}
