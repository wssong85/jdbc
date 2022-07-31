package hello.jdbc.repository;

import hello.jdbc.connection.DBConnectionUtil;
import hello.jdbc.member.Member;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;
import java.util.NoSuchElementException;
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

      int saveCount = pstmt.executeUpdate();
      log.info("saveCount={}", saveCount);
      return member;
    } catch (SQLException e) {

      log.error("db error", e);
      throw e;
    } finally {

      close(con, pstmt, null);
    }
  }

  public Member findById(String memberId) throws SQLException {

    String sql = "select * From member where member_id = ?";

    Connection con = null;
    PreparedStatement pstmt = null;
    ResultSet rs = null;

    try {

      con = getConnection();
      pstmt = con.prepareStatement(sql);
      pstmt.setString(1, memberId);

      rs = pstmt.executeQuery();
      if (rs.next()) {
        Member member = new Member();
        member.setMemberId(rs.getString("member_id"));
        member.setMoney(rs.getInt("money"));
        return member;
      } else {
        throw new NoSuchElementException("member not found memberId=" + memberId);
      }

    } catch (SQLException e) {

      log.error("db error", e);
      throw e;
    } finally {

      close(con, pstmt, rs);
    }
  }

  public void update(String memberId, int money) throws SQLException {

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
      throw e;
    } finally {

      close(con, pstmt, rs);
    }
  }

  public void delete(String memberId) throws SQLException {
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
      throw e;
    } finally {

      close(con, pstmt, rs);
    }
  }

  private void close(Connection con, Statement stmt, ResultSet rs) {

    if (!Objects.isNull(rs)) {

      try {
        rs.close();
      } catch (SQLException e) {
        log.info("rs close error", e);
      }
    }

    if (!Objects.isNull(stmt)) {

      try {
        stmt.close();
      } catch (SQLException e) {
        log.info("stme close error", e);
      }
    }

    if (!Objects.isNull(con)) {

      try {
        con.close();
      } catch (SQLException e) {
        log.info("con close error", e);
      }
    }
  }

  private Connection getConnection() {
    return DBConnectionUtil.getConnection();
  }
}
