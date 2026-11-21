package com.boxdrawer.diagram.mapper;

import com.boxdrawer.diagram.dto.BoardResponse;
import com.boxdrawer.diagram.dto.LinkDTO;
import com.boxdrawer.diagram.dto.ShapeDTO;
import com.boxdrawer.diagram.entity.Board;
import com.boxdrawer.diagram.entity.Shape;
import com.boxdrawer.diagram.entity.Tag;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

public final class BoardMapper {

    private BoardMapper() {
    }

    public static BoardResponse toResponse(Board b) {
        List<ShapeDTO> shapes = b.getShapes().stream()
                .sorted(Comparator.comparingInt(Shape::getSeq))
                .map(BoardMapper::toDto)
                .toList();
        List<LinkDTO> links = b.getLinks().stream()
                .map(l -> new LinkDTO(l.getId(), l.getFromShape().getSeq(), l.getToShape().getSeq(), l.getLabel()))
                .toList();
        Set<String> tags = b.getTags().stream()
                .map(Tag::getName)
                .collect(Collectors.toCollection(TreeSet::new));
        return new BoardResponse(b.getId(), b.getName(), b.getOwner(), b.getCreatedAt(), shapes, links, tags);
    }

    public static ShapeDTO toDto(Shape s) {
        return new ShapeDTO(s.getId(), s.getSeq(), s.getType(), s.getColor(), s.getLabel(), s.getPosX(), s.getPosY());
    }
}
