package com.boxdrawer.diagram.mapper;

import com.boxdrawer.diagram.entity.Board;
import com.boxdrawer.diagram.entity.Link;
import com.boxdrawer.diagram.entity.Shape;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Converts a board into an Excalidraw file (opens on excalidraw.com). */
public final class ExcalidrawMapper {

    private static final int SIZE = 60;
    private static final int FONT_SIZE = 16;

    private ExcalidrawMapper() {
    }

    public static Map<String, Object> toFile(Board board) {
        List<Map<String, Object>> elements = new ArrayList<>();
        for (Shape s : board.getShapes()) {
            elements.add(shape(s));
            if (s.getLabel() != null && !s.getLabel().isBlank()) {
                elements.add(text("label-" + s.getId(), s.getLabel(), s.getPosX() + SIZE / 2, s.getPosY() + SIZE + 14));
            }
        }
        for (Link l : board.getLinks()) {
            elements.add(arrow(l));
            if (l.getLabel() != null && !l.getLabel().isBlank()) {
                int cx = (l.getFromShape().getPosX() + l.getToShape().getPosX()) / 2 + SIZE / 2;
                int cy = (l.getFromShape().getPosY() + l.getToShape().getPosY()) / 2 + SIZE / 2;
                elements.add(text("link-label-" + l.getId(), l.getLabel(), cx, cy - 14));
            }
        }
        return Map.of(
                "type", "excalidraw",
                "version", 2,
                "source", "boxdrawer",
                "elements", elements,
                "appState", Map.of("viewBackgroundColor", "#ffffff"),
                "files", Map.of());
    }

    private static Map<String, Object> shape(Shape s) {
        Map<String, Object> e = base("shape-" + s.getId(), s.getPosX(), s.getPosY(), SIZE, SIZE);
        e.put("backgroundColor", s.getColor());
        switch (s.getType()) {
            case "CIRCLE" -> e.put("type", "ellipse");
            case "TRIANGLE" -> {
                e.put("type", "line");
                e.put("points", List.of(List.of(SIZE / 2, 0), List.of(SIZE, SIZE), List.of(0, SIZE), List.of(SIZE / 2, 0)));
            }
            default -> e.put("type", "rectangle");
        }
        return e;
    }

    private static Map<String, Object> arrow(Link l) {
        int x1 = l.getFromShape().getPosX() + SIZE / 2;
        int y1 = l.getFromShape().getPosY() + SIZE / 2;
        int dx = l.getToShape().getPosX() + SIZE / 2 - x1;
        int dy = l.getToShape().getPosY() + SIZE / 2 - y1;
        Map<String, Object> e = base("link-" + l.getId(), x1, y1, Math.abs(dx), Math.abs(dy));
        e.put("type", "arrow");
        e.put("points", List.of(List.of(0, 0), List.of(dx, dy)));
        e.put("startArrowhead", null);
        e.put("endArrowhead", "arrow");
        return e;
    }

    private static Map<String, Object> text(String id, String value, int centerX, int y) {
        int width = value.length() * FONT_SIZE / 2;
        Map<String, Object> e = base(id, centerX - width / 2, y, width, 20);
        e.put("type", "text");
        e.put("text", value);
        e.put("originalText", value);
        e.put("fontSize", FONT_SIZE);
        e.put("fontFamily", 1);
        e.put("textAlign", "center");
        e.put("verticalAlign", "top");
        e.put("lineHeight", 1.25);
        e.put("containerId", null);
        return e;
    }

    private static Map<String, Object> base(String id, int x, int y, int width, int height) {
        Map<String, Object> e = new HashMap<>();
        e.put("id", id);
        e.put("x", x);
        e.put("y", y);
        e.put("width", width);
        e.put("height", height);
        e.put("angle", 0);
        e.put("strokeColor", "#1e1e1e");
        e.put("backgroundColor", "transparent");
        e.put("fillStyle", "solid");
        e.put("strokeWidth", 2);
        e.put("strokeStyle", "solid");
        e.put("roughness", 1);
        e.put("opacity", 100);
        e.put("groupIds", List.of());
        e.put("roundness", null);
        e.put("seed", Math.abs(id.hashCode()));
        e.put("version", 1);
        e.put("versionNonce", 0);
        e.put("isDeleted", false);
        e.put("boundElements", null);
        e.put("link", null);
        e.put("locked", false);
        return e;
    }
}
