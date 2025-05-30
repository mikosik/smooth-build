package org.smoothbuild.virtualmachine.bytecode;

import static com.google.common.truth.Truth.assertThat;
import static okio.Okio.buffer;
import static org.smoothbuild.common.log.base.Level.ERROR;
import static org.smoothbuild.common.log.base.Level.FATAL;
import static org.smoothbuild.common.log.base.Level.INFO;
import static org.smoothbuild.common.log.base.Level.WARNING;

import okio.ByteString;
import org.junit.jupiter.api.Test;
import org.smoothbuild.virtualmachine.bytecode.helper.StoredLogStruct;
import org.smoothbuild.virtualmachine.dagger.VmTestContext;

public class BytecodeFactoryTest extends VmTestContext {
  private final ByteString bytes = ByteString.encodeUtf8("aaa");

  @Test
  void blob_data_can_be_read_back() throws Exception {
    try (var source = buffer(bBlob(bytes).source())) {
      assertThat(source.readByteString()).isEqualTo(bytes);
    }
  }

  @Test
  void fatal_severity_is_fatal() throws Exception {
    assertThat(StoredLogStruct.levelAsString(bFatalLog("text"))).isEqualTo(FATAL.name());
  }

  @Test
  void error_severity_is_error() throws Exception {
    assertThat(StoredLogStruct.levelAsString(bErrorLog("text"))).isEqualTo(ERROR.name());
  }

  @Test
  void warning_severity_is_warning() throws Exception {
    assertThat(StoredLogStruct.levelAsString(bWarningLog("text"))).isEqualTo(WARNING.name());
  }

  @Test
  void info_severity_is_info() throws Exception {
    assertThat(StoredLogStruct.levelAsString(bInfoLog("text"))).isEqualTo(INFO.name());
  }

  @Test
  void text_returns_text() throws Exception {
    assertThat(StoredLogStruct.message(bErrorLog("text"))).isEqualTo("text");
  }
}
