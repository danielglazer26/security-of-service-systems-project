package bednarz.glazer.sakowicz.backendapp1.text;

import bednarz.glazer.sakowicz.backendapp1.text.dto.NewTextDto;
import bednarz.glazer.sakowicz.backendapp1.text.dto.TextDto;
import bednarz.glazer.sakowicz.backendapp1.text.dto.TextReviewDto;
import bednarz.glazer.sakowicz.backendapp1.text.exception.TextNotFoundException;
import bednarz.glazer.sakowicz.backendapp1.userinfo.Role;
import bednarz.glazer.sakowicz.backendapp1.userinfo.UserInfo;
import bednarz.glazer.sakowicz.backendapp1.userinfo.UserInfoRequest;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

import static java.util.stream.Collectors.toSet;

@Service
@RequiredArgsConstructor
public class TextService {
    private static final int MAX_SSO_USER_INFO_IDS = 30;
    @Value("${sso.url.userinfo}")
    private String userInfoUrl;
    @Value("${app.name}")
    private String applicationName;
    private final TextRepository textRepository;
    private final RestTemplate restTemplate;

    public List<TextDto> getReviewedTextsFor(UserInfo user, HttpServletRequest request) {
        if (user.role() == Role.USER) {
            return getOwnReviewedTextsFor(user);
        }
        var reviewedTexts = textRepository.findAllByReviewedIsTrue();
        var authorIds = getUniqueAuthorIds(reviewedTexts);
        var authors = mapIdsToUsernames(authorIds, request);
        return reviewedTexts.stream()
                .map(text -> text.toTextDto(authors.get(text.getAuthorId())))
                .toList();
    }

    public void deleteById(Long id) {
        if (!textRepository.existsById(id)) {
            throw new TextNotFoundException(id);
        }
        textRepository.deleteById(id);
    }

    public void createText(NewTextDto newTextDto, UserInfo author) {
        Text newText = Text.builder()
                .authorId(author.id())
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

    public List<TextDto> getTextsToReview(HttpServletRequest request) {
        var textsToReview = textRepository.findAllByReviewedIsFalse();
        var authorIds = getUniqueAuthorIds(textsToReview);
        var authors = mapIdsToUsernames(authorIds, request);
        return textsToReview.stream()
                .map(text -> text.toTextDto(authors.get(text.getAuthorId())))
                .toList();
    }

    private List<TextDto> getOwnReviewedTextsFor(UserInfo user) {
        return textRepository.findAllByAuthorIdAndReviewedIsTrue(user.id()).stream()
                .map(text -> text.toTextDto(user.user()))
                .toList();
    }

    private List<Long> getUniqueAuthorIds(List<Text> texts) {
        return texts.stream()
                .map(Text::getAuthorId)
                .collect(toSet())
                .stream()
                .toList();
    }

    private Map<Long, String> mapIdsToUsernames(List<Long> userIds, HttpServletRequest request) {
        Map<Long, String> result = new HashMap<>();

        for (int i = 0; i < userIds.size(); i += MAX_SSO_USER_INFO_IDS) {
            var ids = userIds.stream().skip(i).limit(MAX_SSO_USER_INFO_IDS).toList();
            var usernames = getUsernamesFor(ids, request);

            for (int j = 0; j < ids.size(); j++) {
                result.put(ids.get(j), usernames.get(j));
            }
        }

        return result;
    }

    private List<String> getUsernamesFor(List<Long> ids, HttpServletRequest request) {
        RequestEntity<UserInfoRequest> requestEntity = RequestEntity
                .post(userInfoUrl)
                .header(HttpHeaders.COOKIE, request.getHeader("Cookie"))
                .body(new UserInfoRequest(ids, applicationName));

        ResponseEntity<UserInfo[]> usernamesResponse = restTemplate.exchange(requestEntity, UserInfo[].class);
        return Arrays.stream(Objects.requireNonNull(usernamesResponse.getBody()))
                .map(UserInfo::user)
                .toList();
    }
}
