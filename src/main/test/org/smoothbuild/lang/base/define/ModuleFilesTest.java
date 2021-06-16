package org.smoothbuild.lang.base.define;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.lang.base.define.Space.USER;

import java.nio.file.Path;
import java.util.Optional;

import org.junit.jupiter.api.Test;

public class ModuleFilesTest {
  @Test
  public void name() {
    ModuleFiles module = new ModuleFiles(
        USER, new FileLocation(USER, Path.of("path/myBuild.smooth")), Optional.empty());
    assertThat((Object) module.name())
        .isEqualTo("{prj}/path/myBuild");
  }
}
