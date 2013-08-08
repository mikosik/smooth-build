package org.smoothbuild.registry.instantiate;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.smoothbuild.lang.type.Path.path;

import org.junit.Test;
import org.smoothbuild.lang.function.FunctionDefinition;
import org.smoothbuild.lang.type.Path;

public class FunctionTypeTest {
  FunctionDefinition definition = mock(FunctionDefinition.class);

  String name = "functionName";
  Instantiator instantiator = mock(Instantiator.class);
  FunctionType functionType = new FunctionType(name, instantiator);

  @Test
  public void name() {
    assertThat(functionType.name()).isEqualTo(name);
  }

  @Test
  public void newInstance() throws Exception {
    Path resultDir = path("abc");
    when(instantiator.newInstance(resultDir)).thenReturn(definition);

    assertThat(functionType.newInstance(resultDir)).isEqualTo(definition);
  }
}
