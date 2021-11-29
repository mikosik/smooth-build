package org.smoothbuild.lang.base.define;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.lang.base.define.Item.toTypes;
import static org.smoothbuild.lang.base.define.TestingLoc.loc;
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

public class FuncSTest extends TestingContext {
  @Test
  public void func_without_params_can_be_called_without_args() {
    FuncS func = myFunc(STRING, list());
    assertThat(func.canBeCalledArgless())
        .isTrue();
  }

  @Test
  public void func_with_all_params_with_default_args_can_be_called_without_args() {
    FuncS func = myFunc(STRING, list(paramWithDefault("a"), paramWithDefault("b")));
    assertThat(func.canBeCalledArgless())
        .isTrue();
  }

  @Test
  public void func_with_one_param_without_default_args_cannot_be_called_without_args() {
    FuncS func = myFunc(STRING, list(paramWithDefault("a"), paramWithoutDefault("b")));
    assertThat(func.canBeCalledArgless())
        .isFalse();
  }

  private Item paramWithDefault(String name) {
    return param(name, Optional.of(stringS()));
  }

  private Item paramWithoutDefault(String name) {
    return param(name, Optional.empty());
  }

  private Item param(String name, Optional<ExprS> defaultArg) {
    return new Item(STRING, modulePath(), name, defaultArg, loc());
  }

  private FuncS myFunc(TypeS resultType, ImmutableList<Item> params) {
    return new DefFuncS(funcST(resultType, toTypes(params)),
        modulePath(), "name", nList(params), stringS(), loc(1)
    );
  }
}
