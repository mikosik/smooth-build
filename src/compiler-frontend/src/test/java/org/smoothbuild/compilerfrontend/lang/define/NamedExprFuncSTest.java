package org.smoothbuild.compilerfrontend.lang.define;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.collect.NList.nlist;
import static org.smoothbuild.compilerfrontend.testing.TestingExpressionS.funcSchemaS;
import static org.smoothbuild.compilerfrontend.testing.TestingExpressionS.intS;
import static org.smoothbuild.compilerfrontend.testing.TestingExpressionS.intTS;
import static org.smoothbuild.compilerfrontend.testing.TestingExpressionS.itemS;
import static org.smoothbuild.compilerfrontend.testing.TestingExpressionS.location;
import static org.smoothbuild.compilerfrontend.testing.TestingExpressionS.stringTS;

import org.junit.jupiter.api.Test;

public class NamedExprFuncSTest {
  @Test
  public void to_string() {
    var params = nlist(itemS(intTS(), "myParam"));
    var schema = funcSchemaS(params, stringTS());
    var func = new NamedExprFuncS(schema, "myFunc", params, intS(17), location(1));
    assertThat(func.toString())
        .isEqualTo(
            """
            NamedExprFuncS(
              name = myFunc
              schema = <>(Int)->String
              params = [
                ItemS(
                  type = Int
                  name = myParam
                  defaultValue = None
                  location = {prj}/build.smooth:1
                )
              ]
              location = {prj}/build.smooth:1
              body = IntS(Int, 17, {prj}/build.smooth:1)
            )""");
  }
}
