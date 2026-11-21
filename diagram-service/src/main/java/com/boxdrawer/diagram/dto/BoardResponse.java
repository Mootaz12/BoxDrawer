package com.boxdrawer.diagram.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public record BoardResponse(Long id, String name, String owner, LocalDateTime createdAt,
                            List<ShapeDTO> shapes, List<LinkDTO> links, Set<String> tags) {
}
