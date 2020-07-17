package org.smoothbuild.lang.expr;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.lang.base.Location.internal;
import static org.smoothbuild.testing.common.AssertCall.assertCall;
import static org.smoothbuild.util.Lists.list;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.smoothbuild.lang.base.Location;

public class ExpressionTest {
  @Test
  public void null_code_location_is_forbidden() {
    assertCall(() -> new MyExpression(list(), null))
        .throwsException(NullPointerException.class);
  }

  @Test
  public void null_children_is_forbidden() {
    assertCall(() -> new MyExpression(null, internal()))
        .throwsException(NullPointerException.class);
  }

  @Test
  public void code_location() {
    Location location = internal();
    MyExpression expression = new MyExpression(list(), location);
    assertThat(expression.location())
        .isEqualTo(location);
  }

  public static class MyExpression extends Expression {
    public MyExpression(List<? extends Expression> children, Location location) {
      super(children, location);
    }

    @Override
    public <T> T visit(ExpressionVisitor<T> visitor) {
      return null;
    }
  }
}
