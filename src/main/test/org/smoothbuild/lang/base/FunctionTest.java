package org.smoothbuild.lang.base;

import static org.smoothbuild.util.Lists.list;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import java.util.List;

import org.junit.Test;
import org.smoothbuild.lang.expr.Expression;
import org.smoothbuild.lang.type.ConcreteType;
import org.smoothbuild.lang.type.TestingTypesDb;
import org.smoothbuild.lang.type.Type;
import org.smoothbuild.lang.type.TypeChooser;

public class FunctionTest {
  private static final ConcreteType STRING = new TestingTypesDb().string();
  private String name;
  private List<Parameter> parameters;
  private Signature signature;
  private Function function;

  @Test(expected = NullPointerException.class)
  public void nullSignatureIsForbidden() throws Exception {
    new MyAbstractFunction(null);
  }

  @Test
  public void type_returns_signature_type() {
    given(signature = new Signature(STRING, "name", list()));
    given(function = new MyAbstractFunction(signature));
    when(function).type();
    thenReturned(STRING);
  }

  @Test
  public void name_returns_signature_name() {
    given(name = "name");
    given(signature = new Signature(STRING, name, list()));
    given(function = new MyAbstractFunction(signature));
    when(function).type();
    thenReturned(STRING);
  }

  @Test
  public void params_returns_signature_params() {
    given(parameters = list(new Parameter(STRING, "name", null)));
    given(signature = new Signature(STRING, "name", parameters));
    given(function = new MyAbstractFunction(signature));
    when(function).parameters();
    thenReturned(parameters);
  }

  public static class MyAbstractFunction extends Function {
    public MyAbstractFunction(Signature signature) {
      super(signature, Location.unknownLocation());
    }

    @Override
    public Expression createCallExpression(Type type,
        TypeChooser<ConcreteType> evaluatorTypeChooser, Location location) {
      return null;
    }
  }
}
