package hello.jdbc.service;

import hello.jdbc.member.Member;
import hello.jdbc.repository.MemberRepositoryV3;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;

/**
 * 트랜잭션 - 트랜잭션 템플릿
 */
@Slf4j
public class MemberServiceV3_2 {

  //  private final PlatformTransactionManager transactionManager;
  private final TransactionTemplate txTemplate;
  private final MemberRepositoryV3 memberRepository;

  public MemberServiceV3_2(PlatformTransactionManager transactionManager, MemberRepositoryV3 memberRepository) {
    this.txTemplate = new TransactionTemplate(transactionManager);
    this.memberRepository = memberRepository;
  }

  public void accountTransfer(String fromId, String toId, int money) throws SQLException {

    txTemplate.executeWithoutResult((status) -> {
      try {
        bizLogic(fromId, toId, money);
      } catch (SQLException e) {
        throw new IllegalStateException(e);
      }
    });
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
