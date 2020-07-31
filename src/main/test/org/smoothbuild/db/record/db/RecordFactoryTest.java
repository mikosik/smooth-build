package org.smoothbuild.db.record.db;

import static org.smoothbuild.cli.console.Level.ERROR;
import static org.smoothbuild.cli.console.Level.INFO;
import static org.smoothbuild.cli.console.Level.WARNING;

import org.junit.jupiter.api.Test;
import org.smoothbuild.db.record.base.Messages;
import org.smoothbuild.testing.TestingContext;

import com.google.common.truth.Truth;

import okio.ByteString;

public class RecordFactoryTest extends TestingContext {
  private final ByteString bytes = ByteString.encodeUtf8("aaa");

  @Test
  public void blob_data_can_be_read_back() throws Exception {
    Truth.assertThat(recordFactory().blob(sink -> sink.write(bytes)).source().readByteString())
        .isEqualTo(bytes);
  }

  @Test
  public void error_severity_is_error() {
    Truth.assertThat(Messages.severity(recordFactory().errorMessage("text")))
        .isEqualTo(ERROR.name());
  }

  @Test
  public void warning_severity_is_warning() {
    Truth.assertThat(Messages.severity(recordFactory().warningMessage("text")))
        .isEqualTo(WARNING.name());
  }

  @Test
  public void info_severity_is_info() {
    Truth.assertThat(Messages.severity(recordFactory().infoMessage("text")))
        .isEqualTo(INFO.name());
  }

  @Test
  public void text_returns_text() {
    Truth.assertThat(Messages.text(recordFactory().errorMessage("text")))
        .isEqualTo("text");
  }
}
