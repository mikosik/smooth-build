package org.smoothbuild.lang.base.define;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.lang.base.define.Item.toTypes;
import static org.smoothbuild.lang.base.define.TestingLocation.loc;
import static org.smoothbuild.lang.base.define.TestingModulePath.modulePath;
import static org.smoothbuild.lang.base.type.TestingTypesS.STRING;
import static org.smoothbuild.util.collect.Lists.list;
import static org.smoothbuild.util.collect.NList.nList;

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
    FunctionS function = myFunction(STRING, list(paramWithDefault("a"), paramWithDefault("b")));
    assertThat(function.canBeCalledArgless())
        .isTrue();
  }

  @Test
  public void function_with_one_param_without_default_arguments_cannot_be_called_without_args() {
    FunctionS function = myFunction(STRING, list(paramWithDefault("a"), paramWithoutDefault("b")));
    assertThat(function.canBeCalledArgless())
        .isFalse();
  }

  private Item paramWithDefault(String name) {
    return param(name, Optional.of(stringS()));
  }

  private Item paramWithoutDefault(String name) {
    return param(name, Optional.empty());
  }

  private Item param(String name, Optional<ExprS> defaultArgument) {
    return new Item(STRING, modulePath(), name, defaultArgument, loc());
  }

  private FunctionS myFunction(TypeS resultType, ImmutableList<Item> parameters) {
    return new DefinedFunctionS(functionST(resultType, toTypes(parameters)),
        modulePath(), "name", nList(parameters), stringS(), loc(1)
    );
  }
}
