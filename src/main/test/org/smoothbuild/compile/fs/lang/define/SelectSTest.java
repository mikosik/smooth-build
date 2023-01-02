package org.smoothbuild.compile.fs.lang.define;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.testing.TestContext;
import org.smoothbuild.util.collect.NList;

public class SelectSTest extends TestContext {
  @Test
  public void to_string() {
    var annotationS = new AnnotationS("myAnnotation", stringS(7, "myPath"), location(17));
    var structTS = structTS("MyStruct", NList.nlist(sigS(intTS(), "field")));
    var structValue = annotatedValueS(11, annotationS, structTS, "structValue");
    var selectS = selectS(3, monoizeS(12, structValue), "field");
    assertThat(selectS.toString())
        .isEqualTo("""
            SelectS(
              selectable = MonoizeS(
                varMap = {}
                monoizableS = RefS(
                  schema = <>MyStruct
                  name = structValue
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
