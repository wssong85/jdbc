package hello.jdbc.repository;

import com.zaxxer.hikari.HikariDataSource;
import hello.jdbc.member.Member;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import java.sql.SQLException;
import java.util.NoSuchElementException;

import static hello.jdbc.connection.ConnectionConst.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Slf4j
class MemberRepositoryV1Test {

  MemberRepositoryV1 repository;

  @BeforeEach
  void beforeEach() {

    // 기본 DriverManager - 항상 새로운 커넥션을 획득
//    DriverManagerDataSource dataSource = new DriverManagerDataSource(URL, USERNAME, PASSWORD);
//    repository = new MemberRepositoryV1(dataSource);

    HikariDataSource hikariDataSource = new HikariDataSource();
    hikariDataSource.setJdbcUrl(URL);
    hikariDataSource.setUsername(USERNAME);
    hikariDataSource.setPassword(PASSWORD);
    hikariDataSource.setMaximumPoolSize(10);
    hikariDataSource.setPoolName("MyPool");
    repository = new MemberRepositoryV1(hikariDataSource);
  }

  @Test
  void crud() throws SQLException, InterruptedException {

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

    Thread.sleep(1000);
  }
}