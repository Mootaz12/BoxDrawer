package com.boxdrawer.diagram.controller;

import com.boxdrawer.diagram.dto.*;
import com.boxdrawer.diagram.service.BoardService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/boards")
public class BoardController {

    private final BoardService service;

    public BoardController(BoardService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BoardResponse create(@Valid @RequestBody BoardRequest request,
                                @RequestHeader(value = "X-User", defaultValue = "anonymous") String user) {
        return service.create(request, user);
    }

    @GetMapping
    public List<BoardResponse> list() {
        return service.list();
    }

    @GetMapping("/{id}")
    public BoardResponse get(@PathVariable Long id) {
        return service.get(id);
    }

    @GetMapping("/{id}/export")
    public ResponseEntity<Map<String, Object>> export(@PathVariable Long id) {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"board-" + id + ".excalidraw\"")
                .contentType(MediaType.APPLICATION_JSON)
                .body(service.export(id));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    @PostMapping("/{id}/shapes")
    public BoardResponse addShape(@PathVariable Long id, @Valid @RequestBody ShapeRequest request) {
        return service.addShape(id, request);
    }

    @PostMapping("/{id}/grid")
    public BoardResponse addGrid(@PathVariable Long id, @Valid @RequestBody GridRequest request) {
        return service.addGrid(id, request);
    }

    @PostMapping("/{id}/links")
    public BoardResponse addLink(@PathVariable Long id, @Valid @RequestBody LinkRequest request) {
        return service.addLink(id, request);
    }

    @PostMapping("/{id}/tags/{name}")
    public BoardResponse addTag(@PathVariable Long id, @PathVariable String name) {
        return service.addTag(id, name);
    }

    @PostMapping("/{id}/share")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Map<String, String> share(@PathVariable Long id, @Valid @RequestBody ShareRequest request,
                                     @RequestHeader(value = "X-User", defaultValue = "anonymous") String user) {
        service.share(id, request.email(), user);
        return Map.of("status", "accepted", "message", "Email will be sent to " + request.email());
    }
}
