package org.smoothbuild.compile.fs.lang.define;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.testing.TestContext;
import org.smoothbuild.util.collect.NList;

public class AnnotatedFuncSTest extends TestContext {
  @Test
  public void to_string() {
    var params = NList.nlist(itemS(intTS(), "myParam"));
    var funcTS = funcSchemaS(params, stringTS());
    var func = new AnnotatedFuncS(nativeAnnotationS(), funcTS, "myFunc", params, location(1));
    assertThat(func.toString())
        .isEqualTo("""
            AnnotatedFuncS(
              AnnotationS(
                name = Native
                path = StringS(String, "impl", myBuild.smooth:1)
                location = myBuild.smooth:1
              )
              schema = <>(Int)->String
              params = [
                ItemS(
                  type = Int
                  name = myParam
                  defaultValue = Optional.empty
                  location = myBuild.smooth:1
                )
              ]
              location = myBuild.smooth:1
            )""");
  }
}
