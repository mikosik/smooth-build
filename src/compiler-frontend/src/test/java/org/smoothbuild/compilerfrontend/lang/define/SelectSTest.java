package org.smoothbuild.compilerfrontend.lang.define;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.collect.NList.nlist;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.annotatedValueS;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.instantiateS;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.intTS;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.location;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.selectS;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.sigS;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.stringS;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.structTS;

import org.junit.jupiter.api.Test;

public class SelectSTest {
  @Test
  public void to_string() {
    var annotationS = new SAnnotation("myAnnotation", stringS(7, "myPath"), location(17));
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
                  referencedName = structValue
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
