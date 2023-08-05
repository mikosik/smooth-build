package org.smoothbuild.filesystem.install;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.filesystem.project.TempManager;

public class TempManagerTest {
  @Test
  public void each_path_is_different() {
    TempManager tempManager = new TempManager();
    assertThat(tempManager.tempPath())
        .isNotEqualTo(tempManager.tempPath());
  }
}
