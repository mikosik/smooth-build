package org.smoothbuild.lang.value;

import static org.smoothbuild.lang.message.Messages.ERROR;
import static org.smoothbuild.lang.message.Messages.INFO;
import static org.smoothbuild.lang.message.Messages.WARNING;
import static org.smoothbuild.lang.message.Messages.severity;
import static org.smoothbuild.lang.message.Messages.text;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import org.junit.Test;
import org.smoothbuild.testing.TestingContext;

import okio.ByteString;

public class ValueFactoryTest extends TestingContext {
  private final ByteString bytes = ByteString.encodeUtf8("aaa");
  private Struct message;

  @Test
  public void blob_data_can_be_read_back() throws Exception {
    when(valueFactory().blob(sink -> sink.write(bytes)).source().readByteString());
    thenReturned(bytes);
  }

  @Test
  public void error_severity_is_error() {
    given(message = valueFactory().errorMessage("text"));
    when(() -> severity(message));
    thenReturned(ERROR);
  }

  @Test
  public void warning_severity_is_warning() {
    given(message = valueFactory().warningMessage("text"));
    when(() -> severity(message));
    thenReturned(WARNING);
  }

  @Test
  public void info_severity_is_info() {
    given(message = valueFactory().infoMessage("text"));
    when(() -> severity(message));
    thenReturned(INFO);
  }

  @Test
  public void text_returns_text() {
    given(message = valueFactory().errorMessage("text"));
    when(() -> text(message));
    thenReturned("text");
  }
}
