package org.smoothbuild.compilerfrontend.lang.define;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.collect.NList.nlist;

import org.junit.jupiter.api.Test;
import org.smoothbuild.compilerfrontend.testing.FrontendCompilerTestContext;

public class SNamedExprFuncTest extends FrontendCompilerTestContext {
  @Test
  void to_string() {
    var params = nlist(sItem(sIntType(), "myParam"));
    var schema = sFuncSchema(params, sStringType());
    var func = new SNamedExprFunc(schema, "myFunc", params, sInt(17), location(1));
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
                  defaultValueFullName = None
                  location = {t-project}/module.smooth:1
                )
              ]
              location = {t-project}/module.smooth:1
              body = SInt(Int, 17, {t-project}/module.smooth:1)
            )""");
  }
}
