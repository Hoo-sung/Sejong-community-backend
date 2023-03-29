package sejong.back.domain.repository;

import sejong.back.domain.Sticker.Sticker;

import java.util.List;
import java.util.stream.Stream;

public interface StickerRepository {
    Sticker save(Sticker sticker);

    Stream<Sticker> findByIdTreeId(Long treeId);

    List<Sticker> findAll();
}
