package org.smoothbuild.compilerfrontend.lang.define;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.collect.Maybe.none;
import static org.smoothbuild.common.collect.Maybe.some;
import static org.smoothbuild.common.collect.NList.nlist;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.funcSchemaS;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.intTS;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.itemS;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.location;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.stringS;

import org.junit.jupiter.api.Test;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.compilerfrontend.lang.type.SType;

public class SFuncTest {
  @Test
  public void func_without_params_can_be_called_without_args() {
    SFunc func = myFunc(intTS(), list());
    assertThat(func.canBeCalledArgless()).isTrue();
  }

  @Test
  public void func_with_all_params_with_default_values_can_be_called_without_args() {
    SFunc func = myFunc(intTS(), list(paramWithDefault("a"), paramWithDefault("b")));
    assertThat(func.canBeCalledArgless()).isTrue();
  }

  @Test
  public void func_with_one_param_without_default_value_cannot_be_called_without_args() {
    SFunc func = myFunc(intTS(), list(paramWithDefault("a"), paramWithoutDefault("b")));
    assertThat(func.canBeCalledArgless()).isFalse();
  }

  private SItem paramWithDefault(String name) {
    return itemS(intTS(), name, some(stringS()));
  }

  private SItem paramWithoutDefault(String name) {
    return itemS(intTS(), name, none());
  }

  private SFunc myFunc(SType resultT, List<SItem> params) {
    var schema = funcSchemaS(SItem.toTypes(params), resultT);
    return new SNamedExprFunc(schema, "name", nlist(params), stringS(), location(1));
  }
}
