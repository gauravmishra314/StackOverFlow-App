package com.springapp.stackoverflow.repository;

import com.springapp.stackoverflow.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionRepository extends JpaRepository<Question,Long> {
}
