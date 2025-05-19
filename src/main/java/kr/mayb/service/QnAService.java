package kr.mayb.service;

import kr.mayb.data.model.Member;
import kr.mayb.data.model.UserQuestion;
import kr.mayb.data.repository.UserQuestionRepository;
import kr.mayb.error.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    public UserQuestion registerAnswer(long questionId, String answer) {
        UserQuestion userQuestion = userQuestionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found : " + questionId));

        userQuestion.setAnswer(answer);
        return userQuestionRepository.save(userQuestion);
    }
}
