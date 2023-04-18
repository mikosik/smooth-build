package org.smoothbuild.systemtest.stdlib.array;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.systemtest.SystemTestCase;

public class ElemTest extends SystemTestCase {
  @Test
  public void first_element() throws Exception {
    createUserModule("""
            result = elem(["first", "second", "third"], 0);
            """);
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactAsString("result"))
        .isEqualTo("first");
  }

  @Test
  public void last_element() throws Exception {
    createUserModule("""
            result = elem(["first", "second", "third"], 2);
            """);
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactAsString("result"))
        .isEqualTo("third");
  }

  @Test
  public void index_out_of_bounds_causes_exception() throws Exception {
    createUserModule("""
            result = elem(["first", "second", "third"], 3);
            """);
    runSmoothBuild("result");
    assertFinishedWithError();
    assertSysOutContains("Index (3) out of bounds. Array size = 3.");
  }

  @Test
  public void negative_index_causes_exception() throws Exception {
    createUserModule("""
            result = elem(["first", "second", "third"], -1);
            """);
    runSmoothBuild("result");
    assertFinishedWithError();
    assertSysOutContains("Index (-1) out of bounds. Array size = 3.");
  }
}
