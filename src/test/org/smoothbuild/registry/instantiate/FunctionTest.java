package org.smoothbuild.registry.instantiate;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.Test;
import org.smoothbuild.lang.function.FullyQualifiedName;
import org.smoothbuild.lang.function.FunctionDefinition;
import org.smoothbuild.lang.function.Type;

public class FunctionTest {
  FunctionDefinition definition = mock(FunctionDefinition.class);

  FullyQualifiedName name = FullyQualifiedName.fullyQualifiedName("my.package.myFunction");
  Instantiator instantiator = mock(Instantiator.class);
  Function function = new Function(name, Type.STRING, instantiator);

  @Test
  public void name() {
    assertThat(function.name()).isEqualTo(name);
  }

  @Test
  public void type() {
    Type actual = function.type();
    assertThat(actual).isEqualTo(Type.STRING);
  }

}
