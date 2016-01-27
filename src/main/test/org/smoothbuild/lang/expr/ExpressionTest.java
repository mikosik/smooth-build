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
import org.smoothbuild.lang.message.CodeLocation;
import org.smoothbuild.lang.type.Type;
import org.smoothbuild.task.base.Computer;

public class ExpressionTest {
  private final Type type = STRING;
  private CodeLocation codeLocation;

  private MyExpression expression;

  @Before
  public void before() {
    givenTest(this);
  }

  @Test
  public void null_type_is_forbidden() {
    when(() -> new MyExpression(null, codeLocation));
    thenThrown(NullPointerException.class);
  }

  @Test
  public void null_code_location_is_forbidden() {
    when(() -> new MyExpression(type, null));
    thenThrown(NullPointerException.class);
  }

  @Test
  public void type() throws Exception {
    given(expression = new MyExpression(type, codeLocation));
    when(expression.type());
    thenReturned(type);
  }

  @Test
  public void code_location() throws Exception {
    given(expression = new MyExpression(type, codeLocation));
    when(expression.codeLocation());
    thenReturned(codeLocation);
  }

  public static class MyExpression extends Expression {
    public MyExpression(Type type, CodeLocation codeLocation) {
      super(type, asList(), codeLocation);
    }

    public Computer createComputer() {
      return null;
    }
  }
}
