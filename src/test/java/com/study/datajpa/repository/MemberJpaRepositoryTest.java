package com.study.datajpa.repository;

import com.study.datajpa.entity.Member;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback(false) // rollback 안되고, db commit 처리 -> 학습할 때 사용
class MemberJpaRepositoryTest {

    @Autowired
    MemberJpaRepository memberJpaRepository;
    @PersistenceContext
    EntityManager entityManager;

    @Test
    public void testMember() {
        Member member = new Member("memberA");
        Member saveMember = memberJpaRepository.save(member);

        Member findMember = memberJpaRepository.find(saveMember.getId());

        assertThat(findMember.getId()).isEqualTo(member.getId());
        assertThat(findMember.getUserName()).isEqualTo(member.getUserName());
        assertThat(findMember).isEqualTo(member); // 영속성 컨텍스트에 의해 같은 인스턴스
    }

    @Test
    public void basicCRUD() {
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");

        memberJpaRepository.save(member1);
        memberJpaRepository.save(member2);

        //리스트 검증
        Member findMember1 = memberJpaRepository.findById(member1.getId()).get();
        Member findMember2 = memberJpaRepository.findById(member2.getId()).get();
        assertThat(findMember1).isEqualTo(2);

        //카운트 검증
        long count =memberJpaRepository.count();
        assertThat(findMember1).isEqualTo(2);

        //삭제 검증
        memberJpaRepository.delete(member1);
        memberJpaRepository.delete(member2);

        long deleteCount =memberJpaRepository.count();
        assertThat(findMember1).isEqualTo(0);

    }

    @Test
    public void bulkUpdate() {
        //given
        memberJpaRepository.save(new Member("member1",10));
        memberJpaRepository.save(new Member("member2",20));
        memberJpaRepository.save(new Member("member3",30));
        memberJpaRepository.save(new Member("member4",9));
        memberJpaRepository.save(new Member("member5",40));

        //when
        int resultCount = memberJpaRepository.bulkAgePlus(1);
        entityManager.clear(); // 영속성 컨텍스트 clear

        // bulk 연산에서 조심해야할 점(bulk 연산 시, 연산 값을 1차캐시에 저장하고, 이 값을 db에 값을 반영한다.
        // 영속성컨텍스트에는 그대로 값이 남아있으므로 값을 출력해보면 bulk연산 수행 전의 값이 나온다.
        // 따라서 db에 반영된 값을 조회하려면 clear연산으로 영속성컨텍스트의 값을 다 날리고, 조회하면 db에 있는 변경된 값이 조회된다.)
        List<Member> result = memberJpaRepository.findByUsername("member5");
        Member member = result.get(0);
        System.out.println("member: "+ member); // age가 아직 영속성 컨텍스트에 있는 값이 나오므로 40이 나온다.

        //then
        assertThat(resultCount).isEqualTo(4);


    }







}