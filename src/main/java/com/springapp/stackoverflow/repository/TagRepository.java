package com.springapp.stackoverflow.repository;

import com.springapp.stackoverflow.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<Tag,Long> {
    Tag findByName(String name);
}
