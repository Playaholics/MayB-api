package kr.mayb.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import kr.mayb.facade.QnAFacade;
import kr.mayb.security.DenyAll;
import kr.mayb.security.PermitAuthenticated;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "QnA", description = "QnA API")
@DenyAll
@RestController
@RequiredArgsConstructor
public class QnAController {

    private final QnAFacade qnAFacade;

    @Operation(summary = "상품 QnA 등록")
    @PermitAuthenticated
    @PostMapping("/questions")
    public void registerQuestion(@RequestBody @Valid QnARequest qnARequest) {
        qnAFacade.registerQuestion(qnARequest.productId(), qnARequest.question(), qnARequest.isSecret());
    }

    private record QnARequest(
            long productId,
            @NotBlank
            String question,
            boolean isSecret
    ) {
    }
}
