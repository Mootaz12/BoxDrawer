package com.boxdrawer.command.client;

import com.boxdrawer.command.dto.GridRequest;
import com.boxdrawer.command.dto.LinkRequest;
import com.boxdrawer.command.dto.ShapeRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

/** Synchronous call to the service named in clients.diagram-service.name, resolved through Eureka. */
@FeignClient(name = "${clients.diagram-service.name}")
public interface DiagramClient {

    @PostMapping("/boards/{boardId}/shapes")
    Map<String, Object> createShape(@PathVariable("boardId") Long boardId, @RequestBody ShapeRequest request);

    @PostMapping("/boards/{boardId}/grid")
    Map<String, Object> createGrid(@PathVariable("boardId") Long boardId, @RequestBody GridRequest request);

    @PostMapping("/boards/{boardId}/links")
    Map<String, Object> createLink(@PathVariable("boardId") Long boardId, @RequestBody LinkRequest request);
}
