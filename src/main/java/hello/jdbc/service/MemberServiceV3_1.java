package hello.jdbc.service;

import hello.jdbc.member.Member;
import hello.jdbc.repository.MemberRepositoryV3;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;

/**
 * 트랜잭션 - 트랜잭션 매니저
 */
@Slf4j
@RequiredArgsConstructor
public class MemberServiceV3_1 {

  private final PlatformTransactionManager transactionManager;
  private final MemberRepositoryV3 memberRepository;

  public void accountTransfer(String fromId, String toId, int money) throws SQLException {

    // 트랜잭션 시작
    TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionDefinition());

    try {

      // 비지니스 로직
      bizLogic(fromId, toId, money);
      transactionManager.commit(status);

    } catch (Exception e) {

      // 실패시 롤백
      transactionManager.rollback(status);
      throw new IllegalStateException(e);
    }
  }

  private void bizLogic(String fromId, String toId, int money) throws SQLException {
    // 비지니스 로직
    Member fromMember = memberRepository.findById(fromId);
    Member toMember = memberRepository.findById(toId);

    memberRepository.update(fromId, fromMember.getMoney() - money);
    validation(toMember);
    memberRepository.update(toId, toMember.getMoney() + money);
  }

  private void release(Connection con) {
    if (con != null) {
      try {
        con.setAutoCommit(true); // setAutocommit true 인 상태로 유지
        con.close();
      } catch (Exception e) {
        log.info("error", e);
      }
    }
  }

  private void validation(Member toMember) {
    if (Objects.equals(toMember.getMemberId(), "ex")) {
      throw new IllegalStateException("이체중 예외 발생");
    }
  }
}
