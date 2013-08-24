package org.smoothbuild.expression;

import static org.assertj.core.api.Assertions.assertThat;
import static org.smoothbuild.expression.LiteralExpression.literalExpression;
import static org.smoothbuild.expression.LiteralExpression.stringExpression;

import org.junit.Test;
import org.smoothbuild.function.Type;

public class LiteralExpressionTest {
  ExpressionId id = new ExpressionId("abc");
  String string = "string-value";

  @Test(expected = IllegalArgumentException.class)
  public void typeIncompatibleWithValueThrowsException() throws Exception {
    literalExpression(id, Type.STRING, Integer.valueOf(33));
  }

  @Test
  public void idOfStringExpression() {
    assertThat(stringExpression(id, string).id()).isSameAs(id);
  }

  @Test
  public void typeOfStringExpression() {
    assertThat(stringExpression(id, string).type()).isEqualTo(Type.STRING);
  }

  @Test
  public void resultOfStringExpression() {
    assertThat(stringExpression(id, string).result()).isEqualTo(string);
  }
}
