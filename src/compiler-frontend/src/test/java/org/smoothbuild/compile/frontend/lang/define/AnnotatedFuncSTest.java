package org.smoothbuild.compile.frontend.lang.define;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.collect.NList.nlist;

import org.junit.jupiter.api.Test;
import org.smoothbuild.testing.TestContext;

public class AnnotatedFuncSTest extends TestContext {
  @Test
  public void to_string() {
    var params = nlist(itemS(intTS(), "myParam"));
    var funcTS = funcSchemaS(params, stringTS());
    var func = new AnnotatedFuncS(nativeAnnotationS(), funcTS, "myFunc", params, location(1));
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
