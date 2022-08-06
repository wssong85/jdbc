package hello.jdbc.repository;

import hello.jdbc.member.Member;
import hello.jdbc.repository.ex.MyDbException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator;
import org.springframework.jdbc.support.SQLExceptionTranslator;

import javax.sql.DataSource;
import java.sql.*;
import java.util.NoSuchElementException;

/**
 * SQLExceptionTranslator 추가
 */
@Slf4j
public class MemberRepositoryV4_2 implements MemberRepository{

  private final DataSource dataSource;

  private final SQLExceptionTranslator exTranslatorr;


  public MemberRepositoryV4_2(DataSource dataSource) {
    this.dataSource = dataSource;
    this.exTranslatorr = new SQLErrorCodeSQLExceptionTranslator(dataSource);
  }

  public Member save(Member member) {

    String sql = "insert into member(member_id, money) values (?, ?)";

    Connection con = null;
    PreparedStatement pstmt = null;

    try {

      con = getConnection();
      pstmt = con.prepareStatement(sql);
      pstmt.setString(1, member.getMemberId());
      pstmt.setInt(2, member.getMoney());

      int saveCount = pstmt.executeUpdate();
      log.info("saveCount={}", saveCount);
      return member;
    } catch (SQLException e) {
      log.error("db error", e);

      throw exTranslatorr.translate("save", sql, e);
    } finally {

      close(con, pstmt, null);
    }
  }

  public Member findById(String memberId) {

    String sql = "select * From member where member_id = ?";

    Connection con = null;
    PreparedStatement pstmt = null;
    ResultSet rs = null;

    Member member = null;

    try {

      con = getConnection();
      pstmt = con.prepareStatement(sql);
      pstmt.setString(1, memberId);

      rs = pstmt.executeQuery();
      if (rs.next()) {
        member = new Member();
        member.setMemberId(rs.getString("member_id"));
        member.setMoney(rs.getInt("money"));
        return member;
      } else {
        throw new NoSuchElementException("member not found memberId=" + memberId);
      }

    } catch (SQLException e) {

      log.error("db error", e);
      throw exTranslatorr.translate("select", sql, e);
    } finally {

      close(con, pstmt, rs);
    }
  }

  public void update(String memberId, int money) {

    String sql = "update member set money=? where member_id=?";

    Connection con = null;
    PreparedStatement pstmt = null;
    ResultSet rs = null;

    try {

      con = getConnection();
      pstmt = con.prepareStatement(sql);
      pstmt.setInt(1, money);
      pstmt.setString(2, memberId);
      int updateResultSize = pstmt.executeUpdate();
      log.info("updateResultSize={}", updateResultSize);
    } catch (SQLException e) {

      log.error("db error", e);
      throw exTranslatorr.translate("update", sql, e);
    } finally {

      close(con, pstmt, rs);
    }
  }

  public void delete(String memberId) {
    String sql = "delete from member where member_id=?";

    Connection con = null;
    PreparedStatement pstmt = null;
    ResultSet rs = null;

    try {

      con = getConnection();
      pstmt = con.prepareStatement(sql);
      pstmt.setString(1, memberId);
      int deleteResultSize = pstmt.executeUpdate();
      log.info("deleteResultSize={}", deleteResultSize);
    } catch (SQLException e) {

      log.error("db error", e);
      throw exTranslatorr.translate("delete", sql, e);
    } finally {

      close(con, pstmt, rs);
    }
  }

  private void close(Connection con, Statement stmt, ResultSet rs) {

    JdbcUtils.closeResultSet(rs);
    JdbcUtils.closeStatement(stmt);
    // 주의! 트랜잭션 동기화를 사용하려면 DataSourceUtils를 사용해야 한다.
    DataSourceUtils.releaseConnection(con, dataSource);
  }

  private Connection getConnection() {

    // 주의! 트랜잭션 동기화를 사용하려면 DataSourceUtils를 사용해야 한다.
    Connection con = DataSourceUtils.getConnection(dataSource);
    log.info("get connection={}, class={}", con, con.getClass());
    return con;
  }
}
