package org.smoothbuild.compile.frontend.lang.define;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.collect.Lists.list;
import static org.smoothbuild.common.collect.NList.nlist;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.smoothbuild.compile.frontend.lang.type.TypeS;
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
  public void func_with_all_params_with_default_values_can_be_called_without_args() {
    FuncS func = myFunc(intTS(), list(paramWithDefault("a"), paramWithDefault("b")));
    assertThat(func.canBeCalledArgless())
        .isTrue();
  }

  @Test
  public void func_with_one_param_without_default_value_cannot_be_called_without_args() {
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

  private FuncS myFunc(TypeS resultT, ImmutableList<ItemS> params) {
    var schema = funcSchemaS(ItemS.toTypes(params), resultT);
    return new NamedExprFuncS(schema, "name", nlist(params), stringS(), location(1));
  }
}
