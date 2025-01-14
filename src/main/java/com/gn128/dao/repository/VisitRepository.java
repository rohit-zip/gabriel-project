package com.gn128.dao.repository;


import com.gn128.entity.Visit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VisitRepository extends JpaRepository<Visit, String> {

    List<Visit> findAllByUserId(String userId);
}
