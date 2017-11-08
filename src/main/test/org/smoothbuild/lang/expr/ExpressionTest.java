package org.smoothbuild.lang.expr;

import static java.util.Arrays.asList;
import static org.smoothbuild.lang.type.Types.STRING;
import static org.testory.Testory.given;
import static org.testory.Testory.givenTest;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import org.junit.Before;
import org.junit.Test;
import org.smoothbuild.db.values.ValuesDb;
import org.smoothbuild.lang.function.base.Scope;
import org.smoothbuild.lang.message.Location;
import org.smoothbuild.lang.type.Type;
import org.smoothbuild.task.base.Evaluator;

public class ExpressionTest {
  private final Type type = STRING;
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
    public MyExpression(Type type, Location location) {
      super(type, asList(), location);
    }

    @Override
    public Evaluator createEvaluator(ValuesDb valuesDb, Scope<Evaluator> scope) {
      return null;
    }
  }
}
