package com.study.datajpa.repository;

import java.util.*;

import com.study.datajpa.dto.MemberDto;
import com.study.datajpa.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MemberRepository extends JpaRepository<Member, Long> {

    /*******************************************JPA NamedQuery*****************************************************/
    //함수형으로 표현하면 알아서 만들어준다.(짤막한 쿼리들 조건 1-2개는 그냥 이렇게 한다.)
    List<Member> findByUsernameAndAgeGreaterThan(String username, int age); // 메소드 이름으로 쿼리 생성

    @Query(name = "Member.findByUsername") // name query를 불러와서 편리하게 쓸 수 있다.
    List<Member> findByUsername(@Param("username") String username);
    /**************************************************************************************************************/


    /*******************************************@Query***************************************************************/
    // @query를 이용하여 직접 쿼리를 작성할 수도 있다. - 현업에서 많이 쓰임.
    @Query("select m from Member m where m.userName = :username and m.age = :age")
    List<Member> findUser(@Param("username") String username, @Param("age") int age);

    //@query를 이용하여 하나의 필드값만 가지고 올 수 있다.
    @Query("select m.userName from Member m") //@Query - 값, DTO 조회하기 : 걍 m.필드명 해서 return value만 맞춰주면된다.
    List<String> findUsernameList();
    /**************************************************************************************************************/


    /*******************************************@Query 값, dto 조회***************************************************/
    //@query를 이용하여 하나의 필드값만 가지고 올 수 있다. : 반드시 생성자와 매치되도록 select 절을 new로 작성
    @Query("select new com.study.datajpa.dto.MemberDto(m.id, m.userName, t.name) from Member m join m.team t")
    List<MemberDto> findMemberDto();

    /*******************************************@Query 값, List 바인딩***************************************************/
    //'in :field' 절을 사용하여 List형식의 파라미터를 바인딩 할 수도 있다.(실무에서 많이쓰임)
    @Query("select m from Member  m where m.userName in :names")
    List<Member> findByNames(@Param("names") List<String> names);
    /**************************************************************************************************************/

    /************************************************반환타임********************************************************/
    //spring data-jpa는 다양한 형태로 반환타입을 설정할 수 있다.
    List<Member> findListByUsername(String username); // 컬렉션
    Member findMemberByUsername(String username); // 단건
    Optional<Member> findOptionalMemberByUsername(String username); // 단건 Optional
    /**************************************************************************************************************/

    /************************************************페이징*********************************************************/
    // 주의 : page는 1이 아닌, 0부터 시작
    public Page<Member> findByAge(int age, Pageable pageable); // paging - 요청나간 page대로 페이징 쿼리

    public Slice<Member> findByAgeSlice(int age, Pageable pageable); // slice - 요청나간거보다 하나 더 페이징 쿼리(다음 페이지가
                                                                     //         있나 없나 - 더보기 기능)

    // counting 쿼리를 분리하는 이유 : page 조인을 할 경우 select와 카운트쿼리가 두 개가 나가는데, where조건이 없는 순수 left,
    //                             outer 조인같은 경우는 사실 조인을 할 필요가 없다.(갯수가 어짜피 똑같)
    //  countQuery를 별도로 작성하면 카운트 쿼리는 작성한대로 나간다! -> 전체 원장에 대한 카운트라면 고려해볼 것~~
    @Query(value = "select m from Member m left join m.team t",
            countQuery = "select count(m) from Member m")
    public Page<Member> findByAgeAddCount(int age, Pageable pageable); // paging - 요청나간 page대로 페이징 쿼리
    /**************************************************************************************************************/



}
