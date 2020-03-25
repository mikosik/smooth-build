package org.smoothbuild.util;

import static com.google.common.truth.Truth.assertThat;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;

public class PathsTest {
  @Test
  public void change_extension_to_different_one() {
    assertThat((Object) Paths.changeExtension(newPath("abc/def.txt"), "md"))
        .isEqualTo(newPath("abc/def.md"));
  }

  @Test
  public void change_extension_on_path_that_does_not_have_one() {
    assertThat((Object) Paths.changeExtension(newPath("abc/def"), "md"))
        .isEqualTo(newPath("abc/def.md"));
  }

  private static Path newPath(String path) {
    return java.nio.file.Paths.get(path);
  }
}
