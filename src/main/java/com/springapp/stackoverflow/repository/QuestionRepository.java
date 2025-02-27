package com.springapp.stackoverflow.repository;

import com.springapp.stackoverflow.model.Question;
import com.springapp.stackoverflow.model.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question,Long>, JpaSpecificationExecutor<Question> {
    @Query("SELECT t FROM Tag t JOIN t.questions q WHERE q.id = :questionId")
    List<Tag> findTagsByQuestionId(@Param("questionId") Long questionId);

    @Query("SELECT q FROM Question q WHERE SIZE(q.answers) = 0 ORDER BY q.createdAt DESC")
    Page<Question> findUnansweredQuestions(Pageable pageable);

}
