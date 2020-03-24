package org.smoothbuild.util.reflect;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.testing.common.AssertCall.assertCall;

import org.junit.jupiter.api.Test;

public class ClassesTest {
  @Test
  public void binary_path_of_top_level_class() {
    assertThat(Classes.binaryPath(String.class))
        .isEqualTo("java/lang/String.class");
  }

  @Test
  public void binary_path_of_inner_class() {
    assertThat(Classes.binaryPath(Character.Subset.class))
        .isEqualTo("java/lang/Character$Subset.class");
  }

  @Test
  public void binary_path_to_binary_name_for_top_level_class() {
    assertThat(Classes.binaryPathToBinaryName("java/lang/String.class"))
        .isEqualTo("java.lang.String");
  }

  @Test
  public void binary_path_to_binary_name_for_inner_class() {
    assertThat(Classes.binaryPathToBinaryName("java/lang/Character$Subset.class"))
        .isEqualTo("java.lang.Character$Subset");
  }

  @Test
  public void binary_path_to_binary_name_fails_for_non_class_file_path() {
    assertCall(() -> Classes.binaryPathToBinaryName("java/lang/String.not-class"))
        .throwsException(IllegalArgumentException.class);
  }
}
