package hello.jdbc.service;

import hello.jdbc.member.Member;
import hello.jdbc.repository.MemberRepositoryV3;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.sql.SQLException;

import static hello.jdbc.connection.ConnectionConst.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * 트랜잭션 - DataSource, transactionManager 자동 등록
 */
@Slf4j
@SpringBootTest
class MemberServiceV3_3Test {

  public static final String MEMBER_A = "memberA";
  public static final String MEMBER_B = "memberB";
  public static final String MEMBER_EX = "ex";

  @Autowired
  private MemberRepositoryV3 memberRepository;
  @Autowired
  private MemberServiceV3_3 memberService;

  @RequiredArgsConstructor
  @TestConfiguration
  static class TestConfig {

    private final DataSource dataSource;

    @Bean
    MemberRepositoryV3 memberRepositoryV3() {
      return new MemberRepositoryV3(dataSource);
    }

    @Bean
    MemberServiceV3_3 memberServiceV3_3() {
      return new MemberServiceV3_3(memberRepositoryV3());
    }
  }

  @Test
  void AopCheck() {
    log.info("memberService class={}", memberService.getClass());
    log.info("memberRepository class={}", memberRepository.getClass());

    Assertions.assertTrue(AopUtils.isAopProxy(memberService));
    Assertions.assertFalse(AopUtils.isAopProxy(memberRepository));
  }

  @Test
  @DisplayName("정상 이체")
  void accountTransfer() throws SQLException {

    //given
    Member memberA = new Member(MEMBER_A, 10000);
    Member memberB = new Member(MEMBER_B, 10000);
    memberRepository.save(memberA);
    memberRepository.save(memberB);

    //when
    memberService.accountTransfer(memberA.getMemberId(), memberB.getMemberId(), 2000);

    //then
    Member findMemberA = memberRepository.findById(memberA.getMemberId());
    Member findMemberB = memberRepository.findById(memberB.getMemberId());

    assertEquals(8000, findMemberA.getMoney());
    assertEquals(12000, findMemberB.getMoney());
  }

  @Test
  @DisplayName("이체중 예외 발생")
  void accountTransferEx() throws SQLException {

    //given
    Member memberA = new Member(MEMBER_A, 10000);
    Member memberB = new Member(MEMBER_EX, 10000);
    memberRepository.save(memberA);
    memberRepository.save(memberB);

    //when
    assertThrows(IllegalStateException.class,
        () -> memberService.accountTransfer(memberA.getMemberId(), memberB.getMemberId(), 2000));

    //then
    Member findMemberA = memberRepository.findById(memberA.getMemberId());
    Member findMemberB = memberRepository.findById(memberB.getMemberId());

    assertEquals(10000, findMemberA.getMoney());
    assertEquals(10000, findMemberB.getMoney());
  }

  @AfterEach
  void after() throws SQLException {
    memberRepository.delete(MEMBER_A);
    memberRepository.delete(MEMBER_B);
    memberRepository.delete(MEMBER_EX);
  }
}