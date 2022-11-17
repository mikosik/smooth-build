package org.smoothbuild.compile.lang.define;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.compile.lang.define.ItemS.toTypes;
import static org.smoothbuild.util.collect.NList.nlist;

import org.junit.jupiter.api.Test;
import org.smoothbuild.testing.TestContext;

public class AnnFuncSTest extends TestContext {
  @Test
  public void to_string() {
    var params = nlist(itemS(intTS(), "myParam"));
    var funcTS = funcTS(stringTS(), toTypes(params.list()));
    var func = new AnnFuncS(natAnnS(), funcTS, "myFunc", params, loc(1));
    assertThat(func.toString())
        .isEqualTo("""
            AnnFuncS(
              AnnS(
                name = Native
                path = StringS(String, "impl", myBuild.smooth:1)
                loc = myBuild.smooth:1
              )
              type = (Int)->String
              params = [
                Int myParam
              ]
              loc = myBuild.smooth:1
            )""");
  }
}
