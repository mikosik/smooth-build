package org.smoothbuild.common.base;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.base.Throwables.messageFrom;

import java.io.IOException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class ThrowablesTest {
  @Nested
  class _messageFor {
    @Test
    void returns_message_when_present() {
      assertThat(messageFrom(new IOException("my message"))).isEqualTo("my message");
    }

    @Test
    void returns_information_about_missing_message_when_message_not_present() {
      assertThat(messageFrom(new IOException())).isEqualTo("java.io.IOException without a message");
    }
  }
}
