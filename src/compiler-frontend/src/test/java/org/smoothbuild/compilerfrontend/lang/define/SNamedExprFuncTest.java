package org.smoothbuild.compilerfrontend.lang.define;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.collect.NList.nlist;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.location;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.sFuncSchema;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.sInt;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.sIntType;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.sItem;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.sStringType;

import org.junit.jupiter.api.Test;

public class SNamedExprFuncTest {
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
                  defaultValue = None
                  location = {t-project}/build.smooth:1
                )
              ]
              location = {t-project}/build.smooth:1
              body = SInt(Int, 17, {t-project}/build.smooth:1)
            )""");
  }
}
