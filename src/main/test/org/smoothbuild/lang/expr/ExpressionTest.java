package org.smoothbuild.lang.expr;

import static org.smoothbuild.util.Lists.list;
import static org.testory.Testory.given;
import static org.testory.Testory.givenTest;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.smoothbuild.db.values.ValuesDb;
import org.smoothbuild.lang.base.Location;
import org.smoothbuild.lang.base.Scope;
import org.smoothbuild.task.base.Evaluator;

public class ExpressionTest {
  private Location location;
  private MyExpression expression;

  @Before
  public void before() {
    givenTest(this);
  }

  @Test
  public void null_code_location_is_forbidden() {
    when(() -> new MyExpression(list(), null));
    thenThrown(NullPointerException.class);
  }

  @Test
  public void null_children_is_forbidden() {
    when(() -> new MyExpression(null, location));
    thenThrown(NullPointerException.class);
  }

  @Test
  public void code_location() throws Exception {
    given(expression = new MyExpression(list(), location));
    when(expression.location());
    thenReturned(location);
  }

  public static class MyExpression extends Expression {
    public MyExpression(List<? extends Expression> children, Location location) {
      super(children, location);
    }

    @Override
    public Evaluator createEvaluator(ValuesDb valuesDb, Scope<Evaluator> scope) {
      return null;
    }
  }
}
