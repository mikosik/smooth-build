package org.smoothbuild.expression;

import static org.fest.assertions.api.Assertions.assertThat;

import org.junit.Test;
import org.smoothbuild.function.Type;

public class StringExpressionTest {
  ExpressionId id = new ExpressionId("abc");
  String string = "string-value";

  StringExpression expression = new StringExpression(id, string);

  @Test
  public void id() {
    assertThat(expression.id()).isSameAs(id);
  }

  @Test
  public void type() {
    assertThat(expression.type()).isEqualTo(Type.STRING);
  }

  @Test
  public void result() {
    assertThat(expression.result()).isEqualTo(string);
  }
}
