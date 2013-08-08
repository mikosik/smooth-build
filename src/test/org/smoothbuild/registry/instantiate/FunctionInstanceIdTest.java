package org.smoothbuild.registry.instantiate;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.smoothbuild.lang.type.Path.path;

import org.junit.Test;
import org.smoothbuild.lang.type.Path;

public class FunctionInstanceIdTest {

  @Test
  public void resultDir() {
    Path path = path("abc");
    FunctionInstanceId id = new FunctionInstanceId(path);
    assertThat(id.resultDir()).isEqualTo(path);
  }
}
