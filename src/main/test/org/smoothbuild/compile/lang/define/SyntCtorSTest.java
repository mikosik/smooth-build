package org.smoothbuild.compile.lang.define;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.util.collect.NList.nlist;

import org.junit.jupiter.api.Test;
import org.smoothbuild.testing.TestContext;

public class SyntCtorSTest extends TestContext {
  @Test
  public void to_string() {
    var structTS = structTS("MyStruct", nlist(sigS(intTS(), "field")));
    var syntCtorS = syntCtorS(17, structTS, "syntCtorName");
    assertThat(syntCtorS.toString())
        .isEqualTo("""
            SyntCtorS(
              schema = <>(Int)->MyStruct
              params = [
                ItemS(
                  type = Int
                  name = field
                  defaultValue = Optional.empty
                  loc = myBuild.smooth:2
                )
              ]
              loc = myBuild.smooth:17
            )""");
  }
}
