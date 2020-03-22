package org.smoothbuild.lang.base;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.mock;
import static org.smoothbuild.testing.common.AssertCall.assertCall;
import static org.smoothbuild.util.Lists.list;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.smoothbuild.lang.object.type.ConcreteType;
import org.smoothbuild.parse.expr.Expression;
import org.smoothbuild.testing.TestingContext;

public class FunctionTest extends TestingContext {
  private final ConcreteType STRING = stringType();

  @Test
  public void nullSignatureIsForbidden() {
    assertCall(() -> new MyFunction(null))
        .throwsException(NullPointerException.class);
  }

  @Test
  public void type_returns_signature_type() {
    Function function = new MyFunction(new Signature(STRING, "name", list()));
    assertThat(function.type())
        .isEqualTo(STRING);
  }

  @Test
  public void name_returns_signature_name() {
    Function function = new MyFunction(new Signature(STRING, "name", list()));
    assertThat(function.type())
        .isEqualTo(STRING);
  }

  @Test
  public void params_returns_signature_params() {
    List<Parameter> parameters = list(new Parameter(0, STRING, "name", null));
    Function function = new MyFunction(new Signature(STRING, "name", parameters));
    assertThat(function.parameters())
        .isEqualTo(parameters);
  }

  @Test
  public void function_without_params_can_be_called_without_args() {
    Function function = new MyFunction(new Signature(STRING, "name", list()));
    assertThat(function.canBeCalledArgless())
        .isTrue();
  }

  @Test
  public void function_with_all_params_with_default_values_can_be_called_without_args() {
    List<Parameter> parameters = list(paramWithDefault(), paramWithDefault());
    Function function = new MyFunction(new Signature(STRING, "name", parameters));
    assertThat(function.canBeCalledArgless())
        .isTrue();
  }

  @Test
  public void function_with_one_param_without_default_values_cannot_be_called_without_args() {
    List<Parameter> parameters = list(paramWithDefault(), paramWithoutDefault());
    Function function = new MyFunction(new Signature(STRING, "name", parameters));
    assertThat(function.canBeCalledArgless())
        .isFalse();
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
