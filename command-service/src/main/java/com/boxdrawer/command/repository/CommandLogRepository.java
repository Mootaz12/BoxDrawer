package com.boxdrawer.command.repository;

import com.boxdrawer.command.entity.CommandLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommandLogRepository extends JpaRepository<CommandLog, Long> {
    List<CommandLog> findByBoardIdOrderByIdDesc(Long boardId);
}
