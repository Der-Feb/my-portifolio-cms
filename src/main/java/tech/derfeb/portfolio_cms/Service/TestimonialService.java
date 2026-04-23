package tech.derfeb.portfolio_cms.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import tech.derfeb.portfolio_cms.Dto.TestimonialRequestDto;
import tech.derfeb.portfolio_cms.Model.TestimonialModel;
import tech.derfeb.portfolio_cms.Repository.TestimonialRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TestimonialService {

    @Autowired
    private TestimonialRepository testimonialRepository;

    public TestimonialModel createTestimonial(TestimonialRequestDto dto) {
        TestimonialModel testimonial = new TestimonialModel();
        updateModelFromDto(testimonial, dto);
        return testimonialRepository.save(testimonial);
    }

    public List<TestimonialModel> getAllTestimonials() {
        return testimonialRepository.findAll();
    }

    public TestimonialModel getTestimonialById(String id) {
        return testimonialRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Testimonial not found"));
    }

    public TestimonialModel updateTestimonial(String id, TestimonialRequestDto dto) {
        TestimonialModel testimonial = getTestimonialById(id);
        updateModelFromDto(testimonial, dto);
        return testimonialRepository.save(testimonial);
    }

    public void deleteTestimonial(String id) {
        if (!testimonialRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Testimonial not found");
        }
        testimonialRepository.deleteById(id);
    }

    private void updateModelFromDto(TestimonialModel testimonial, TestimonialRequestDto dto) {
        if (dto.getName() != null) testimonial.setName(dto.getName());
        if (dto.getRole() != null) testimonial.setRole(dto.getRole());
        if (dto.getCompany() != null) testimonial.setCompany(dto.getCompany());
        if (dto.getComment() != null) testimonial.setComment(dto.getComment());
        if (dto.getProjectWorkedOn() != null) testimonial.setProjectWorkedOn(dto.getProjectWorkedOn());
        if (dto.getProfileImage() != null) testimonial.setProfileImage(dto.getProfileImage());
        
        if (dto.getContactLinks() != null) {
            testimonial.setContactLinks(dto.getContactLinks().stream()
                    .map(linkDto -> new TestimonialModel.ContactLink(linkDto.getLabel(), linkDto.getUrl()))
                    .collect(Collectors.toList()));
        }
    }
}
