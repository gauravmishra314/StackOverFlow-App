package com.springapp.stackoverflow.service;

import com.springapp.stackoverflow.model.Tag;

public interface TagService {
    Tag findOrCreateTag(String tagName);
}
