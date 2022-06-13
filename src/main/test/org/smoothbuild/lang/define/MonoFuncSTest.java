package org.smoothbuild.lang.define;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.lang.define.ItemS.toTypes;
import static org.smoothbuild.testing.type.TestingTS.STRING;
import static org.smoothbuild.util.collect.Lists.list;
import static org.smoothbuild.util.collect.NList.nList;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.smoothbuild.lang.type.MonoTS;
import org.smoothbuild.testing.TestingContext;

import com.google.common.collect.ImmutableList;

public class MonoFuncSTest extends TestingContext {
  @Test
  public void func_without_params_can_be_called_without_args() {
    MonoFuncS func = myFunc(STRING, list());
    assertThat(func.canBeCalledArgless())
        .isTrue();
  }

  @Test
  public void func_with_all_params_with_default_args_can_be_called_without_args() {
    MonoFuncS func = myFunc(STRING, list(paramWithDefault("a"), paramWithDefault("b")));
    assertThat(func.canBeCalledArgless())
        .isTrue();
  }

  @Test
  public void func_with_one_param_without_default_args_cannot_be_called_without_args() {
    MonoFuncS func = myFunc(STRING, list(paramWithDefault("a"), paramWithoutDefault("b")));
    assertThat(func.canBeCalledArgless())
        .isFalse();
  }

  private ItemS paramWithDefault(String name) {
    return param(name, Optional.of(stringS()));
  }

  private ItemS paramWithoutDefault(String name) {
    return param(name, Optional.empty());
  }

  private ItemS param(String name, Optional<MonoObjS> body) {
    return new ItemS(STRING, name, body, loc());
  }

  private MonoFuncS myFunc(MonoTS resT, ImmutableList<ItemS> params) {
    return new DefFuncS(funcTS(resT, toTypes(params)),
        modPath(), "name", nList(params), stringS(), loc(1)
    );
  }
}