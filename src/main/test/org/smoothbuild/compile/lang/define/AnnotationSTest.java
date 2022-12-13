package org.smoothbuild.compile.lang.define;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.testing.TestContext;

public class AnnotationSTest extends TestContext {
  @Test
  public void to_string() {
    var annS = new AnnotationS("myAnnotation", stringS(7, "myPath"), loc(17));
    assertThat(annS.toString())
        .isEqualTo("""
            AnnS(
              name = myAnnotation
              path = StringS(String, "myPath", myBuild.smooth:7)
              loc = myBuild.smooth:17
            )""");
  }
}
