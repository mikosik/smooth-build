package org.smoothbuild.lang.base;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.mock;
import static org.smoothbuild.lang.base.Location.internal;
import static org.smoothbuild.lang.base.Signature.signature;
import static org.smoothbuild.lang.base.type.TestingTypes.STRING;
import static org.smoothbuild.testing.common.AssertCall.assertCall;
import static org.smoothbuild.util.Lists.list;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.smoothbuild.lang.expr.Expression;
import org.smoothbuild.testing.TestingContext;

import com.google.common.collect.ImmutableList;

public class CallableTest extends TestingContext {

  @Test
  public void nullSignatureIsForbidden() {
    assertCall(() -> new MyCallable(null))
        .throwsException(NullPointerException.class);
  }

  @Test
  public void type_returns_signature_type() {
    Callable callable = new MyCallable(signature(STRING, "name", list()));
    assertThat(callable.type())
        .isEqualTo(STRING);
  }

  @Test
  public void name_returns_signature_name() {
    Callable callable = new MyCallable(signature(STRING, "name", list()));
    assertThat(callable.type())
        .isEqualTo(STRING);
  }

  @Test
  public void params_returns_signature_params() {
    List<Parameter> parameters = list(new Parameter(0, STRING, "name", null, internal()));
    Callable callable = new MyCallable(signature(STRING, "name", parameters));
    assertThat(callable.parameters())
        .isEqualTo(parameters);
  }

  @Test
  public void callable_without_params_can_be_called_without_args() {
    Callable callable = new MyCallable(signature(STRING, "name", list()));
    assertThat(callable.canBeCalledArgless())
        .isTrue();
  }

  @Test
  public void callable_with_all_params_with_default_values_can_be_called_without_args() {
    List<Parameter> parameters = list(paramWithDefault(), paramWithDefault());
    Callable callable = new MyCallable(signature(STRING, "name", parameters));
    assertThat(callable.canBeCalledArgless())
        .isTrue();
  }

  @Test
  public void callable_with_one_param_without_default_values_cannot_be_called_without_args() {
    List<Parameter> parameters = list(paramWithDefault(), paramWithoutDefault());
    Callable callable = new MyCallable(signature(STRING, "name", parameters));
    assertThat(callable.canBeCalledArgless())
        .isFalse();
  }

  private Parameter paramWithDefault() {
    return new Parameter(0, STRING, "a", Optional.of(mock(Expression.class)), internal());
  }

  private Parameter paramWithoutDefault() {
    return new Parameter(0, STRING, "a", null, internal());
  }

  public static class MyCallable extends Callable {
    public MyCallable(Signature signature) {
      super(signature, internal());
    }

    @Override
    public Expression createCallExpression(ImmutableList<Expression> arguments, Location location) {
      return null;
    }
  }
}
