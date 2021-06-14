package org.smoothbuild.lang.base.define;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.util.Lists.list;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;

public class SModuleTest {
  @Test
  public void smooth_file() {
    SModule module = new SModule(Space.USER, Path.of("myModule.smooth"), list());
    assertThat((Object) module.smoothFile().path())
        .isEqualTo(Path.of("myModule.smooth"));
  }

  @Test
  public void native_file() {
    SModule module = new SModule(Space.USER, Path.of("myModule.smooth"), list());
    assertThat((Object) module.nativeFile().path())
        .isEqualTo(Path.of("myModule.jar"));
  }
}
