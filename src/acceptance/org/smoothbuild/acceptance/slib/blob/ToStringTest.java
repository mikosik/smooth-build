package org.smoothbuild.acceptance.slib.blob;

import static com.google.common.truth.Truth.assertThat;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;

public class ToStringTest extends AcceptanceTestCase {
  @Test
  public void to_string_function() throws IOException {
    createUserModule(
        "  result = file(toBlob('abc'), 'file1.txt').content | toString;  ");
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactFileContentAsString("result"))
        .isEqualTo("abc");
  }
}
