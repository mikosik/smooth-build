package org.smoothbuild.db.object.db;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.cli.console.Level.ERROR;
import static org.smoothbuild.cli.console.Level.INFO;
import static org.smoothbuild.cli.console.Level.WARNING;

import org.junit.jupiter.api.Test;
import org.smoothbuild.exec.base.MessageStruct;
import org.smoothbuild.testing.TestingContextImpl;

import okio.ByteString;

public class ObjectFactoryTest extends TestingContextImpl {
  private final ByteString bytes = ByteString.encodeUtf8("aaa");

  @Test
  public void blob_data_can_be_read_back() throws Exception {
    assertThat(objectFactory().blob(sink -> sink.write(bytes)).source().readByteString())
        .isEqualTo(bytes);
  }

  @Test
  public void error_severity_is_error() {
    assertThat(MessageStruct.severity(objectFactory().errorMessage("text")))
        .isEqualTo(ERROR.name());
  }

  @Test
  public void warning_severity_is_warning() {
    assertThat(MessageStruct.severity(objectFactory().warningMessage("text")))
        .isEqualTo(WARNING.name());
  }

  @Test
  public void info_severity_is_info() {
    assertThat(MessageStruct.severity(objectFactory().infoMessage("text")))
        .isEqualTo(INFO.name());
  }

  @Test
  public void text_returns_text() {
    assertThat(MessageStruct.text(objectFactory().errorMessage("text")))
        .isEqualTo("text");
  }
}
