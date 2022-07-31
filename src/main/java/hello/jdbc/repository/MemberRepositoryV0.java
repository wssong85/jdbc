package hello.jdbc.repository;

import hello.jdbc.connection.DBConnectionUtil;
import hello.jdbc.member.Member;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;
import java.util.Objects;

/**
 * JDBC - DriverManager 사용
 */
@Slf4j
public class MemberRepositoryV0 {

  public Member save(Member member) throws SQLException {

    String sql = "insert into member(member_id, money) values (?, ?)";

    Connection con = null;
    PreparedStatement pstmt = null;

    try {

      con = getConnection();
      pstmt = con.prepareStatement(sql);
      pstmt.setString(1, member.getMemberId());
      pstmt.setInt(2, member.getMoney());
      return member;
    } catch (SQLException e) {

      log.error("db error", e);
      throw e;
    } finally {

      close(con, pstmt, null);
    }
  }

  private void close(Connection con, Statement stmt, ResultSet rs) {

    if (!Objects.isNull(stmt)) {

      try {
        stmt.close();
      } catch (SQLException e) {
        log.info("stme close error", e);
      }
    }

    if (!Objects.isNull(rs)) {

      try {
        rs.close();
      } catch (SQLException e) {
        log.info("rs close error: {}", e);
      }
    }

    if (!Objects.isNull(con)) {

      try {
        con.close();
      } catch (SQLException e) {
        log.info("con close error: {}", e);
      }
    }
  }

  private Connection getConnection() {
    return DBConnectionUtil.getConnection();
  }
}
