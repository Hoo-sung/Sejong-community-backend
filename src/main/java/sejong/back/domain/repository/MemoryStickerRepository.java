package sejong.back.domain.repository;

import org.springframework.stereotype.Repository;
import sejong.back.domain.Sticker.Sticker;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

@Repository
public class MemoryStickerRepository implements StickerRepository{
    private static final Map<Long, Sticker> store = new ConcurrentHashMap<>();
    private static Long sequence = 0L;

    @Override
    public Sticker save(Sticker sticker) {
        sticker.setId(++sequence);
        store.put(sticker.getId(), sticker);
        return sticker;
    }

    @Override
    public Stream<Sticker> findByIdTreeId(Long treeId) {
        return  findAll().stream().filter(m -> m.getTreeId().equals(treeId));
    }

    @Override
    public List<Sticker> findAll() {
        return new ArrayList<>(store.values());
    }

}
