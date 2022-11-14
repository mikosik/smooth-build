package org.smoothbuild.compile.lang.define;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.util.collect.NList.nlist;

import org.junit.jupiter.api.Test;
import org.smoothbuild.testing.TestContext;

public class DefFuncSTest extends TestContext {
  @Test
  public void to_string() {
    var func = defFuncS(stringTS(), "myFunc", nlist(itemS(intTS(), "myParam")), intS(17));
    assertThat(func.toString())
        .isEqualTo("""
            DefFuncS(
              type = (Int)->String
              params = [
                Int myParam
              ]
              loc = myBuild.smooth:1
              body = IntS(Int, 17, myBuild.smooth:1)
            )""");
  }
}
