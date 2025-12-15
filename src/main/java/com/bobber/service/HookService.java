package com.bobber.service;

import com.bobber.api.dto.HookCreateResponseDTO;
import com.bobber.domain.Hook;
import com.bobber.repository.HookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
}
