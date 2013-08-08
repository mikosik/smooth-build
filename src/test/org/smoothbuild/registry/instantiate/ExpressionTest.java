package org.smoothbuild.registry.instantiate;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.Test;
import org.smoothbuild.lang.function.FunctionDefinition;
import org.smoothbuild.lang.function.Type;

public class ExpressionTest {
  ExpressionId id = new ExpressionId("abc");
  FunctionDefinition definition = mock(FunctionDefinition.class);

  Expression expression = new Expression(id, Type.STRING, definition);

  @Test
  public void id() {
    assertThat(expression.id()).isEqualTo(id);
  }

  @Test
  public void type() throws Exception {
    @SuppressWarnings("unchecked")
    Type<String> actual = (Type<String>) expression.type();
    assertThat(actual).isEqualTo(Type.STRING);
  }
}
