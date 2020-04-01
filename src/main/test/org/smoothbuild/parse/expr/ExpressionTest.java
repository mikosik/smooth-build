package org.smoothbuild.parse.expr;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.lang.base.Location.unknownLocation;
import static org.smoothbuild.testing.common.AssertCall.assertCall;
import static org.smoothbuild.util.Lists.list;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.smoothbuild.exec.task.base.Task;
import org.smoothbuild.lang.base.Location;
import org.smoothbuild.lang.base.Scope;

public class ExpressionTest {
  @Test
  public void null_code_location_is_forbidden() {
    assertCall(() -> new MyExpression(list(), null))
        .throwsException(NullPointerException.class);
  }

  @Test
  public void null_children_is_forbidden() {
    assertCall(() -> new MyExpression(null, unknownLocation()))
        .throwsException(NullPointerException.class);
  }

  @Test
  public void code_location() {
    Location location = unknownLocation();
    MyExpression expression = new MyExpression(list(), location);
    assertThat(expression.location())
        .isEqualTo(location);
  }

  public static class MyExpression extends Expression {
    public MyExpression(List<? extends Expression> children, Location location) {
      super(children, location);
    }

    @Override
    public Task createTask(Scope<Task> scope) {
      return null;
    }
  }
}
