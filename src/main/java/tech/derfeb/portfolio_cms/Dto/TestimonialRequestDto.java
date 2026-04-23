package tech.derfeb.portfolio_cms.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TestimonialRequestDto {
    private String name;
    private String role;
    private String company;
    private String comment;
    private String projectWorkedOn;
    private String profileImage;
    private List<ContactLinkDto> contactLinks;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ContactLinkDto {
        private String label;
        private String url;
    }
}
