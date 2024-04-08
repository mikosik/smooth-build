package org.smoothbuild.common.log.base;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.base.Strings.convertOsLineSeparatorsToNewLine;
import static org.smoothbuild.common.log.base.Level.ERROR;
import static org.smoothbuild.common.log.base.Log.error;
import static org.smoothbuild.common.log.base.Log.fatal;
import static org.smoothbuild.common.log.base.Log.info;
import static org.smoothbuild.common.log.base.Log.warning;

import com.google.common.testing.EqualsTester;
import org.junit.jupiter.api.Test;

public class LogTest {
  @Test
  void fatal_message_from_stack_trace() {
    var throwable = new RuntimeException();
    StackTraceElement[] stackTrace =
        new StackTraceElement[] {new StackTraceElement("MyClass", "myMethod", "MyClass.java", 10)};
    throwable.setStackTrace(stackTrace);
    var fatal = fatal(throwable);
    assertThat(convertOsLineSeparatorsToNewLine(fatal.message()))
        .isEqualTo(
            """
            java.lang.RuntimeException
            \tat MyClass.myMethod(MyClass.java:10)\
            """);
  }

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
