package org.smoothbuild.common.log;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.log.Level.ERROR;
import static org.smoothbuild.common.log.Log.error;
import static org.smoothbuild.common.log.Log.fatal;
import static org.smoothbuild.common.log.Log.info;
import static org.smoothbuild.common.log.Log.warning;

import com.google.common.testing.EqualsTester;
import org.junit.jupiter.api.Test;

public class LogTest {
  @Test
  void equality() {
    new EqualsTester()
        .addEqualityGroup(fatal("message"), fatal("message"))
        .addEqualityGroup(fatal("message2"), fatal("message2"))
        .addEqualityGroup(error("message"), error("message"))
        .addEqualityGroup(error("message2"), error("message2"))
        .addEqualityGroup(warning("message"), warning("message"))
        .addEqualityGroup(warning("message2"), warning("message2"))
        .addEqualityGroup(info("message"), info("message"))
        .addEqualityGroup(info("message2"), info("message2"))
        .testEquals();
  }

  @Test
  void to_string() {
    assertThat(new Log(ERROR, "my message").toString()).isEqualTo("Log{ERROR, 'my message'}");
  }
}
