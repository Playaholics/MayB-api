package kr.mayb.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import kr.mayb.dto.QnADto;
import kr.mayb.enums.QnAStatus;
import kr.mayb.facade.QnAFacade;
import kr.mayb.security.DenyAll;
import kr.mayb.security.PermitAdmin;
import kr.mayb.security.PermitAll;
import kr.mayb.security.PermitAuthenticated;
import kr.mayb.util.request.PageRequest;
import kr.mayb.util.response.ApiResponse;
import kr.mayb.util.response.PageResponse;
import kr.mayb.util.response.Responses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "QnA", description = "QnA API")
@DenyAll
@RestController
@RequiredArgsConstructor
public class QnAController {

    private final QnAFacade qnAFacade;

    @Operation(summary = "상품 QnA 등록")
    @PermitAuthenticated
    @PostMapping("/questions")
    public ResponseEntity<ApiResponse<QnADto>> registerQuestion(@RequestBody @Valid QuestionRequest request) {
        QnADto response = qnAFacade.registerQuestion(request.productId(), request.question(), request.isSecret());
        return Responses.ok(response);
    }

    @Operation(summary = "상품 QnA 답변 등록")
    @PermitAdmin
    @PostMapping("/questions/answers")
    public ResponseEntity<ApiResponse<QnADto>> registerAnswer(@RequestBody @Valid AnswerRequest request) {
        QnADto response = qnAFacade.registerAnswer(request.questionId(), request.answer());
        return Responses.ok(response);
    }

    @Operation(summary = "상품 QnA 조회")
    @PermitAll
    @GetMapping("/questions")
    public ResponseEntity<ApiResponse<PageResponse<QnADto, Void>>> getQnAs(@RequestParam("pid") long productId,
                                                                           @RequestParam("ex_secret") boolean excludeSecret,
                                                                           @RequestParam("only_mine") boolean onlyMine,
                                                                           @RequestParam("status") QnAStatus status,
                                                                           PageRequest pageRequest) {
        PageResponse<QnADto, Void> response = qnAFacade.getQnAs(productId, excludeSecret, onlyMine, status, pageRequest);
        return Responses.ok(response);
    }

    @Operation
    @PermitAuthenticated
    @PutMapping("/questions/{questionId}")
    public ResponseEntity<ApiResponse<QnADto>> updateQuestion(@PathVariable long questionId, @RequestBody @Valid UpdateRequest request) {
        QnADto response = qnAFacade.updateQuestion(questionId, request.content());
        return Responses.ok(response);
    }

    private record QuestionRequest(
            long productId,
            @NotBlank
            String question,
            boolean isSecret
    ) {
    }

    private record AnswerRequest(
            long questionId,
            @NotBlank
            String answer
    ) {
    }

    private record UpdateRequest(
            String content
    ) {
    }
}
