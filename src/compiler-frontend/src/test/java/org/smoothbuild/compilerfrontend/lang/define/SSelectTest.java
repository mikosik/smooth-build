package org.smoothbuild.compilerfrontend.lang.define;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.compilerfrontend.lang.base.NList.nlist;

import org.junit.jupiter.api.Test;
import org.smoothbuild.compilerfrontend.testing.FrontendCompilerTestContext;

public class SSelectTest extends FrontendCompilerTestContext {
  @Test
  void to_string() {
    var annotationS = new SAnnotation("myAnnotation", sString(7, "myPath"), location(17));
    var structTS = sStructType("MyStruct", nlist(sSig(sIntType(), "field")));
    var structValue = sAnnotatedValue(11, annotationS, structTS, "structValue");
    var selectS = sSelect(3, sInstantiate(12, structValue), "field");
    assertThat(selectS.toString())
        .isEqualTo(
            """
            SSelect(
              selectable = SInstantiate(
                typeArgs = <>
                polymorphicS = SReference(
                  schema = <>MyStruct
                  referencedName = structValue
                  location = {t-project}/module.smooth:12
                )
                evaluationType = MyStruct{Int field}
                location = {t-project}/module.smooth:12
              )
              field = field
              location = {t-project}/module.smooth:3
            )""");
  }
}
