package hello.jdbc.service;

import hello.jdbc.member.Member;
import hello.jdbc.repository.MemberRepository;
import hello.jdbc.repository.MemberRepositoryV3;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.Objects;

/**
 * 예외 누수 문제 해결
 * SQLException 제거
 *
 * MemberRepository 인터페이스 의존
 */
@RequiredArgsConstructor
@Slf4j
public class MemberServiceV4 {

  private final MemberRepository memberRepository;

  @Transactional
  public void accountTransfer(String fromId, String toId, int money) {
    bizLogic(fromId, toId, money);
  }

  private void bizLogic(String fromId, String toId, int money) {
    // 비지니스 로직
    Member fromMember = memberRepository.findById(fromId);
    Member toMember = memberRepository.findById(toId);

    memberRepository.update(fromId, fromMember.getMoney() - money);
    validation(toMember);
    memberRepository.update(toId, toMember.getMoney() + money);
  }

  private void validation(Member toMember) {
    if (Objects.equals(toMember.getMemberId(), "ex")) {
      throw new IllegalStateException("이체중 예외 발생");
    }
  }
}
