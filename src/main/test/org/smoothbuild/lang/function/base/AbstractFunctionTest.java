package org.smoothbuild.lang.function.base;

import static java.util.Arrays.asList;
import static org.smoothbuild.lang.type.Types.STRING;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import java.util.List;

import org.junit.Test;
import org.smoothbuild.lang.expr.Expression;
import org.smoothbuild.lang.message.Location;

public class AbstractFunctionTest {
  Name name;
  List<Parameter> parameters;
  Signature signature;
  AbstractFunction function;

  @Test(expected = NullPointerException.class)
  public void nullSignatureIsForbidden() throws Exception {
    new MyAbstractFunction(null);
  }

  @Test
  public void type_returns_signature_type() {
    given(signature = new Signature(STRING, new Name("name"), asList()));
    given(function = new MyAbstractFunction(signature));
    when(function).type();
    thenReturned(STRING);
  }

  @Test
  public void name_returns_signature_name() {
    given(name = new Name("name"));
    given(signature = new Signature(STRING, name, asList()));
    given(function = new MyAbstractFunction(signature));
    when(function).type();
    thenReturned(STRING);
  }

  @Test
  public void params_returns_signature_params() {
    given(parameters = asList(new Parameter(STRING, new Name("name"), null)));
    given(signature = new Signature(STRING, new Name("name"), parameters));
    given(function = new MyAbstractFunction(signature));
    when(function).parameters();
    thenReturned(parameters);
  }

  public static class MyAbstractFunction extends AbstractFunction {
    public MyAbstractFunction(Signature signature) {
      super(signature);
    }

    @Override
    public Expression createCallExpression(boolean isGenerated, Location location) {
      return null;
    }
  }
}
