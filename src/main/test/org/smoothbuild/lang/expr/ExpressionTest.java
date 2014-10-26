package org.smoothbuild.lang.expr;

import static org.smoothbuild.lang.base.Types.STRING;
import static org.testory.Testory.given;
import static org.testory.Testory.givenTest;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import org.junit.Before;
import org.junit.Test;
import org.smoothbuild.lang.base.SString;
import org.smoothbuild.lang.base.Type;
import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.task.work.TaskWorker;
import org.smoothbuild.util.Empty;
import org.testory.Closure;

public class ExpressionTest {
  Type<SString> type = STRING;
  CodeLocation codeLocation;

  MyExpression expr;

  @Before
  public void before() {
    givenTest(this);
  }

  @Test
  public void null_type_is_forbidden() {
    when($myExpr(null, codeLocation));
    thenThrown(NullPointerException.class);
  }

  @Test
  public void null_code_location_is_forbidden() {
    when($myExpr(type, null));
    thenThrown(NullPointerException.class);
  }

  @Test
  public void type() throws Exception {
    given(expr = new MyExpression(type, codeLocation));
    when(expr.type());
    thenReturned(type);
  }

  @Test
  public void code_location() throws Exception {
    given(expr = new MyExpression(type, codeLocation));
    when(expr.codeLocation());
    thenReturned(codeLocation);
  }

  private static Closure $myExpr(final Type<SString> type, final CodeLocation codeLocation) {
    return new Closure() {
      @Override
      public Object invoke() throws Throwable {
        return new MyExpression(type, codeLocation);
      }
    };
  }

  public static class MyExpression extends Expression<SString> {
    public MyExpression(Type<SString> type, CodeLocation codeLocation) {
      super(type, Empty.exprList(), codeLocation);
    }

    @Override
    public TaskWorker<SString> createWorker() {
      return null;
    }
  }
}
