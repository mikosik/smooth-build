package org.smoothbuild.lang.base.define;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.mock;
import static org.smoothbuild.lang.base.define.Item.toTypes;
import static org.smoothbuild.lang.base.define.TestingLocation.loc;
import static org.smoothbuild.lang.base.define.TestingModulePath.modulePath;
import static org.smoothbuild.lang.base.type.TestingTypesS.STRING;
import static org.smoothbuild.util.collect.Lists.list;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.smoothbuild.lang.base.type.impl.TypeS;
import org.smoothbuild.lang.expr.ExprS;
import org.smoothbuild.testing.TestingContext;

import com.google.common.collect.ImmutableList;

public class FunctionSTest extends TestingContext {
  @Test
  public void function_without_params_can_be_called_without_args() {
    FunctionS function = myFunction(STRING, list());
    assertThat(function.canBeCalledArgless())
        .isTrue();
  }

  @Test
  public void function_with_all_params_with_default_arguments_can_be_called_without_args() {
    List<Item> parameters = list(paramWithDefault(), paramWithDefault());
    FunctionS function = myFunction(STRING, parameters);
    assertThat(function.canBeCalledArgless())
        .isTrue();
  }

  @Test
  public void function_with_one_param_without_default_arguments_cannot_be_called_without_args() {
    List<Item> parameters = list(paramWithDefault(), paramWithoutDefault());
    FunctionS function = myFunction(STRING, parameters);
    assertThat(function.canBeCalledArgless())
        .isFalse();
  }

  private Item paramWithDefault() {
    return param(Optional.of(mock(ExprS.class)));
  }

  private Item paramWithoutDefault() {
    return param(Optional.empty());
  }

  private Item param(Optional<ExprS> defaultArgument) {
    return new Item(STRING, modulePath(), "a", defaultArgument, loc());
  }

  private FunctionS myFunction(TypeS resultType, List<Item> parameters) {
    return new DefinedFunction(functionST(resultType, toTypes(parameters)),
        modulePath(), "name", ImmutableList.copyOf(parameters),
        mock(ExprS.class), loc(1)
    );
  }
}
