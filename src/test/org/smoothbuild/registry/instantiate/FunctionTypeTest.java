package org.smoothbuild.registry.instantiate;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.smoothbuild.lang.type.Path.path;

import org.junit.Test;
import org.smoothbuild.lang.function.Function;
import org.smoothbuild.lang.type.Path;

public class FunctionTypeTest {
  Function function = mock(Function.class);

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
    when(instantiator.newInstance(resultDir)).thenReturn(function);

    assertThat(functionType.newInstance(resultDir)).isEqualTo(function);
  }
}
