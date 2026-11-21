package com.boxdrawer.command.controller;

import com.boxdrawer.command.dto.CommandLogDTO;
import com.boxdrawer.command.dto.CommandRequest;
import com.boxdrawer.command.dto.CommandResponse;
import com.boxdrawer.command.service.CommandService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/commands")
public class CommandController {

    private final CommandService service;

    public CommandController(CommandService service) {
        this.service = service;
    }

    @PostMapping
    public CommandResponse execute(@Valid @RequestBody CommandRequest request) {
        return service.execute(request);
    }

    @GetMapping
    public List<CommandLogDTO> history(@RequestParam Long boardId) {
        return service.history(boardId);
    }
}
