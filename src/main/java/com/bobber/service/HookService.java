package com.bobber.service;

import com.bobber.api.dto.HookCreateResponseDTO;
import com.bobber.domain.Hook;
import com.bobber.repository.HookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class HookService {

    private final HookRepository hookRepository;

    public HookCreateResponseDTO generateHook() {
        Hook hook = new Hook();
        hook = hookRepository.save(hook);

        return new HookCreateResponseDTO(
                hook.getId(),
                hook.getSecret(),
                hook.getCreatedAt()
        );
    }

    public Hook requireHook(UUID hookId, String secret) {
        return hookRepository.findById(hookId)
                .filter(h -> h.getSecret().equals(secret))
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND)
                );
    }

    public Hook requireHook(UUID hookId) {
        return hookRepository.findById(hookId)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND)
                );
    }
}
