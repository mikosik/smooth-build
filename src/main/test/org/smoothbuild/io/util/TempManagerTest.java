package org.smoothbuild.io.util;

import static org.hamcrest.Matchers.not;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import org.junit.Test;

public class TempManagerTest {
  private TempManager tempManager;

  @Test
  public void each_path_is_different() throws Exception {
    given(tempManager = new TempManager(null));
    when(tempManager.tempPath());
    thenReturned(not(tempManager.tempPath()));
  }
}
