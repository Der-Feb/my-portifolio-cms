package tech.derfeb.portfolio_cms.Dto;

import lombok.Data;
import java.util.List;

@Data
public class PartnerRequestDto {
    private String name;
    private String imagePreview;
    private List<String> links;
}
