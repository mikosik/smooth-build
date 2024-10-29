package org.smoothbuild.compilerfrontend.lang.define;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.collect.NList.nlist;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.sConstructor;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.sIntType;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.sSig;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.sStructType;

import org.junit.jupiter.api.Test;

public class SConstructorTest {
  @Test
  void to_string() {
    var structTS = sStructType("MyStruct", nlist(sSig(sIntType(), "field")));
    var constructorS = sConstructor(17, structTS, "constructorName");
    assertThat(constructorS.toString())
        .isEqualTo(
            """
            SConstructor(
              name = constructorName
              schema = <>(Int)->MyStruct
              params = [
                SItem(
                  type = Int
                  name = field
                  defaultValue = None
                  location = {t-project}/build.smooth:2
                )
              ]
              location = {t-project}/build.smooth:17
            )""");
  }
}
