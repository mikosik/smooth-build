package org.smoothbuild.registry.instantiate;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.Test;
import org.smoothbuild.lang.function.FunctionDefinition;
import org.smoothbuild.lang.function.Type;

public class FunctionExpressionTest {
  ExpressionId id = new ExpressionId("abc");
  FunctionDefinition definition = mock(FunctionDefinition.class);

  Expression functionExpression = new FunctionExpression(id, Type.STRING, definition);

  @Test
  public void id() {
    assertThat(functionExpression.id()).isEqualTo(id);
  }

  @Test
  public void type() throws Exception {
    Type actual = functionExpression.type();
    assertThat(actual).isEqualTo(Type.STRING);
  }
}
