package com.study.datajpa.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(of = {"id", "userName", "age"})
@NamedQuery (
        name ="Member.findByUsername",
        query = "select m from Member m where m.userName = :username" // nameQuery 실무에서 잘 안 쓴다...
)
public class Member {

    @Id @GeneratedValue
    @Column(name = "memeber_id")
    private Long id;

    private String userName;

    private int age;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

// JPA는 No Arguement 생성자가 필요 - protected로
//    protected Member() {
//    }

    public Member(String userName) {
        this.userName = userName;
    }

    public Member(String userName, int age) {
        this.userName = userName;
        this.age = age;
    }

    public Member(String userName, int age, Team team) {
        this.userName = userName;
        this.age = age;
        if(team != null) {
            changeTeam(team);
        }
    }

    public void changeTeam(Team team) {
        this.team = team;
        team.getMembers().add(this); //팀에 있는 멤버에 다가도 add해준다.
    }
}
