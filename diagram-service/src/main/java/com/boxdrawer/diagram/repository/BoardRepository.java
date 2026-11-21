package com.boxdrawer.diagram.repository;

import com.boxdrawer.diagram.entity.Board;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardRepository extends JpaRepository<Board, Long> {
}
