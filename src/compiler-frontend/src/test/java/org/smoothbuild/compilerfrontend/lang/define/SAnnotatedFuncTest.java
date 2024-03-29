package org.smoothbuild.compilerfrontend.lang.define;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.collect.NList.nlist;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.location;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.sFuncSchema;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.sIntType;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.sItem;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.sNativeAnnotation;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.sStringType;

import org.junit.jupiter.api.Test;

public class SAnnotatedFuncTest {
  @Test
  public void to_string() {
    var params = nlist(sItem(sIntType(), "myParam"));
    var funcTS = sFuncSchema(params, sStringType());
    var func = new SAnnotatedFunc(sNativeAnnotation(), funcTS, "myFunc", params, location(1));
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
