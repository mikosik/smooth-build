package org.smoothbuild.compilerfrontend.lang.define;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.compilerfrontend.lang.base.Fqn.fqn;
import static org.smoothbuild.compilerfrontend.lang.base.NList.nlist;

import org.junit.jupiter.api.Test;
import org.smoothbuild.compilerfrontend.testing.FrontendCompilerTestContext;

public class SNamedExprFuncTest extends FrontendCompilerTestContext {
  @Test
  void to_string() {
    var params = nlist(sItem(sIntType(), "myParam"));
    var schema = sFuncSchema(params, sStringType());
    var func = new SNamedExprFunc(schema, fqn("myFunc"), params, sInt(17), location(1));
    assertThat(func.toString())
        .isEqualTo(
            """
            SNamedExprFunc(
              name = myFunc
              schema = <>(Int)->String
              params = [
                SItem(
                  type = Int
                  name = myParam
                  defaultValueId = None
                  location = {t-project}/module.smooth:1
                )
              ]
              location = {t-project}/module.smooth:1
              body = SInt(
                type = Int
                bigInteger = 17
                location = {t-project}/module.smooth:1
              )
            )""");
  }
}
