package org.smoothbuild.compilerfrontend.lang.define;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.collect.NList.nlist;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.location;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.sIntType;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.sStringType;

import org.junit.jupiter.api.Test;
import org.smoothbuild.compilerfrontend.testing.TestingSExpression;

public class SNamedExprFuncTest {
  @Test
  public void to_string() {
    var params = nlist(TestingSExpression.sItem(sIntType(), "myParam"));
    var schema = TestingSExpression.sFuncSchema(params, sStringType());
    var func =
        new SNamedExprFunc(schema, "myFunc", params, TestingSExpression.sInt(17), location(1));
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
                  location = {prj}/build.smooth:1
                )
              ]
              location = {prj}/build.smooth:1
              body = SInt(Int, 17, {prj}/build.smooth:1)
            )""");
  }
}
