package org.smoothbuild.lang.expr;

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
import org.smoothbuild.lang.type.ConcreteType;
import org.smoothbuild.lang.type.TestingTypesDb;
import org.smoothbuild.task.base.Evaluator;
import org.smoothbuild.util.Dag;

public class ExpressionTest {
  private final ConcreteType type = new TestingTypesDb().string();
  private Location location;
  private MyExpression expression;

  @Before
  public void before() {
    givenTest(this);
  }

  @Test
  public void null_type_is_forbidden() {
    when(() -> new MyExpression(null, location));
    thenThrown(NullPointerException.class);
  }

  @Test
  public void null_code_location_is_forbidden() {
    when(() -> new MyExpression(type, null));
    thenThrown(NullPointerException.class);
  }

  @Test
  public void type() throws Exception {
    given(expression = new MyExpression(type, location));
    when(expression.type());
    thenReturned(type);
  }

  @Test
  public void code_location() throws Exception {
    given(expression = new MyExpression(type, location));
    when(expression.location());
    thenReturned(location);
  }

  public static class MyExpression extends Expression {
    public MyExpression(ConcreteType type, Location location) {
      super(type, location);
    }

    @Override
    public Dag<Evaluator> createEvaluator(List<Dag<Expression>> children, ValuesDb valuesDb,
        Scope<Dag<Evaluator>> scope) {
      return null;
    }
  }
}
