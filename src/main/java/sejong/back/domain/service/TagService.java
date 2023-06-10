package sejong.back.domain.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import sejong.back.domain.repository.TreeRepository;
import sejong.back.domain.repository.memory.tagRepository.DbTagRepository;
import sejong.back.domain.tag.Tag;

import java.sql.SQLException;

@Slf4j
@RequiredArgsConstructor
@Service
public class TagService {

    private final DbTagRepository tagRepository;

    public Tag save(Tag tag){
        return tagRepository.save(tag);
    }

    public Tag findByTagId(int tag_Id){
        return tagRepository.findByTagId(tag_Id);
    }

    public Tag findByDescription(String description){
        return tagRepository.findByDescription(description);
    }

    public void update(int tag_id, String updateContext){
        tagRepository.update(tag_id, updateContext);
    }

    public void delete(int tag_id) {
        tagRepository.delete(tag_id);
    }

}
