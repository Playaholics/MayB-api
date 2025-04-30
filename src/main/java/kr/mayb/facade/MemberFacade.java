package kr.mayb.facade;

import kr.mayb.dto.MemberDto;
import kr.mayb.dto.MemberUpdateRequest;
import kr.mayb.enums.GcsPath;
import kr.mayb.service.ImageService;
import kr.mayb.service.MemberService;
import kr.mayb.util.ContextUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
@RequiredArgsConstructor
public class MemberFacade {


    private final ImageService imageService;
    private final MemberService memberService;

    public MemberDto updateProfile(MultipartFile file) {
        MemberDto member = ContextUtils.loadMember();
        String profileUrl = imageService.upload(file, GcsPath.PROFILE);

        return memberService.updateProfile(member.getMemberId(), profileUrl);
    }

    public MemberDto updateMember(MemberUpdateRequest request) {
        MemberDto member = ContextUtils.loadMember();
        return memberService.updateMember(member.getMemberId(), request);
    }
}
