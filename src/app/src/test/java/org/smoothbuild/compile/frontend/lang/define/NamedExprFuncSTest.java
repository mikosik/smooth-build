package org.smoothbuild.compile.frontend.lang.define;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.collect.NList.nlist;

import org.junit.jupiter.api.Test;
import org.smoothbuild.testing.TestContext;

public class NamedExprFuncSTest extends TestContext {
  @Test
  public void to_string() {
    var params = nlist(itemS(intTS(), "myParam"));
    var schema = funcSchemaS(params, stringTS());
    var func = new NamedExprFuncS(schema, "myFunc", params, intS(17), location(1));
    assertThat(func.toString())
        .isEqualTo("""
            NamedExprFuncS(
              name = myFunc
              schema = <>(Int)->String
              params = [
                ItemS(
                  type = Int
                  name = myParam
                  defaultValue = Optional.empty
                  location = build.smooth:1
                )
              ]
              location = build.smooth:1
              body = IntS(Int, 17, build.smooth:1)
            )""");
  }
}
