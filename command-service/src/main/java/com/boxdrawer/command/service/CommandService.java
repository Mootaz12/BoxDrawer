package com.boxdrawer.command.service;

import com.boxdrawer.command.client.DiagramClient;
import com.boxdrawer.command.dto.*;
import com.boxdrawer.command.entity.CommandLog;
import com.boxdrawer.command.parser.CommandParser;
import com.boxdrawer.command.parser.ParsedCommand;
import com.boxdrawer.command.repository.CommandLogRepository;
import feign.FeignException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class CommandService {

    private final CommandParser parser;
    private final DiagramClient diagram;
    private final CommandLogRepository logs;

    public CommandService(CommandParser parser, DiagramClient diagram, CommandLogRepository logs) {
        this.parser = parser;
        this.diagram = diagram;
        this.logs = logs;
    }

    public CommandResponse execute(CommandRequest request) {
        ParsedCommand cmd = parser.parse(request.text());
        Map<String, Object> board = null;
        String status = "OK";
        String message = "Done";

        try {
            if (cmd.action() == ParsedCommand.Action.GRID) {
                board = diagram.createGrid(request.boardId(),
                        new GridRequest(cmd.type(), cmd.color(), cmd.rows(), cmd.cols()));
            } else if (cmd.action() == ParsedCommand.Action.LINK) {
                board = diagram.createLink(request.boardId(),
                        new LinkRequest(cmd.from(), cmd.to(), cmd.label()));
            } else if (cmd.action() == ParsedCommand.Action.ADD_SHAPE) {
                board = diagram.createShape(request.boardId(),
                        new ShapeRequest(cmd.type(), cmd.color(), cmd.label()));
            } else {
                status = "UNKNOWN";
                message = "Command not understood";
            }
        } catch (FeignException e) {
            status = "ERROR";
            message = "diagram-service answered " + e.status();
        }

        CommandLog log = new CommandLog();
        log.setBoardId(request.boardId());
        log.setRawText(request.text());
        log.setAction(cmd.action().name());
        log.setStatus(status);
        log.setMessage(message);
        log = logs.save(log);

        return new CommandResponse(log.getId(), cmd.action().name(), status, message, board);
    }

    public List<CommandLogDTO> history(Long boardId) {
        return logs.findByBoardIdOrderByIdDesc(boardId).stream()
                .map(l -> new CommandLogDTO(l.getId(), l.getBoardId(), l.getRawText(), l.getAction(),
                        l.getStatus(), l.getMessage(), l.getCreatedAt()))
                .toList();
    }
}
