package com.springapp.stackoverflow.service.serviceImpl;

import com.springapp.stackoverflow.model.Tag;
import com.springapp.stackoverflow.repository.TagRepository;
import com.springapp.stackoverflow.service.TagService;
import org.springframework.stereotype.Service;

@Service
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;

    public TagServiceImpl(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    @Override
    public Tag findOrCreateTag(String tagName){
        Tag tag = tagRepository.findByName(tagName.trim().toLowerCase());

        // If tag doesn't exist, create new one
        if (tag == null) {
            tag = new Tag();
            tag.setName(tagName.trim().toLowerCase());
            tag = tagRepository.save(tag);
        }

        return tag;
    }
}