package org.smoothbuild.stdlib.string;

import static com.google.common.truth.Truth.assertThat;
import static okio.ByteString.encodeUtf8;

import org.junit.jupiter.api.Test;
import org.smoothbuild.stdlib.StandardLibraryTestCase;

public class ToBlobTest extends StandardLibraryTestCase {
  @Test
  void to_blob_func() throws Exception {
    var userModule = """
        result = toBlob("abc");
        """;
    createUserModule(userModule);
    evaluate("result");
    assertThat(artifact()).isEqualTo((bBlob(encodeUtf8("abc"))));
  }
}
