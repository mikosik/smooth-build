package org.smoothbuild.acceptance.slib.file;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;

public class FileTest extends AcceptanceTestCase {
  @Test
  public void file_constructor() throws Exception {
    givenScript("result = file(toBlob('abc'), 'name.txt');");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    assertThat(artifactDir("result"))
        .containsExactly("name.txt", "abc");
  }
}
