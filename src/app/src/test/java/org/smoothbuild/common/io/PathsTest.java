package org.smoothbuild.common.io;

import static com.google.common.truth.Truth.assertThat;

import java.nio.file.Path;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class PathsTest {
  @Nested
  class change_extension {
    @Test
    public void to_different_one() {
      assertThat((Object) Paths.changeExtension(newPath("abc/def.txt"), "md"))
          .isEqualTo(newPath("abc/def.md"));
    }

    @Test
    public void on_path_that_does_not_have_one() {
      assertThat((Object) Paths.changeExtension(newPath("abc/def"), "md"))
          .isEqualTo(newPath("abc/def.md"));
    }
  }

  @Nested
  class remove_extension {
    @Test
    public void from_path_with_extension() {
      assertThat((Object) Paths.removeExtension("abc/def.txt"))
          .isEqualTo("abc/def");
    }

    @Test
    public void from_path_without_extension() {
      assertThat((Object) Paths.removeExtension("abc/def"))
          .isEqualTo("abc/def");
    }
  }

  private static Path newPath(String path) {
    return Path.of(path);
  }
}
