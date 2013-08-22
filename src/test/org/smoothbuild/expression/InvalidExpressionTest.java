package org.smoothbuild.expression;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.smoothbuild.function.Type;

public class InvalidExpressionTest {
  InvalidExpression invalidExpression = new InvalidExpression(Type.STRING);

  @Test
  public void type() {
    assertThat(invalidExpression.type()).isEqualTo(Type.STRING);
  }

  @Test
  public void id() {
    assertThat(invalidExpression.id()).isNotNull();
  }

  @Test(expected = RuntimeException.class)
  public void calculateThrowsException() throws Exception {
    invalidExpression.calculate();
  }

  @Test(expected = RuntimeException.class)
  public void resultThrowsException() throws Exception {
    invalidExpression.result();
  }
}
