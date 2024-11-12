package org.smoothbuild.stdlib.common;

import static com.google.common.truth.Truth.assertThat;

import okio.ByteString;
import org.junit.jupiter.api.Test;
import org.smoothbuild.stdlib.StandardLibraryTestContext;

public class IdTest extends StandardLibraryTestContext {
  @Test
  void returns_unchanged_string_value() throws Exception {
    var userModule = """
        result = id("abc");
        """;
    createUserModule(userModule);
    evaluate("result");
    assertThat(artifact()).isEqualTo(bString("abc"));
  }

  @Test
  void returns_unchanged_blob_value() throws Exception {
    var userModule = """
        result = id(0x010203);
        """;
    createUserModule(userModule);
    evaluate("result");
    assertThat(artifact()).isEqualTo(bBlob(ByteString.of((byte) 1, (byte) 2, (byte) 3)));
  }
}
