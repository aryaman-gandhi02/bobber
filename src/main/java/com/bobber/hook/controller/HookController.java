package com.bobber.hook.controller;

import com.bobber.hook.dto.HookCreateResponseDTO;
import com.bobber.hook.service.HookService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/hooks")
@RequiredArgsConstructor
public class HookController {

    private final HookService hookService;

    @PostMapping
    public HookCreateResponseDTO createHook() {
        return hookService.generateHook();
    }
}
