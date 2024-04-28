package org.smoothbuild.compilerfrontend.lang.define;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.collect.NList.nlist;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.location;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.sAnnotatedValue;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.sInstantiate;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.sIntType;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.sSelect;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.sSig;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.sString;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.sStructType;

import org.junit.jupiter.api.Test;

public class SSelectTest {
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
                  location = {project}/build.smooth:12
                )
                evaluationType = MyStruct{Int field}
                location = {project}/build.smooth:12
              )
              field = field
              location = {project}/build.smooth:3
            )""");
  }
}
