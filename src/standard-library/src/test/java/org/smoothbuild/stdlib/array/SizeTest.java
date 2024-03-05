package org.smoothbuild.stdlib.array;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.stdlib.StandardLibraryTestCase;

public class SizeTest extends StandardLibraryTestCase {
  @Test
  public void empty_array_has_size_0() throws Exception {
    createUserModule("""
        result = size([]);
        """);
    evaluate("result");
    assertThat(artifact()).isEqualTo(intB(0));
  }

  @Test
  public void one_element_array_has_size_1() throws Exception {
    createUserModule("""
        result = size([1]);
        """);
    evaluate("result");
    assertThat(artifact()).isEqualTo(intB(1));
  }

  @Test
  public void two_elements_array_has_size_2() throws Exception {
    createUserModule("""
        result = size([1, 2]);
        """);
    evaluate("result");
    assertThat(artifact()).isEqualTo(intB(2));
  }

  @Test
  public void three_elements_array_has_size_3() throws Exception {
    createUserModule("""
        result = size([1, 2, 3]);
        """);
    evaluate("result");
    assertThat(artifact()).isEqualTo(intB(3));
  }
}
