package org.smoothbuild.lang.base;

import static org.smoothbuild.util.Lists.list;
import static org.testory.Testory.given;
import static org.testory.Testory.mock;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import java.util.List;

import org.junit.Test;
import org.smoothbuild.lang.expr.Expression;
import org.smoothbuild.lang.object.type.ConcreteType;
import org.smoothbuild.testing.TestingContext;

public class FunctionTest extends TestingContext {
  private final ConcreteType STRING = stringType();
  private String name;
  private List<Parameter> parameters;
  private Signature signature;
  private Function function;

  @Test(expected = NullPointerException.class)
  public void nullSignatureIsForbidden() throws Exception {
    new MyFunction(null);
  }

  @Test
  public void type_returns_signature_type() {
    given(signature = new Signature(STRING, "name", list()));
    given(function = new MyFunction(signature));
    when(function).type();
    thenReturned(STRING);
  }

  @Test
  public void name_returns_signature_name() {
    given(name = "name");
    given(signature = new Signature(STRING, name, list()));
    given(function = new MyFunction(signature));
    when(function).type();
    thenReturned(STRING);
  }

  @Test
  public void params_returns_signature_params() {
    given(parameters = list(new Parameter(0, STRING, "name", null)));
    given(signature = new Signature(STRING, "name", parameters));
    given(function = new MyFunction(signature));
    when(function).parameters();
    thenReturned(parameters);
  }

  @Test
  public void function_without_params_can_be_called_without_args() {
    given(parameters = list());
    given(signature = new Signature(STRING, "name", parameters));
    given(function = new MyFunction(signature));
    when(() -> function.canBeCalledArgless());
    thenReturned(true);
  }

  @Test
  public void function_with_all_params_with_default_values_can_be_called_without_args() {
    given(parameters = list(paramWithDefault(), paramWithDefault()));
    given(signature = new Signature(STRING, "name", parameters));
    given(function = new MyFunction(signature));
    when(() -> function.canBeCalledArgless());
    thenReturned(true);
  }

  @Test
  public void function_with_one_param_without_default_values_cannot_be_called_without_args() {
    given(parameters = list(paramWithDefault(), paramWithoutDefault()));
    given(signature = new Signature(STRING, "name", parameters));
    given(function = new MyFunction(signature));
    when(() -> function.canBeCalledArgless());
    thenReturned(false);
  }

  private Parameter paramWithDefault() {
    return new Parameter(0, stringType(), "a", mock(Expression.class));
  }

  private Parameter paramWithoutDefault() {
    return new Parameter(0, stringType(), "a", null);
  }

  public static class MyFunction extends Function {
    public MyFunction(Signature signature) {
      super(signature, Location.unknownLocation());
    }

    @Override
    public Expression createCallExpression(List<? extends Expression> arguments,
        Location location) {
      return null;
    }
  }
}
