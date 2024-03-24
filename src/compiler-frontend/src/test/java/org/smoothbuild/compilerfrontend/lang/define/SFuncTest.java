package org.smoothbuild.compilerfrontend.lang.define;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.collect.Maybe.none;
import static org.smoothbuild.common.collect.Maybe.some;
import static org.smoothbuild.common.collect.NList.nlist;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.location;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.sFuncSchema;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.sIntType;

import org.junit.jupiter.api.Test;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.compilerfrontend.lang.type.SType;
import org.smoothbuild.compilerfrontend.testing.TestingSExpression;

public class SFuncTest {
  @Test
  public void func_without_params_can_be_called_without_args() {
    SFunc func = myFunc(sIntType(), list());
    assertThat(func.canBeCalledArgless()).isTrue();
  }

  @Test
  public void func_with_all_params_with_default_values_can_be_called_without_args() {
    SFunc func = myFunc(sIntType(), list(paramWithDefault("a"), paramWithDefault("b")));
    assertThat(func.canBeCalledArgless()).isTrue();
  }

  @Test
  public void func_with_one_param_without_default_value_cannot_be_called_without_args() {
    SFunc func = myFunc(sIntType(), list(paramWithDefault("a"), paramWithoutDefault("b")));
    assertThat(func.canBeCalledArgless()).isFalse();
  }

  private SItem paramWithDefault(String name) {
    return TestingSExpression.sItem(sIntType(), name, some(TestingSExpression.sString()));
  }

  private SItem paramWithoutDefault(String name) {
    return TestingSExpression.sItem(sIntType(), name, none());
  }

  private SFunc myFunc(SType resultT, List<SItem> params) {
    var schema = sFuncSchema(SItem.toTypes(params), resultT);
    return new SNamedExprFunc(
        schema, "name", nlist(params), TestingSExpression.sString(), location(1));
  }
}
