package org.smoothbuild.virtualmachine.bytecode;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.log.Level.ERROR;
import static org.smoothbuild.common.log.Level.FATAL;
import static org.smoothbuild.common.log.Level.INFO;
import static org.smoothbuild.common.log.Level.WARNING;

import okio.ByteString;
import org.junit.jupiter.api.Test;
import org.smoothbuild.virtualmachine.bytecode.helper.StoredLogStruct;
import org.smoothbuild.virtualmachine.testing.TestingVirtualMachine;

public class BytecodeFTest extends TestingVirtualMachine {
  private final ByteString bytes = ByteString.encodeUtf8("aaa");

  @Test
  public void blob_data_can_be_read_back() throws Exception {
    try (var source = bytecodeF().blob(sink -> sink.write(bytes)).source()) {
      assertThat(source.readByteString()).isEqualTo(bytes);
    }
  }

  @Test
  public void fatal_severity_is_fatal() throws Exception {
    assertThat(StoredLogStruct.levelAsString(bytecodeF().fatalLog("text"))).isEqualTo(FATAL.name());
  }

  @Test
  public void error_severity_is_error() throws Exception {
    assertThat(StoredLogStruct.levelAsString(bytecodeF().errorLog("text"))).isEqualTo(ERROR.name());
  }

  @Test
  public void warning_severity_is_warning() throws Exception {
    assertThat(StoredLogStruct.levelAsString(bytecodeF().warningLog("text")))
        .isEqualTo(WARNING.name());
  }

  @Test
  public void info_severity_is_info() throws Exception {
    assertThat(StoredLogStruct.levelAsString(bytecodeF().infoLog("text"))).isEqualTo(INFO.name());
  }

  @Test
  public void text_returns_text() throws Exception {
    assertThat(StoredLogStruct.message(bytecodeF().errorLog("text"))).isEqualTo("text");
  }
}
