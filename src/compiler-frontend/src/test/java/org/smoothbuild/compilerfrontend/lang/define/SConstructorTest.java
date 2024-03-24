package org.smoothbuild.compilerfrontend.lang.define;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.collect.NList.nlist;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.constructorS;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.intTS;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.sigS;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.structTS;

import org.junit.jupiter.api.Test;

public class SConstructorTest {
  @Test
  public void to_string() {
    var structTS = structTS("MyStruct", nlist(sigS(intTS(), "field")));
    var constructorS = constructorS(17, structTS, "constructorName");
    assertThat(constructorS.toString())
        .isEqualTo(
            """
            ConstructorS(
              name = constructorName
              schema = <>(Int)->MyStruct
              params = [
                ItemS(
                  type = Int
                  name = field
                  defaultValue = None
                  location = {prj}/build.smooth:2
                )
              ]
              location = {prj}/build.smooth:17
            )""");
  }
}
