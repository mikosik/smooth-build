package org.smoothbuild.parse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.Test;
import org.smoothbuild.expression.Expression;
import org.smoothbuild.problem.SourceLocation;

public class ArgumentTest {
  String name = "name";
  Expression expression = mock(Expression.class);
  SourceLocation sourceLocation = mock(SourceLocation.class);

  @Test(expected = NullPointerException.class)
  public void nullExpressionIsForbidden() {
    new Argument(name, null, sourceLocation);
  }

  @Test(expected = NullPointerException.class)
  public void nullSourceLocationIsForbidden() {
    new Argument(name, expression, null);
  }

  @Test
  public void argumentWithNonNullNameIsExplicit() throws Exception {
    assertThat(new Argument(name, expression, sourceLocation).isExplicit()).isTrue();
  }

  @Test
  public void argumentWithNullNameIsNotExplicit() throws Exception {
    assertThat(new Argument(null, expression, sourceLocation).isExplicit()).isFalse();
  }
}
