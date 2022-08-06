package hello.jdbc.service;

import hello.jdbc.member.Member;
import hello.jdbc.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * 트랜잭션 - DataSource, transactionManager 자동 등록
 */
@Slf4j
@SpringBootTest
class MemberServiceV4Test {

  public static final String MEMBER_A = "memberA";
  public static final String MEMBER_B = "memberB";
  public static final String MEMBER_EX = "ex";

  @Autowired
  private MemberRepository memberRepository;
  @Autowired
  private MemberServiceV4 memberService;

  @RequiredArgsConstructor
  @TestConfiguration
  static class TestConfig {

    private final DataSource dataSource;

    @Bean
    MemberRepository memberRepository() {
      return new MemberRepositoryV5(dataSource);
    }

    @Bean
    MemberServiceV4 memberService() {
      return new MemberServiceV4(memberRepository());
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
  void accountTransfer() {

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
  void accountTransferEx() {

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