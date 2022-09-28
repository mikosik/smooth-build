package org.smoothbuild.compile.lang.define;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.compile.lang.define.ItemS.toTypes;
import static org.smoothbuild.util.collect.Lists.list;
import static org.smoothbuild.util.collect.NList.nlist;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.smoothbuild.compile.lang.type.FuncTS;
import org.smoothbuild.compile.lang.type.TypeS;
import org.smoothbuild.testing.TestContext;

import com.google.common.collect.ImmutableList;

public class FuncSTest extends TestContext {
  @Test
  public void func_without_params_can_be_called_without_args() {
    FuncS func = myFunc(intTS(), list());
    assertThat(func.canBeCalledArgless())
        .isTrue();
  }

  @Test
  public void func_with_all_params_with_default_args_can_be_called_without_args() {
    FuncS func = myFunc(intTS(), list(paramWithDefault("a"), paramWithDefault("b")));
    assertThat(func.canBeCalledArgless())
        .isTrue();
  }

  @Test
  public void func_with_one_param_without_default_args_cannot_be_called_without_args() {
    FuncS func = myFunc(intTS(), list(paramWithDefault("a"), paramWithoutDefault("b")));
    assertThat(func.canBeCalledArgless())
        .isFalse();
  }

  private ItemS paramWithDefault(String name) {
    return itemS(intTS(), name, Optional.of(stringS()));
  }

  private ItemS paramWithoutDefault(String name) {
    return itemS(intTS(), name, Optional.empty());
  }

  private FuncS myFunc(TypeS resT, ImmutableList<ItemS> params) {
    return new DefFuncS(new FuncTS(resT, toTypes(params)),
        modPath(), "name", nlist(params), stringS(), loc(1)
    );
  }
}
