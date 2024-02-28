package org.smoothbuild.compile.frontend.lang.define;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.collect.NList.nlist;

import org.junit.jupiter.api.Test;
import org.smoothbuild.testing.TestContext;

public class SelectSTest extends TestContext {
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
                evaluationT = MyStruct(Int field)
                location = {prj}/build.smooth:12
              )
              field = field
              location = {prj}/build.smooth:3
            )""");
  }
}
