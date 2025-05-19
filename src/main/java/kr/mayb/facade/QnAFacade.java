package kr.mayb.facade;

import kr.mayb.data.model.Member;
import kr.mayb.data.model.Product;
import kr.mayb.data.model.UserQuestion;
import kr.mayb.dto.MemberDto;
import kr.mayb.dto.QnADto;
import kr.mayb.service.MemberService;
import kr.mayb.service.ProductService;
import kr.mayb.service.QnAService;
import kr.mayb.util.ContextUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

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

        UserQuestion saved = qnAService.registerQuestion(product.getId(), question, isSecret, author);
        return QnADto.of(saved, author);
    }

    public QnADto registerAnswer(long questionId, String answer) {
        MemberDto admin = ContextUtils.loadMember();

        Member member = memberService.getMember(admin.getMemberId());
        UserQuestion answered = qnAService.registerAnswer(questionId, answer);
        return QnADto.of(answered, member);
    }
}
