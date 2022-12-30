package org.smoothbuild.compile.fs.lang.define;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.testing.TestContext;
import org.smoothbuild.util.collect.NList;

public class ConstructorSTest extends TestContext {
  @Test
  public void to_string() {
    var structTS = structTS("MyStruct", NList.nlist(sigS(intTS(), "field")));
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
