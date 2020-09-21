package org.smoothbuild.lang.base;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.mock;
import static org.smoothbuild.lang.base.Location.internal;
import static org.smoothbuild.lang.base.type.TestingTypes.STRING;
import static org.smoothbuild.util.Lists.list;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.smoothbuild.lang.base.type.Type;
import org.smoothbuild.lang.expr.Expression;
import org.smoothbuild.testing.TestingContext;
import org.smoothbuild.testing.common.TestingLocation;

import com.google.common.collect.ImmutableList;

public class CallableTest extends TestingContext {

  @Test
  public void type_returns_signature_type() {
    Callable callable = new MyCallable(STRING, "name", list());
    assertThat(callable.type())
        .isEqualTo(STRING);
  }

  @Test
  public void name_returns_signature_name() {
    Callable callable = new MyCallable(STRING, "name", list());
    assertThat(callable.type())
        .isEqualTo(STRING);
  }

  @Test
  public void params_returns_signature_params() {
    List<Item> parameters = list(new Item(STRING, "name", Optional.empty(), internal()));
    Callable callable = new MyCallable(STRING, "name", parameters);
    assertThat(callable.parameters())
        .isEqualTo(parameters);
  }

  @Test
  public void callable_without_params_can_be_called_without_args() {
    Callable callable = new MyCallable(STRING, "name", list());
    assertThat(callable.canBeCalledArgless())
        .isTrue();
  }

  @Test
  public void callable_with_all_params_with_default_values_can_be_called_without_args() {
    List<Item> parameters = list(paramWithDefault(), paramWithDefault());
    Callable callable = new MyCallable(STRING, "name", parameters);
    assertThat(callable.canBeCalledArgless())
        .isTrue();
  }

  @Test
  public void callable_with_one_param_without_default_values_cannot_be_called_without_args() {
    List<Item> parameters = list(paramWithDefault(), paramWithoutDefault());
    Callable callable = new MyCallable(STRING, "name", parameters);
    assertThat(callable.canBeCalledArgless())
        .isFalse();
  }

  private Item paramWithDefault() {
    return new Item(STRING, "a", Optional.of(mock(Expression.class)), internal());
  }

  private Item paramWithoutDefault() {
    return new Item(STRING, "a", Optional.empty(), internal());
  }

  public static class MyCallable extends Callable {
    public MyCallable(Type string, String name, List<Item> parameters) {
      super(string, name, ImmutableList.copyOf(parameters), TestingLocation.loc(1));
    }

    @Override
    public Expression createCallExpression(ImmutableList<Expression> arguments, Location location) {
      return null;
    }
  }
}
