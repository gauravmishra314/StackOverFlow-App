package com.springapp.stackoverflow.repository;

import com.springapp.stackoverflow.model.Answer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnswerRepository extends JpaRepository<Answer , Long> {
}
