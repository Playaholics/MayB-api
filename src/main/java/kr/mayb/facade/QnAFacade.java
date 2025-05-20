package kr.mayb.facade;

import kr.mayb.data.model.Member;
import kr.mayb.data.model.Product;
import kr.mayb.data.model.UserQuestion;
import kr.mayb.dto.MemberDto;
import kr.mayb.dto.QnADto;
import kr.mayb.dto.QnAQuery;
import kr.mayb.enums.QnAStatus;
import kr.mayb.service.MemberService;
import kr.mayb.service.ProductService;
import kr.mayb.service.QnAService;
import kr.mayb.util.ContextUtils;
import kr.mayb.util.request.PageRequest;
import kr.mayb.util.response.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class QnAFacade {

    private final ProductService productService;
    private final MemberService memberService;
    private final QnAService qnAService;

    public QnADto registerQuestion(long productId, String question, boolean isSecret) {
        MemberDto member = ContextUtils.loadMember();

        Member author = memberService.getMember(member.getMemberId());
        Product product = productService.getProduct(productId);

        UserQuestion saved = qnAService.registerQuestion(product.getId(), question, isSecret, author.getId());
        return QnADto.of(saved, author, author);
    }

    public QnADto registerAnswer(long questionId, String answer) {
        MemberDto admin = ContextUtils.loadMember();
        Member member = memberService.getMember(admin.getMemberId());

        UserQuestion answered = qnAService.registerAnswer(questionId, answer);
        Member author = memberService.getMember(answered.getMemberId());

        return QnADto.of(answered, author, member);
    }

    public PageResponse<QnADto, Void> getQnAs(long productId, boolean excludeSecret, boolean onlyMine, QnAStatus status, PageRequest pageRequest) {
        Optional<MemberDto> signInMember = ContextUtils.getCurrentMember();

        if (signInMember.isEmpty() && onlyMine) {
            throw new AccessDeniedException("Only signed-in users can view.");
        }

        QnAQuery qnAQuery = QnAQuery.of(productId, excludeSecret, status, onlyMine);
        Page<UserQuestion> userQuestions = signInMember
                .map(member -> {
                    qnAQuery.setMemberId(member.getMemberId());
                    return qnAService.findAll(qnAQuery, pageRequest);
                })
                .orElseGet(() -> qnAService.findAll(qnAQuery, pageRequest));

        List<QnASimple> qnASimples = convertToQnASimple(userQuestions.getContent());

        if (signInMember.isPresent()) {
            Member reader = memberService.getMember(signInMember.get().getMemberId());
            List<QnADto> qnADtoList = qnASimples
                    .stream()
                    .map(qna -> QnADto.of(qna.userQuestion(), qna.author(), reader))
                    .toList();
            return PageResponse.of(new PageImpl<>(qnADtoList, userQuestions.getPageable(), userQuestions.getTotalElements()));
        } else {
            List<QnADto> qnaDtoList = qnASimples
                    .stream()
                    .map(qna -> QnADto.of(qna.userQuestion(), qna.author()))
                    .toList();
            return PageResponse.of(new PageImpl<>(qnaDtoList, userQuestions.getPageable(), userQuestions.getTotalElements()));
        }
    }

    public QnADto updateQuestion(long questionId, String content) {
        MemberDto member = ContextUtils.loadMember();
        UserQuestion updated = qnAService.updateQuestion(questionId, content, member.getMemberId());
        Member author = memberService.getMember(updated.getMemberId());

        return QnADto.of(updated, author, author);
    }

    private List<QnASimple> convertToQnASimple(List<UserQuestion> userQuestions) {
        Set<Long> memberIds = userQuestions
                .stream()
                .map(UserQuestion::getMemberId)
                .collect(Collectors.toSet());
        Map<Long, Member> memberMap = memberService.findAllByIdIn(memberIds);

        return userQuestions
                .stream()
                .map(qna -> {
                    Member member = memberMap.get(qna.getMemberId());
                    if (member == null) {
                        return null;
                    }

                    return new QnASimple(qna, member);
                })
                .toList();
    }

    private record QnASimple(
            UserQuestion userQuestion,
            Member author
    ) {
    }
}
