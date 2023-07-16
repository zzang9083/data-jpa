package com.study.datajpa.repository;

import com.study.datajpa.dto.MemberDto;
import com.study.datajpa.entity.Member;
import com.study.datajpa.entity.Team;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

import static org.assertj.core.api.Assertions.*;



@SpringBootTest
@Transactional
@Rollback(false)
class MemberRepositoryTest {
    @Autowired
    MemberRepository memberRepository;

    @Autowired
    TeamRepository teamRepository;

    @Test
    public void testMember() {
        Member member = new Member("memberA");
        Member savMember = memberRepository.save(member);

        Member findMember = memberRepository.findById(savMember.getId()).get();

        assertThat(findMember.getId()).isEqualTo(member.getId());
        assertThat(findMember.getUserName()).isEqualTo(member.getUserName());
        assertThat(findMember).isEqualTo(member); // 영속성 컨텍스트에 의해 같은 인스턴스
    }

    @Test
    public void basicCRUD() {
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");

        memberRepository.save(member1);
        memberRepository.save(member2);

        //리스트 검증
        Member findMember1 = memberRepository.findById(member1.getId()).get();
        Member findMember2 = memberRepository.findById(member2.getId()).get();
        assertThat(findMember1).isEqualTo(2);

        //카운트 검증
        long count =memberRepository.count();
        assertThat(findMember1).isEqualTo(2);

        //삭제 검증
        memberRepository.delete(member1);
        memberRepository.delete(member2);

        long deleteCount =memberRepository.count();
        assertThat(findMember1).isEqualTo(0);
    }

    @Test
    public void findMemberDto() {

        Team team = new Team("teamA");
        teamRepository.save(team);

        Member member1 = new Member("member1",10);
        member1.setTeam(team);
        memberRepository.save(member1);

        List<MemberDto> memberDto = memberRepository.findMemberDto();
        for(MemberDto dto : memberDto) {
            System.out.println("dto ="+dto);
        }
    }

    @Test
    public void findByNames() {
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");

        memberRepository.save(member1);
        memberRepository.save(member2);

        List<Member> result = memberRepository.findByNames(Arrays.asList("member1","member2"));
        for(Member m : result) {
            System.out.println("member = "+ m);
        }
    }

    @Test
    public void returnType() {
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");

        memberRepository.save(member1);
        memberRepository.save(member2);

        /*** 컬렉션은 값이 없으면 빈 컬렉션, 그냥 객체는 값이 없으면 null**
         *   List는 사이즈로 처리, 객체는 optional로 처리
         * */

        //주의 - result에 결과가 없다고 null이 아니다. 빈 컬렉션을 리턴한다. 주의할것
        List<Member> result = memberRepository.findListByUsername("member1");
        //result : 0
        // if(result == null) 이런 코드 x
        System.out.println(result.size());

        // 주의 - findMember1에 결과가 없으면 null이다.
        Member findMember1 = memberRepository.findMemberByUsername("member1");
        Optional<Member> optionalMember1 = memberRepository.findOptionalMemberByUsername("member1");

    }

    @Test
    public void page() throws Exception {
        //given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));
        //when
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC,
                "username"));
        Page<Member> page = memberRepository.findByAge(10, pageRequest);

        //이런 식으로 paging을 유지하면서 dto형태로 바꿀 수 있다.(api는 dto로 바꿔줘야하기 때문에 유용하다.)
        Page<MemberDto> tomap = page.map(member -> new MemberDto(member.getId(), member.getUserName(), null));

        //then
        List<Member> content = page.getContent(); //조회된 데이터
        assertThat(content.size()).isEqualTo(3); //조회된 데이터 수
        assertThat(page.getTotalElements()).isEqualTo(5); //전체 데이터 수
        assertThat(page.getNumber()).isEqualTo(0); //페이지 번호
        assertThat(page.getTotalPages()).isEqualTo(2); //전체 페이지 번호
        assertThat(page.isFirst()).isTrue(); //첫번째 항목인가?
        assertThat(page.hasNext()).isTrue(); //다음 페이지가 있는가?
    }
}