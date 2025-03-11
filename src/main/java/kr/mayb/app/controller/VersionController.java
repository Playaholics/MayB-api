package kr.mayb.app.controller;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Hidden
@RestController
public class VersionController {

    @Value("${git.commit.message.short}")
    private String commitMessage;

    @Value("${git.branch}")
    private String branch;

    @Value("${git.commit.id.abbrev}")
    private String commitId;

    @Value("${git.commit.time}")
    private String commitTime;

    @RequestMapping("/")
    public Map<String, String> getCommitId() {
        return Map.of(
                "Commit Message", commitMessage,
                "Branch", branch,
                "Commit Id", commitId,
                "Commit Time", commitTime
        );
    }
}
