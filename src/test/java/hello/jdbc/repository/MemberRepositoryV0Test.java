package hello.jdbc.repository;

import hello.jdbc.member.Member;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class MemberRepositoryV0Test {

  private Member member;

  @BeforeEach
  void setUp() {
    member = new Member("hi3", 30000);
  }

  @Test
  void save() throws SQLException {

    MemberRepositoryV0 v0 = new MemberRepositoryV0();
    v0.save(member);
  }
}