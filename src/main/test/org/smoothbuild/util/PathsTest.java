package org.smoothbuild.util;

import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;

public class PathsTest {
  private Path path;

  @Test
  public void change_extension_to_different_one() throws Exception {
    given(path = newPath("abc/def.txt"));
    when(() -> Paths.changeExtension(path, "md"));
    thenReturned(newPath("abc/def.md"));
  }

  @Test
  public void change_extension_on_path_that_does_not_have_one() throws Exception {
    given(path = newPath("abc/def.txt"));
    when(() -> Paths.changeExtension(path, "md"));
    thenReturned(newPath("abc/def.md"));
  }

  private static Path newPath(String path) {
    return java.nio.file.Paths.get(path);
  }
}
