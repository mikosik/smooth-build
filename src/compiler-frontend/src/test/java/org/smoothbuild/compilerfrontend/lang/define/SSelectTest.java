package org.smoothbuild.compilerfrontend.lang.define;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.collect.NList.nlist;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.location;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.sIntType;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.sSig;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.sString;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.sStructType;

import org.junit.jupiter.api.Test;
import org.smoothbuild.compilerfrontend.testing.TestingSExpression;

public class SSelectTest {
  @Test
  public void to_string() {
    var annotationS = new SAnnotation("myAnnotation", sString(7, "myPath"), location(17));
    var structTS = sStructType("MyStruct", nlist(sSig(sIntType(), "field")));
    var structValue = TestingSExpression.sAnnotatedValue(11, annotationS, structTS, "structValue");
    var selectS =
        TestingSExpression.sSelect(3, TestingSExpression.sInstantiate(12, structValue), "field");
    assertThat(selectS.toString())
        .isEqualTo(
            """
            SSelect(
              selectable = SInstantiate(
                typeArgs = <>
                polymorphicS = SReference(
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
