package org.smoothbuild.slib.builtin.string;

import static com.google.common.truth.Truth.assertThat;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.smoothbuild.slib.AcceptanceTestCase;

public class ToBlobTest extends AcceptanceTestCase {
  @Test
  public void to_blob_function() throws IOException {
    givenFile("file.txt", "abc");
    givenScript(
        "  result = toBlob('abc');  ");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    assertThat(artifactContent("result"))
        .isEqualTo("abc");
  }
}
