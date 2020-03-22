package org.smoothbuild.io.util;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;

public class TempManagerTest {

  @Test
  public void each_path_is_different() {
    TempManager tempManager = new TempManager(null);
    assertThat(tempManager.tempPath())
        .isNotEqualTo(tempManager.tempPath());
  }
}
