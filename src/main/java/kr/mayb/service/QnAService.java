package kr.mayb.service;

import kr.mayb.data.model.UserQuestion;
import kr.mayb.data.repository.UserQuestionRepository;
import kr.mayb.dto.QnAQuery;
import kr.mayb.error.ResourceNotFoundException;
import kr.mayb.util.request.PageRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class QnAService {

    private final UserQuestionRepository userQuestionRepository;

    @Transactional
    public UserQuestion registerQuestion(long productId, String question, boolean isSecret, long memberId) {
        UserQuestion userQuestion = new UserQuestion();
        userQuestion.setProductId(productId);
        userQuestion.setQuestion(question);
        userQuestion.setSecret(isSecret);
        userQuestion.setMemberId(memberId);

        return userQuestionRepository.save(userQuestion);
    }

    @Transactional
    public UserQuestion registerAnswer(long questionId, String answer) {
        UserQuestion userQuestion = userQuestionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found : " + questionId));

        userQuestion.setAnswer(answer);
        return userQuestionRepository.save(userQuestion);
    }

    public Page<UserQuestion> findAll(QnAQuery query, PageRequest pageRequest) {
        Pageable pageable = pageRequest.toPageable(Sort.by(Sort.Direction.DESC, "createdAt"));
        Specification<UserQuestion> specQuery = query.toSpecQuery();

        return userQuestionRepository.findAll(specQuery, pageable);
    }
}
