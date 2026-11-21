package com.boxdrawer.diagram.controller;

import com.boxdrawer.diagram.service.BoardService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Public one-time download links sent in share emails (no JWT). */
@RestController
@RequestMapping("/shared")
public class SharedController {

    private final BoardService service;

    public SharedController(BoardService service) {
        this.service = service;
    }

    @GetMapping("/{token}")
    public ResponseEntity<Map<String, Object>> download(@PathVariable String token) {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"board.excalidraw\"")
                .contentType(MediaType.APPLICATION_JSON)
                .body(service.exportShared(token));
    }
}
