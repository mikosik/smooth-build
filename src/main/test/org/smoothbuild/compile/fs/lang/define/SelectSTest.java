package org.smoothbuild.compile.fs.lang.define;

import org.junit.jupiter.api.Test;
import org.smoothbuild.testing.TestContext;
import org.smoothbuild.util.collect.NList;

import com.google.common.truth.Truth;

public class SelectSTest extends TestContext {
  @Test
  public void to_string() {
    var annotationS = new AnnotationS("myAnnotation", stringS(7, "myPath"), location(17));
    var structTS = structTS("MyStruct", NList.nlist(sigS(intTS(), "field")));
    var structValue = annotatedValueS(11, annotationS, structTS, "structValue");
    var selectS = selectS(3, monoizeS(12, structValue), "field");
    Truth.assertThat(selectS.toString())
        .isEqualTo("""
            SelectS(
              selectable = MonoizeS(
                varMap = {}
                monoizableS = EvaluableRefS(
                  namedEvaluable = AnnotatedValue(
                    AnnotationS(
                      name = myAnnotation
                      path = StringS(String, "myPath", myBuild.smooth:7)
                      location = myBuild.smooth:17
                    )
                    schema = <>MyStruct
                    name = structValue
                    location = myBuild.smooth:11
                  )
                  location = myBuild.smooth:12
                )
                evalT = MyStruct
                location = myBuild.smooth:12
              )
              field = field
              location = myBuild.smooth:3
            )""");
  }
}
