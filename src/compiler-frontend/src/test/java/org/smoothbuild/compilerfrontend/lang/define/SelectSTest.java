package org.smoothbuild.compilerfrontend.lang.define;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.collect.NList.nlist;
import static org.smoothbuild.compilerfrontend.testing.TestingExpressionS.annotatedValueS;
import static org.smoothbuild.compilerfrontend.testing.TestingExpressionS.instantiateS;
import static org.smoothbuild.compilerfrontend.testing.TestingExpressionS.intTS;
import static org.smoothbuild.compilerfrontend.testing.TestingExpressionS.location;
import static org.smoothbuild.compilerfrontend.testing.TestingExpressionS.selectS;
import static org.smoothbuild.compilerfrontend.testing.TestingExpressionS.sigS;
import static org.smoothbuild.compilerfrontend.testing.TestingExpressionS.stringS;
import static org.smoothbuild.compilerfrontend.testing.TestingExpressionS.structTS;

import org.junit.jupiter.api.Test;

public class SelectSTest {
  @Test
  public void to_string() {
    var annotationS = new AnnotationS("myAnnotation", stringS(7, "myPath"), location(17));
    var structTS = structTS("MyStruct", nlist(sigS(intTS(), "field")));
    var structValue = annotatedValueS(11, annotationS, structTS, "structValue");
    var selectS = selectS(3, instantiateS(12, structValue), "field");
    assertThat(selectS.toString())
        .isEqualTo(
            """
            SelectS(
              selectable = InstantiateS(
                typeArgs = <>
                polymorphicS = ReferenceS(
                  schema = <>MyStruct
                  name = structValue
                  location = {prj}/build.smooth:12
                )
                evaluationType = MyStruct(Int field)
                location = {prj}/build.smooth:12
              )
              field = field
              location = {prj}/build.smooth:3
            )""");
  }
}
