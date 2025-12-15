package com.bobber.controller;

import com.bobber.entity.Hook;
import com.bobber.repository.HookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/hook")
@RequiredArgsConstructor
public class HookController {

    private final HookRepository hookRepository;

    @PostMapping
    public Hook createHook() {
        Hook hook = new Hook();
        return hookRepository.save(hook);
    }
}
