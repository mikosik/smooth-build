package org.smoothbuild.function.def;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.smoothbuild.function.base.Type;
import org.smoothbuild.function.expr.Expression;
import org.smoothbuild.function.expr.ExpressionId;
import org.smoothbuild.function.expr.ExpressionIdFactory;

public class StringNodeTest {

  @Test(expected = NullPointerException.class)
  public void nullExpressionIsForbidden() throws Exception {
    new StringNode(null);
  }

  @Test
  public void type() throws Exception {
    assertThat(new StringNode("abc").type()).isEqualTo(Type.STRING);
  }

  @Test
  public void expressionFromConstructorIsReturned() {
    String string = "abc";
    ExpressionIdFactory idFactory = mock(ExpressionIdFactory.class);
    ExpressionId id = mock(ExpressionId.class);
    when(idFactory.createId(string)).thenReturn(id);

    Expression expression = new StringNode(string).expression(idFactory);

    assertThat(expression.id()).isSameAs(id);
    assertThat(expression.result()).isSameAs(string);
  }
}
