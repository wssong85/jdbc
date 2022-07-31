package hello.jdbc.connection;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.sql.Connection;

@Slf4j
class DBConnectionUtilTest {

  @Test
  void connection() {

    Connection connection = DBConnectionUtil.getConnection();
    Assertions.assertNotNull(connection);
  }
}