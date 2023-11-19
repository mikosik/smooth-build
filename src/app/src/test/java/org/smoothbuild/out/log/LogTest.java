package org.smoothbuild.out.log;

import static com.google.common.truth.Truth.assertThat;

import com.google.common.testing.EqualsTester;
import org.junit.jupiter.api.Test;

public class LogTest {
  @Test
  void equality() {
    new EqualsTester()
        .addEqualityGroup(Log.fatal("message"), Log.fatal("message"))
        .addEqualityGroup(Log.fatal("message2"), Log.fatal("message2"))
        .addEqualityGroup(Log.error("message"), Log.error("message"))
        .addEqualityGroup(Log.error("message2"), Log.error("message2"))
        .addEqualityGroup(Log.warning("message"), Log.warning("message"))
        .addEqualityGroup(Log.warning("message2"), Log.warning("message2"))
        .addEqualityGroup(Log.info("message"), Log.info("message"))
        .addEqualityGroup(Log.info("message2"), Log.info("message2"))
        .testEquals();
  }

  @Test
  void to_string() {
    assertThat(new Log(Level.ERROR, "my message").toString()).isEqualTo("Log{ERROR, 'my message'}");
  }
}
