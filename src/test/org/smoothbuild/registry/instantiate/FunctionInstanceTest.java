package org.smoothbuild.registry.instantiate;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.Test;
import org.smoothbuild.lang.function.Function;

public class FunctionInstanceTest {
  FunctionInstanceId id = new FunctionInstanceId("abc");
  Function function = mock(Function.class);

  FunctionInstance functionInstance = new FunctionInstance(id, function);

  @Test
  public void id() {
    assertThat(functionInstance.id()).isEqualTo(id);
  }

}
