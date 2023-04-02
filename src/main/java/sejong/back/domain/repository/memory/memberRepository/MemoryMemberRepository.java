package sejong.back.domain.repository.memory.memberRepository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import sejong.back.domain.member.Member;
import sejong.back.domain.member.UpdateMemberForm;
import sejong.back.domain.repository.MemberRepository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Repository
public class MemoryMemberRepository implements MemberRepository {

    private static final Map<Long,Member> storeMember =new ConcurrentHashMap<>();//long은 Key 이다.

    private static Long memberSequence=0l;

    public MemoryMemberRepository() {

    }

    @Override
    public Member save(Member member) {

        member.setKey(++memberSequence);
        storeMember.put(member.getKey(),member);
        return member;
    }

    @Override
    public Member findByKey(Long key) {
        return storeMember.get(key);
    }

    @Override
    public Member findByLoginId(Long loginId) {//string으로 일치하는것 있나 찾기.

////        return findAll().stream()
////                .filter(m->m.getStudentId().equals(loginId))
////                .findFirst();
        for (Member value : storeMember.values()) {
            if (value.getStudentId().equals(loginId)) {
                return value;
            }
        }
        return null;
    }


    public void clearStore(){//memory 삭제.
        storeMember.clear();
    }

    @Override
    public List<Member> findAll() {

        return new ArrayList<>(storeMember.values());
    }

    @Override
    public void update(Long key, UpdateMemberForm form) {//공개 설정 정보를 바꾸도록 해야한다.

        Member findMember = storeMember.get(key);
        findMember.setNickName(form.getNickName());
//        findMember.setMemberType(updateMember.getMemberType());
//        findMember.setTags(updateMember.getTags());
    }
}
