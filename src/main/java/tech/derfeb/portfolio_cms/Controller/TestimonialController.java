package tech.derfeb.portfolio_cms.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.derfeb.portfolio_cms.Dto.TestimonialRequestDto;
import tech.derfeb.portfolio_cms.Model.TestimonialModel;
import tech.derfeb.portfolio_cms.Service.TestimonialService;

import java.util.List;

@RestController
@RequestMapping("/api/testimonials")
public class TestimonialController {

    @Autowired
    private TestimonialService testimonialService;

    @PostMapping
    public ResponseEntity<TestimonialModel> createTestimonial(@RequestBody TestimonialRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(testimonialService.createTestimonial(dto));
    }

    @GetMapping
    public ResponseEntity<List<TestimonialModel>> getAllTestimonials() {
        return ResponseEntity.ok(testimonialService.getAllTestimonials());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TestimonialModel> getTestimonialById(@PathVariable String id) {
        return ResponseEntity.ok(testimonialService.getTestimonialById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TestimonialModel> updateTestimonial(
            @PathVariable String id,
            @RequestBody TestimonialRequestDto dto) {
        return ResponseEntity.ok(testimonialService.updateTestimonial(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTestimonial(@PathVariable String id) {
        testimonialService.deleteTestimonial(id);
        return ResponseEntity.noContent().build();
    }
}
