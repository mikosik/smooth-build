package org.smoothbuild.db.object.db;

import static org.smoothbuild.cli.console.Level.ERROR;
import static org.smoothbuild.cli.console.Level.INFO;
import static org.smoothbuild.cli.console.Level.WARNING;

import org.junit.jupiter.api.Test;
import org.smoothbuild.exec.base.MessageTuple;
import org.smoothbuild.testing.TestingContext;

import com.google.common.truth.Truth;

import okio.ByteString;

public class ObjectFactoryTest extends TestingContext {
  private final ByteString bytes = ByteString.encodeUtf8("aaa");

  @Test
  public void blob_data_can_be_read_back() throws Exception {
    Truth.assertThat(objectFactory().blob(sink -> sink.write(bytes)).source().readByteString())
        .isEqualTo(bytes);
  }

  @Test
  public void error_severity_is_error() {
    Truth.assertThat(MessageTuple.severity(objectFactory().errorMessage("text")))
        .isEqualTo(ERROR.name());
  }

  @Test
  public void warning_severity_is_warning() {
    Truth.assertThat(MessageTuple.severity(objectFactory().warningMessage("text")))
        .isEqualTo(WARNING.name());
  }

  @Test
  public void info_severity_is_info() {
    Truth.assertThat(MessageTuple.severity(objectFactory().infoMessage("text")))
        .isEqualTo(INFO.name());
  }

  @Test
  public void text_returns_text() {
    Truth.assertThat(MessageTuple.text(objectFactory().errorMessage("text")))
        .isEqualTo("text");
  }
}
