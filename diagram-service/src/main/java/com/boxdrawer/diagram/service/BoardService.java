package com.boxdrawer.diagram.service;

import com.boxdrawer.diagram.dto.*;
import com.boxdrawer.diagram.entity.Board;
import com.boxdrawer.diagram.entity.Link;
import com.boxdrawer.diagram.entity.Shape;
import com.boxdrawer.diagram.entity.ShareToken;
import com.boxdrawer.diagram.entity.Tag;
import com.boxdrawer.diagram.event.BoardEventPublisher;
import com.boxdrawer.diagram.event.BoardSharedEvent;
import com.boxdrawer.diagram.mapper.BoardMapper;
import com.boxdrawer.diagram.mapper.ExcalidrawMapper;
import com.boxdrawer.diagram.repository.BoardRepository;
import com.boxdrawer.diagram.repository.ShareTokenRepository;
import com.boxdrawer.diagram.repository.TagRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@Transactional
public class BoardService {

    private static final Set<String> TYPES = Set.of("SQUARE", "CIRCLE", "TRIANGLE");
    // Automatic layout: shapes are placed on a grid, the client never sends coordinates
    private static final int START = 40;
    private static final int STEP = 100;
    private static final int MAX_PER_ROW = 8;

    private final BoardRepository boards;
    private final TagRepository tags;
    private final ShareTokenRepository shareTokens;
    private final BoardEventPublisher publisher;

    public BoardService(BoardRepository boards, TagRepository tags, ShareTokenRepository shareTokens,
                        BoardEventPublisher publisher) {
        this.boards = boards;
        this.tags = tags;
        this.shareTokens = shareTokens;
        this.publisher = publisher;
    }

    public BoardResponse create(BoardRequest request, String owner) {
        Board board = new Board();
        board.setName(request.name());
        board.setOwner(owner);
        return BoardMapper.toResponse(boards.save(board));
    }

    @Transactional(readOnly = true)
    public List<BoardResponse> list() {
        return boards.findAll().stream().map(BoardMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public BoardResponse get(Long id) {
        return BoardMapper.toResponse(find(id));
    }

    public void delete(Long id) {
        boards.delete(find(id));
    }

    public BoardResponse addShape(Long boardId, ShapeRequest request) {
        Board board = find(boardId);
        String type = checkType(request.type());
        int seq = nextSeq(board);
        int x = START;
        int y = START;
        Shape last = lastShape(board);
        if (last != null) {
            x = last.getPosX() + STEP;
            y = last.getPosY();
            if (x > START + (MAX_PER_ROW - 1) * STEP) {
                x = START;
                y += STEP;
            }
        }
        board.getShapes().add(newShape(board, type, request.color(), request.label(), seq, x, y));
        boards.flush();
        return BoardMapper.toResponse(board);
    }

    public BoardResponse addGrid(Long boardId, GridRequest request) {
        Board board = find(boardId);
        String type = checkType(request.type());
        int start = nextSeq(board);
        int baseY = board.getShapes().isEmpty() ? START
                : board.getShapes().stream().mapToInt(Shape::getPosY).max().orElse(0) + STEP;
        int total = request.rows() * request.cols();
        for (int i = 0; i < total; i++) {
            int x = START + (i % request.cols()) * STEP;
            int y = baseY + (i / request.cols()) * STEP;
            board.getShapes().add(newShape(board, type, request.color(), null, start + i, x, y));
        }
        boards.flush();
        return BoardMapper.toResponse(board);
    }

    public BoardResponse addLink(Long boardId, LinkRequest request) {
        Board board = find(boardId);
        if (request.fromSeq().equals(request.toSeq())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A shape cannot be linked to itself");
        }
        Link link = new Link();
        link.setBoard(board);
        link.setFromShape(findShape(board, request.fromSeq()));
        link.setToShape(findShape(board, request.toSeq()));
        link.setLabel(request.label());
        board.getLinks().add(link);
        boards.flush();
        return BoardMapper.toResponse(board);
    }

    public BoardResponse addTag(Long boardId, String tagName) {
        Board board = find(boardId);
        String name = tagName.trim().toLowerCase();
        Tag tag = tags.findByName(name).orElseGet(() -> tags.save(new Tag(name)));
        board.getTags().add(tag);
        boards.flush();
        return BoardMapper.toResponse(board);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> export(Long id) {
        return ExcalidrawMapper.toFile(find(id));
    }

    public Map<String, Object> exportShared(String token) {
        if (shareTokens.markUsed(token) == 0) {
            throw new ResponseStatusException(HttpStatus.GONE, "Link invalid or already used");
        }
        return ExcalidrawMapper.toFile(shareTokens.findById(token).orElseThrow().getBoard());
    }

    /** Saves a one-time token and publishes an event; notification-service sends the email. */
    public void share(Long boardId, String email, String sharedBy) {
        Board board = find(boardId);
        ShareToken token = shareTokens.save(new ShareToken(board, email));
        publisher.publishBoardShared(new BoardSharedEvent(email, token.getToken(), board.getName(), sharedBy));
    }

    // ---------- helpers ----------

    private Board find(Long id) {
        return boards.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Board not found: " + id));
    }

    private Shape findShape(Board board, int seq) {
        return board.getShapes().stream()
                .filter(s -> s.getSeq() == seq)
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Shape " + seq + " not found"));
    }

    private String checkType(String type) {
        String t = type.trim().toUpperCase();
        if (!TYPES.contains(t)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unknown shape type: " + type);
        }
        return t;
    }

    private int nextSeq(Board board) {
        return board.getShapes().stream().mapToInt(Shape::getSeq).max().orElse(0) + 1;
    }

    private Shape lastShape(Board board) {
        return board.getShapes().stream().max(Comparator.comparingInt(Shape::getSeq)).orElse(null);
    }

    private Shape newShape(Board board, String type, String color, String label, int seq, int x, int y) {
        Shape s = new Shape();
        s.setBoard(board);
        s.setSeq(seq);
        s.setType(type);
        s.setColor(color == null || color.isBlank() ? "gray" : color.toLowerCase());
        s.setLabel(label);
        s.setPosX(x);
        s.setPosY(y);
        return s;
    }
}
