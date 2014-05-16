package org.smoothbuild.util;

import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import org.junit.Test;

public class ClassesTest {

  @Test
  public void binary_path_of_top_level_class() {
    when(Classes.binaryPath(String.class));
    thenReturned("java/lang/String.class");
  }

  @Test
  public void binary_path_of_inner_class() throws Exception {
    when(Classes.binaryPath(Character.Subset.class));
    thenReturned("java/lang/Character$Subset.class");
  }

  @Test
  public void binary_path_to_binary_name_for_top_level_class() throws Exception {
    when(Classes.binaryPathToBinaryName("java/lang/String.class"));
    thenReturned("java.lang.String");
  }

  @Test
  public void binary_path_to_binary_name_for_inner_class() throws Exception {
    when(Classes.binaryPathToBinaryName("java/lang/Character$Subset.class"));
    thenReturned("java.lang.Character$Subset");
  }

  @Test(expected = IllegalArgumentException.class)
  public void binary_path_to_binary_name_fails_for_non_class_file_path() throws Exception {
    Classes.binaryPathToBinaryName("java/lang/String.not-class");
  }
}
