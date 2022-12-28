package org.smoothbuild.compile.fs.lang.define;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.testing.TestContext;
import org.smoothbuild.util.collect.NList;

public class NamedExprFuncSTest extends TestContext {
  @Test
  public void to_string() {
    var params = NList.nlist(itemS(intTS(), "myParam"));
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
                  location = myBuild.smooth:1
                )
              ]
              location = myBuild.smooth:1
              body = IntS(Int, 17, myBuild.smooth:1)
            )""");
  }
}
