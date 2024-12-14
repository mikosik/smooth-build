package org.smoothbuild.compilerfrontend.lang.define;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.compilerfrontend.lang.base.Fqn.fqn;
import static org.smoothbuild.compilerfrontend.lang.base.NList.nlist;

import org.junit.jupiter.api.Test;
import org.smoothbuild.compilerfrontend.testing.FrontendCompilerTestContext;

public class SAnnotatedFuncTest extends FrontendCompilerTestContext {
  @Test
  void to_string() {
    var params = nlist(sItem(sIntType(), "myParam"));
    var funcTS = sFuncSchema(params, sStringType());
    var func = new SAnnotatedFunc(sNativeAnnotation(), funcTS, fqn("myFunc"), params, location(1));
    assertThat(func.toString())
        .isEqualTo(
            """
            SAnnotatedFunc(
              SAnnotation(
                name = Native
                path = SString(String, "impl", {t-project}/module.smooth:1)
                location = {t-project}/module.smooth:1
              )
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
            )""");
  }
}
