package org.smoothbuild.registry.instantiate;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.Test;
import org.smoothbuild.lang.function.FunctionDefinition;

public class ExpressionTest {
  ExpressionId id = new ExpressionId("abc");
  FunctionDefinition definition = mock(FunctionDefinition.class);

  Expression expression = new Expression(id, definition);

  @Test
  public void id() {
    assertThat(expression.id()).isEqualTo(id);
  }

}
