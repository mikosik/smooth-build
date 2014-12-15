package org.smoothbuild.lang.expr;

import static org.smoothbuild.lang.base.Types.STRING;
import static org.testory.Testory.given;
import static org.testory.Testory.givenTest;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import org.junit.Before;
import org.junit.Test;
import org.smoothbuild.lang.base.Type;
import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.task.work.TaskWorker;
import org.smoothbuild.util.Empty;
import org.testory.Closure;

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
    when($myExpression(null, codeLocation));
    thenThrown(NullPointerException.class);
  }

  @Test
  public void null_code_location_is_forbidden() {
    when($myExpression(type, null));
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

  private static Closure $myExpression(final Type type, final CodeLocation codeLocation) {
    return new Closure() {
      @Override
      public Object invoke() throws Throwable {
        return new MyExpression(type, codeLocation);
      }
    };
  }

  public static class MyExpression extends Expression {
    public MyExpression(Type type, CodeLocation codeLocation) {
      super(type, Empty.expressionList(), codeLocation);
    }

    @Override
    public TaskWorker createWorker() {
      return null;
    }
  }
}
