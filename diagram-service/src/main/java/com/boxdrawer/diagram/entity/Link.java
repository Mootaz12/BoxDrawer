package com.boxdrawer.diagram.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "links")
public class Link {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "board_id")
    private Board board;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "from_shape_id")
    private Shape fromShape;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "to_shape_id")
    private Shape toShape;

    private String label;

    public Long getId() { return id; }
    public Board getBoard() { return board; }
    public void setBoard(Board board) { this.board = board; }
    public Shape getFromShape() { return fromShape; }
    public void setFromShape(Shape fromShape) { this.fromShape = fromShape; }
    public Shape getToShape() { return toShape; }
    public void setToShape(Shape toShape) { this.toShape = toShape; }
    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }
}
