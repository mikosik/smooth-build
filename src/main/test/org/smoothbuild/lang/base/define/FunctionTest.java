package org.smoothbuild.lang.base.define;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.mock;
import static org.smoothbuild.lang.base.define.Item.toItemSignatures;
import static org.smoothbuild.lang.base.define.TestingLocation.loc;
import static org.smoothbuild.lang.base.define.TestingModulePath.modulePath;
import static org.smoothbuild.lang.base.type.TestingTypes.STRING;
import static org.smoothbuild.util.collect.Lists.list;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.smoothbuild.lang.base.type.api.Type;
import org.smoothbuild.lang.expr.Expression;
import org.smoothbuild.testing.TestingContextImpl;

import com.google.common.collect.ImmutableList;

public class FunctionTest extends TestingContextImpl {
  @Test
  public void function_without_params_can_be_called_without_args() {
    Function function = myFunction(STRING, list());
    assertThat(function.canBeCalledArgless())
        .isTrue();
  }

  @Test
  public void function_with_all_params_with_default_arguments_can_be_called_without_args() {
    List<Item> parameters = list(paramWithDefault(), paramWithDefault());
    Function function = myFunction(STRING, parameters);
    assertThat(function.canBeCalledArgless())
        .isTrue();
  }

  @Test
  public void function_with_one_param_without_default_arguments_cannot_be_called_without_args() {
    List<Item> parameters = list(paramWithDefault(), paramWithoutDefault());
    Function function = myFunction(STRING, parameters);
    assertThat(function.canBeCalledArgless())
        .isFalse();
  }

  private Item paramWithDefault() {
    return param(Optional.of(mock(Expression.class)));
  }

  private Item paramWithoutDefault() {
    return param(Optional.empty());
  }

  private Item param(Optional<Expression> defaultArgument) {
    return new Item(STRING, modulePath(), "a", defaultArgument, loc());
  }

  private Function myFunction(Type resultType, List<Item> parameters) {
    return new DefinedFunction(functionST(resultType, toItemSignatures(parameters)),
        modulePath(), "name", ImmutableList.copyOf(parameters),
        mock(Expression.class), loc(1)
    );
  }
}
