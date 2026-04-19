package tech.derfeb.portfolio_cms.Dto;

import lombok.Data;
import java.util.List;

@Data
public class ProjectRequestDto {
    private String name;
    private String description;
    private String reviewImage;
    private String githubLink;
    private String liveLink;
    private List<String> techStack;
    private List<PartnerRequestDto> partners;
}
