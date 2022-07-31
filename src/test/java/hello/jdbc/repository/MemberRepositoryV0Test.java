package hello.jdbc.repository;

import hello.jdbc.member.Member;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.sql.SQLException;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class MemberRepositoryV0Test {

  MemberRepositoryV0 repository = new MemberRepositoryV0();

  @Test
  void crud() throws SQLException {

    // save
    Member member = new Member("memberV100", 40000);
    repository.save(member);

    // fineMember
    Member findMember = repository.findById(member.getMemberId());
    log.info("findMember={}", findMember);
    assertEquals(member, findMember);

    // update: money: 10000 -> 20000
    repository.update(member.getMemberId(), 20000);
    Member updatedMember = repository.findById(member.getMemberId());
    assertEquals(20000, updatedMember.getMoney());

    // delete
    repository.delete(member.getMemberId());
    assertThrows(NoSuchElementException.class, () -> repository.findById(member.getMemberId()));
  }
}