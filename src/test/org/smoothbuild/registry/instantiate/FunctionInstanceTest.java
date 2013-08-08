package org.smoothbuild.registry.instantiate;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.Test;
import org.smoothbuild.lang.function.FunctionDefinition;

public class FunctionInstanceTest {
  FunctionInstanceId id = new FunctionInstanceId("abc");
  FunctionDefinition definition = mock(FunctionDefinition.class);

  FunctionInstance functionInstance = new FunctionInstance(id, definition);

  @Test
  public void id() {
    assertThat(functionInstance.id()).isEqualTo(id);
  }

}
