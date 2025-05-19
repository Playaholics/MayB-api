package kr.mayb.dto;

import kr.mayb.data.model.Member;
import kr.mayb.data.model.UserQuestion;

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
    public static QnADto of(UserQuestion userQuestion, long memberId) {
        Member author = userQuestion.getMember();
        String maskedName = author.getMaskedName();
        String answer = getAnswer(userQuestion, author.getId(), memberId);
        boolean isAnswered = Optional.ofNullable(userQuestion.getAnswer()).isPresent();

        return new QnADto(
                userQuestion.getId(),
                userQuestion.getQuestion(),
                answer,
                maskedName,
                userQuestion.getCreatedAt(),
                isAnswered,
                userQuestion.isSecret(),
                isMyQuestion(author.getId(), memberId)
        );
    }

    private static String getAnswer(UserQuestion userQuestion, long authorId, long memberId) {
        if (userQuestion.isSecret() && !isMyQuestion(authorId, memberId)) {
            return null;
        }

        return userQuestion.getAnswer();
    }

    private static boolean isMyQuestion(long authorId, long currentMemberId) {
        return authorId == currentMemberId;
    }
}
