package kr.mayb.facade;

import kr.mayb.data.model.Member;
import kr.mayb.dto.MemberDto;
import kr.mayb.enums.GcsFolderPath;
import kr.mayb.service.GcsService;
import kr.mayb.service.MemberService;
import kr.mayb.util.ContextUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
@RequiredArgsConstructor
public class MemberFacade {

    private final GcsService gcsService;
    private final MemberService memberService;

    public MemberDto updateProfile(MultipartFile file) {
        MemberDto member = ContextUtils.loadMember();
        String profileUrl = gcsService.uploadFile(file, GcsFolderPath.PROFILE);

        Member updated = memberService.updateProfile(member.getMemberId(), profileUrl);
        return memberService.convertToMemberDto(updated);
    }
}
