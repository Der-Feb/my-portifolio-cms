package tech.derfeb.portfolio_cms.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import tech.derfeb.portfolio_cms.Model.UserModel;
import tech.derfeb.portfolio_cms.Repository.UserRepository;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class AvailabilityService {

    @Autowired
    private UserRepository userRepository;

    // Store all SSE connections
    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    public Boolean getAvailabilityStatus() {
        // Get the first user (since it's single-user CMS)
        return userRepository.findAll().stream()
                .findFirst()
                .map(UserModel::getAvailableForWork)
                .orElse(false);
    }

    public Boolean updateAvailabilityStatus(String username, Boolean availableForWork) {
        UserModel user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setAvailableForWork(availableForWork);
        userRepository.save(user);

        // Notify all SSE listeners
        notifyStatusChange(availableForWork);

        return availableForWork;
    }

    public SseEmitter createEmitter() {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE); // No timeout
        emitters.add(emitter);

        // Remove emitter when completed or timed out
        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(() -> emitters.remove(emitter));
        emitter.onError((e) -> emitters.remove(emitter));

        // Send initial status
        try {
            emitter.send(SseEmitter.event()
                    .name("availability-status")
                    .data(getAvailabilityStatus()));
        } catch (IOException e) {
            emitters.remove(emitter);
        }

        return emitter;
    }

    private void notifyStatusChange(Boolean availableForWork) {
        List<SseEmitter> deadEmitters = new CopyOnWriteArrayList<>();

        emitters.forEach(emitter -> {
            try {
                emitter.send(SseEmitter.event()
                        .name("availability-status")
                        .data(availableForWork));
            } catch (IOException e) {
                deadEmitters.add(emitter);
            }
        });

        // Remove dead emitters
        emitters.removeAll(deadEmitters);
    }
}
