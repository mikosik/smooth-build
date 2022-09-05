package org.smoothbuild.compile.lang.define;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.testing.TestContext;
import org.smoothbuild.util.collect.NList;

public class AnnFuncSTest extends TestContext {
  @Test
  public void to_string() {
    var func = natFuncS(stringTS(), "myFunc", NList.nlist(itemS(intTS(), "myParam")));
    assertThat(func.toString())
        .isEqualTo("@Native(\"StringS[type=String, string=impl, loc=myBuild.smooth:1]\") "
            + "loc=myBuild.smooth:1 String myFunc(Int myParam)");
  }
}
