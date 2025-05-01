package kr.mayb.facade;

import io.micrometer.common.util.StringUtils;
import kr.mayb.dto.MemberDto;
import kr.mayb.dto.MemberUpdateRequest;
import kr.mayb.enums.GcsBucketPath;
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
        String profileUrl = imageService.upload(file, GcsBucketPath.PROFILE);

        if (StringUtils.isNotBlank(member.getProfileUrl())) {
            // Delete the old profile image if it exists
            imageService.delete(member.getProfileUrl(), GcsBucketPath.PROFILE);
        }

        return memberService.updateProfile(member.getMemberId(), profileUrl);
    }

    public MemberDto updateMember(MemberUpdateRequest request) {
        MemberDto member = ContextUtils.loadMember();
        return memberService.updateMember(member.getMemberId(), request);
    }
}
