package kr.mayb.service;

import jakarta.transaction.Transactional;
import kr.mayb.data.model.Member;
import kr.mayb.data.model.UserQuestion;
import kr.mayb.data.repository.UserQuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class QnAService {

    private final UserQuestionRepository userQuestionRepository;

    @Transactional
    public UserQuestion registerQuestion(long productId, String question, boolean isSecret, Member author) {
        UserQuestion userQuestion = new UserQuestion();
        userQuestion.setProductId(productId);
        userQuestion.setQuestion(question);
        userQuestion.setSecret(isSecret);
        userQuestion.setMember(author);

        return userQuestionRepository.save(userQuestion);
    }
}
