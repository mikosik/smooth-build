package org.smoothbuild.systemtest.stdlib.array;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.systemtest.SystemTestCase;

public class SizeTest extends SystemTestCase {
  @Test
  public void empty_array_has_size_0() throws Exception {
    createUserModule("""
        result = size([]);
        """);
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactAsInt("result")).isEqualTo(0);
  }

  @Test
  public void one_element_array_has_size_1() throws Exception {
    createUserModule("""
        result = size([1]);
        """);
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactAsInt("result")).isEqualTo(1);
  }

  @Test
  public void two_elements_array_has_size_2() throws Exception {
    createUserModule("""
        result = size([1, 2]);
        """);
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactAsInt("result")).isEqualTo(2);
  }

  @Test
  public void three_elements_array_has_size_3() throws Exception {
    createUserModule("""
        result = size([1, 2, 3]);
        """);
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactAsInt("result")).isEqualTo(3);
  }
}
