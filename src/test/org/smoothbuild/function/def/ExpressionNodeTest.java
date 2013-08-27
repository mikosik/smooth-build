package org.smoothbuild.function.def;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.smoothbuild.function.base.Type;
import org.smoothbuild.function.expr.Expression;

public class ExpressionNodeTest {
  Expression expression = mock(Expression.class);

  @Test(expected = NullPointerException.class)
  public void nullExpressionIsForbidden() throws Exception {
    new ExpressionNode(null);
  }

  @Test
  public void type() throws Exception {
    when(expression.type()).thenReturn(Type.STRING);
    assertThat(new ExpressionNode(expression).type()).isEqualTo(Type.STRING);
  }

  @Test
  public void expressionFromConstructorIsReturned() {
    assertThat(new ExpressionNode(expression).expression(null)).isSameAs(expression);
  }
}
