package org.smoothbuild.compilerfrontend.lang.define;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.compilerfrontend.lang.name.Fqn.fqn;
import static org.smoothbuild.compilerfrontend.lang.name.NList.nlist;

import org.junit.jupiter.api.Test;
import org.smoothbuild.compilerfrontend.testing.FrontendCompilerTestContext;

public class SNamedExprFuncTest extends FrontendCompilerTestContext {
  @Test
  void to_source_code() {
    var params = nlist(sItem(varA(), "p1"), sItem(sIntType(), "p2", "default:value"));
    var resultType = sStringType();
    var func = new SNamedExprFunc(resultType, fqn("module:myFunc"), params, sInt(17), location(1));
    assertThat(func.toSourceCode())
        .isEqualTo("""
        String myFunc(A p1, Int p2 = default:value)
          = 17;""");
  }

  @Test
  void to_string() {
    var params = nlist(sItem(sIntType(), "myParam"));
    var resultType = sStringType();
    var func = new SNamedExprFunc(resultType, fqn("myFunc"), params, sInt(17), location(1));
    assertThat(func.toString())
        .isEqualTo(
            """
            SNamedExprFunc(
              fqn = myFunc
              type = (Int)->String
              params = [
                SItem(
                  type = Int
                  fqn = myFunc:myParam
                  defaultValue = None
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
