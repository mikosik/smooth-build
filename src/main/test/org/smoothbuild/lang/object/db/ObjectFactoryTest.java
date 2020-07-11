package org.smoothbuild.lang.object.db;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.lang.object.base.Messages.ERROR;
import static org.smoothbuild.lang.object.base.Messages.INFO;
import static org.smoothbuild.lang.object.base.Messages.WARNING;
import static org.smoothbuild.lang.object.base.Messages.severity;
import static org.smoothbuild.lang.object.base.Messages.text;

import org.junit.jupiter.api.Test;
import org.smoothbuild.testing.TestingContext;

import okio.ByteString;

public class ObjectFactoryTest extends TestingContext {
  private final ByteString bytes = ByteString.encodeUtf8("aaa");

  @Test
  public void blob_data_can_be_read_back() throws Exception {
    assertThat(objectFactory().blob(sink -> sink.write(bytes)).source().readByteString())
        .isEqualTo(bytes);
  }

  @Test
  public void error_severity_is_error() {
    assertThat(severity(objectFactory().errorMessage("text")))
        .isEqualTo(ERROR);
  }

  @Test
  public void warning_severity_is_warning() {
    assertThat(severity(objectFactory().warningMessage("text")))
        .isEqualTo(WARNING);
  }

  @Test
  public void info_severity_is_info() {
    assertThat(severity(objectFactory().infoMessage("text")))
        .isEqualTo(INFO);
  }

  @Test
  public void text_returns_text() {
    assertThat(text(objectFactory().errorMessage("text")))
        .isEqualTo("text");
  }
}
