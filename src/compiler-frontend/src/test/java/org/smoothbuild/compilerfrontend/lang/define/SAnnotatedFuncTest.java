package org.smoothbuild.compilerfrontend.lang.define;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.collect.NList.nlist;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.location;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.sIntType;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.sStringType;

import org.junit.jupiter.api.Test;
import org.smoothbuild.compilerfrontend.testing.TestingSExpression;

public class SAnnotatedFuncTest {
  @Test
  public void to_string() {
    var params = nlist(TestingSExpression.sItem(sIntType(), "myParam"));
    var funcTS = TestingSExpression.sFuncSchema(params, sStringType());
    var func = new SAnnotatedFunc(
        TestingSExpression.sNativeAnnotation(), funcTS, "myFunc", params, location(1));
    assertThat(func.toString())
        .isEqualTo(
            """
            SAnnotatedFunc(
              SAnnotation(
                name = Native
                path = SString(String, "impl", {prj}/build.smooth:1)
                location = {prj}/build.smooth:1
              )
              schema = <>(Int)->String
              params = [
                SItem(
                  type = Int
                  name = myParam
                  defaultValue = None
                  location = {prj}/build.smooth:1
                )
              ]
              location = {prj}/build.smooth:1
            )""");
  }
}
