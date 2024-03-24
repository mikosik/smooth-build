package org.smoothbuild.compilerfrontend.lang.define;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.collect.NList.nlist;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.funcSchemaS;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.intTS;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.itemS;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.location;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.nativeAnnotationS;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.stringTS;

import org.junit.jupiter.api.Test;

public class SAnnotatedFuncTest {
  @Test
  public void to_string() {
    var params = nlist(itemS(intTS(), "myParam"));
    var funcTS = funcSchemaS(params, stringTS());
    var func = new SAnnotatedFunc(nativeAnnotationS(), funcTS, "myFunc", params, location(1));
    assertThat(func.toString())
        .isEqualTo(
            """
            AnnotatedFuncS(
              AnnotationS(
                name = Native
                path = StringS(String, "impl", {prj}/build.smooth:1)
                location = {prj}/build.smooth:1
              )
              schema = <>(Int)->String
              params = [
                ItemS(
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
