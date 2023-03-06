package org.smoothbuild.systemtest.slib.array;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.util.collect.Lists.list;

import org.junit.jupiter.api.Test;
import org.smoothbuild.systemtest.SystemTestCase;

public class ConcatTest extends SystemTestCase {
  @Test
  public void concat_empty_array() throws Exception {
    createUserModule("""
            [Int] result = concat([[]]);
            """);
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactStringified("result"))
        .isEqualTo(list());
  }

  @Test
  public void concat_array_with_one_elem() throws Exception {
    createUserModule("""
            result = concat([["a", "b", "c"]]);
            """);
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactStringified("result"))
        .isEqualTo(list("a", "b", "c"));
  }

  @Test
  public void concat_array_with_two_elems() throws Exception {
    createUserModule("""
            result = concat([["a", "b", "c"], ["d", "e", "f"]]);
            """);
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactStringified("result"))
        .isEqualTo(list("a", "b", "c", "d", "e", "f"));
  }
}
