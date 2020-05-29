package org.smoothbuild.acceptance.slib.blob;

import static com.google.common.truth.Truth.assertThat;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;

public class ToStringTest extends AcceptanceTestCase {
  @Test
  public void to_string_function() throws IOException {
    givenScript(
        "  result = file(toBlob('abc'), 'file1.txt').content | toString;  ");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    assertThat(artifactFileContent("result"))
        .isEqualTo("abc");
  }
}
