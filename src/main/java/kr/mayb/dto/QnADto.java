package kr.mayb.dto;

import kr.mayb.data.model.Authority;
import kr.mayb.data.model.Member;
import kr.mayb.data.model.UserQuestion;
import kr.mayb.enums.AuthorityName;

import java.time.OffsetDateTime;
import java.util.Optional;

public record QnADto(
        long userQuestionId,
        String question,
        String answer,
        String authorName,
        OffsetDateTime createdAt,
        boolean isAnswered,
        boolean isSecret,
        boolean isMyQuestion
) {
    public static QnADto of(UserQuestion userQuestion, Member author, Member reader) {
        String maskedName = author.getMaskedName();
        String answer = getAnswer(userQuestion, author.getId(), reader);
        boolean isAnswered = Optional.ofNullable(userQuestion.getAnswer()).isPresent();

        return new QnADto(
                userQuestion.getId(),
                userQuestion.getQuestion(),
                answer,
                maskedName,
                userQuestion.getCreatedAt(),
                isAnswered,
                userQuestion.isSecret(),
                isMyQuestion(author.getId(), reader.getId())
        );
    }

    public static QnADto of(UserQuestion userQuestion, Member author) {
        String maskedName = author.getMaskedName();

        String question;
        if (userQuestion.isSecret()) {
            question = null;
        } else {
            question = userQuestion.getQuestion();
        }

        String answer;
        if (userQuestion.isSecret()) {
            answer = null;
        } else {
            answer = userQuestion.getAnswer();
        }

        boolean isAnswered = Optional.ofNullable(userQuestion.getAnswer()).isPresent();

        return new QnADto(
                userQuestion.getId(),
                question,
                answer,
                maskedName,
                userQuestion.getCreatedAt(),
                isAnswered,
                userQuestion.isSecret(),
                false
        );
    }

    private static String getQuestion(UserQuestion userQuestion, long authorId, Member reader) {
        boolean isAdmin = reader.getAuthorities()
                .stream()
                .map(Authority::getName)
                .anyMatch(name -> name == AuthorityName.ROLE_ADMIN);

        // Admin can see all questions
        if (isAdmin) {
            return userQuestion.getQuestion();
        }

        // If the question is secret and the member is not the author, return null
        if (userQuestion.isSecret() && !isMyQuestion(authorId, reader.getId())) {
            return null;
        }

        return userQuestion.getQuestion();
    }

    private static String getAnswer(UserQuestion userQuestion, long authorId, Member reader) {
        boolean isAdmin = reader.getAuthorities()
                .stream()
                .map(Authority::getName)
                .anyMatch(name -> name == AuthorityName.ROLE_ADMIN);

        // Admin can see all answers
        if (isAdmin) {
            return userQuestion.getAnswer();
        }

        // If the question is secret and the member is not the author, return null
        if (userQuestion.isSecret() && !isMyQuestion(authorId, reader.getId())) {
            return null;
        }

        return userQuestion.getAnswer();
    }

    private static boolean isMyQuestion(long authorId, long readerId) {
        return authorId == readerId;
    }
}
