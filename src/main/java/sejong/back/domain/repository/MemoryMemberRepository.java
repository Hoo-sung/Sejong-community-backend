package sejong.back.domain.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import sejong.back.domain.member.Member;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Repository
public class MemoryMemberRepository implements MemberRepository {

    private static final Map<Long, Member> store = new ConcurrentHashMap<>();//long은 Key 이다.

    private static Long sequence = 0l;

    @Override
    public Member save(Member member) {

        member.setKey(++sequence);
        store.put(member.getKey(), member);
        return member;
    }

    @Override
    public Member findByKey(Long key) {
        return store.get(key);
    }

    @Override
    public Member findByLoginId(String loginId) {//string으로 일치하는것 있나 찾기.

//        return findAll().stream()
//                .filter(m->m.getStudentId().equals(loginId))
//                .findFirst();
        for (Member value : store.values()) {
            if (value.getStudentId().equals(loginId)) {
                return value;
            }
        }
        return null;
    }


    public void clearStore() {//memory 삭제.
        store.clear();
    }

    @Override
    public List<Member> findAll() {

        return new ArrayList<>(store.values());
    }
}
