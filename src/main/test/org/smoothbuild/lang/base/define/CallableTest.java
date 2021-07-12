package org.smoothbuild.lang.base.define;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.mock;
import static org.smoothbuild.lang.base.define.TestingItem.item;
import static org.smoothbuild.lang.base.define.TestingLocation.loc;
import static org.smoothbuild.lang.base.type.TestingTypes.STRING;
import static org.smoothbuild.util.Lists.list;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.smoothbuild.lang.base.type.FunctionType;
import org.smoothbuild.lang.base.type.Type;
import org.smoothbuild.lang.expr.Expression;
import org.smoothbuild.testing.TestingContext;

import com.google.common.collect.ImmutableList;

public class CallableTest extends TestingContext {

  @Test
  public void type_returns_function_type() {
    Callable callable = new MyCallable(STRING, "name", list(), loc(7));
    assertThat(callable.type())
        .isEqualTo(new FunctionType(STRING, ImmutableList.of()));
  }

  @Test
  public void params_returns_signature_params() {
    List<Item> parameters = list(item("name"));
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
    return new Item(STRING, "a", Optional.of(mock(Expression.class)));
  }

  private Item paramWithoutDefault() {
    return new Item(STRING, "a", Optional.empty());
  }

  public static class MyCallable extends Callable {
    public MyCallable(Type string, String name, List<Item> parameters) {
      this(string, name, parameters, loc(1));
    }

    public MyCallable(Type string, String name, List<Item> parameters, Location location) {
      super(string, TestingModulePath.modulePath(), name, ImmutableList.copyOf(parameters),
          location);
    }
  }
}
