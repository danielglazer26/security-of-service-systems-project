package bednarz.glazer.sakowicz.text;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TextRepository extends JpaRepository<Text, Long> {
    List<Text> findAllByAuthorIdAndReviewedIsTrue(Long authorId);
    List<Text> findAllByReviewedIsTrue();
    List<Text> findAllByReviewedIsFalse();
}
