package org.smoothbuild.bytecode.db;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.out.log.Level.ERROR;
import static org.smoothbuild.out.log.Level.INFO;
import static org.smoothbuild.out.log.Level.WARNING;

import org.junit.jupiter.api.Test;
import org.smoothbuild.run.eval.MessageStruct;
import org.smoothbuild.testing.TestContext;

import okio.ByteString;

public class BytecodeFTest extends TestContext {
  private final ByteString bytes = ByteString.encodeUtf8("aaa");

  @Test
  public void blob_data_can_be_read_back() throws Exception {
    assertThat(bytecodeF().blob(sink -> sink.write(bytes)).source().readByteString())
        .isEqualTo(bytes);
  }

  @Test
  public void error_severity_is_error() {
    assertThat(MessageStruct.severity(bytecodeF().errorMessage("text")))
        .isEqualTo(ERROR.name());
  }

  @Test
  public void warning_severity_is_warning() {
    assertThat(MessageStruct.severity(bytecodeF().warningMessage("text")))
        .isEqualTo(WARNING.name());
  }

  @Test
  public void info_severity_is_info() {
    assertThat(MessageStruct.severity(bytecodeF().infoMessage("text")))
        .isEqualTo(INFO.name());
  }

  @Test
  public void text_returns_text() {
    assertThat(MessageStruct.text(bytecodeF().errorMessage("text")))
        .isEqualTo("text");
  }
}
