package org.smoothbuild.compile.fs.lang.define;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.collect.NList.nlist;

import org.junit.jupiter.api.Test;
import org.smoothbuild.testing.TestContext;

public class ConstructorSTest extends TestContext {
  @Test
  public void to_string() {
    var structTS = structTS("MyStruct", nlist(sigS(intTS(), "field")));
    var constructorS = constructorS(17, structTS, "constructorName");
    assertThat(constructorS.toString())
        .isEqualTo("""
            ConstructorS(
              name = constructorName
              schema = <>(Int)->MyStruct
              params = [
                ItemS(
                  type = Int
                  name = field
                  defaultValue = Optional.empty
                  location = myBuild.smooth:2
                )
              ]
              location = myBuild.smooth:17
            )""");
  }
}
