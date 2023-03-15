package sejong.back.domain.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import sejong.back.domain.member.Member;
import sejong.back.domain.member.UpdateMemberForm;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class MemoryMemberRepository implements MemberRepository {

    private static final Map<Long,Member> store =new ConcurrentHashMap<>();//long은 Key 이다.

    private static Long sequence=0l;

    @Override
    public Member save(Member member) {

        member.setKey(++sequence);
        store.put(member.getKey(),member);
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


    public void clearStore(){//memory 삭제.
        store.clear();
    }

    @Override
    public List<Member> findAll() {

        return new ArrayList<>(store.values());
    }

    @Override
    public void update(Long key, Member updateMember) {

        Member findMember = findByKey(key);
        findMember.setMemberType(updateMember.getMemberType());
        findMember.setTags(updateMember.getTags());
    }
}
