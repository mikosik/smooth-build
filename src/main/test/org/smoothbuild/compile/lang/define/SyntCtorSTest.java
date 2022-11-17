package org.smoothbuild.compile.lang.define;

import static com.google.common.truth.Truth.assertThat;
import static java.util.Optional.empty;
import static org.smoothbuild.compile.lang.define.ItemS.toTypes;
import static org.smoothbuild.util.collect.NList.nlist;

import org.junit.jupiter.api.Test;
import org.smoothbuild.testing.TestContext;

public class SyntCtorSTest extends TestContext {
  @Test
  public void to_string() {
    var structTS = structTS("MyStruct", nlist(sigS(intTS(), "field")));
    var fields = structTS.fields();
    var params = fields.map(f -> new ItemS(f.type(), f.nameSane(), empty(), loc(2)));
    var funcTS = funcTS(structTS, toTypes(params.list()));
    var syntCtorS = new SyntCtorS(funcTS, "syntCtorName", params, loc(17));
    assertThat(syntCtorS.toString())
        .isEqualTo("""
            SyntCtorS(
              type = (Int)->MyStruct
              params = [
                Int field
              ]
              loc = myBuild.smooth:17
            )""");
  }
}
