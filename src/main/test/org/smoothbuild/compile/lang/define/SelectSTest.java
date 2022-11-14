package org.smoothbuild.compile.lang.define;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.util.collect.NList.nlist;

import org.junit.jupiter.api.Test;
import org.smoothbuild.testing.TestContext;

public class SelectSTest extends TestContext {
  @Test
  public void to_string() {
    var annS = new AnnS("myAnnotation", stringS(7, "myPath"), loc(17));
    var structTS = structTS("MyStruct", nlist(sigS(intTS(), "field")));
    var structValue = annValS(annS, structTS, "structValue", loc(11));
    var selectS = selectS(3, monoizeS(12, structValue), "field");
    assertThat(selectS.toString())
        .isEqualTo("""
            SelectS(
              selectable = PolyRefS(
                varMap = {}
                polyExprS = PolyRefS(
                  polyEvaluable = PolyValS(
                    schema = <>MyStruct
                    mono = AnnVal(
                      AnnS(
                        name = myAnnotation
                        path = StringS(String, "myPath", myBuild.smooth:7)
                        loc = myBuild.smooth:17
                      )
                      type = MyStruct
                      name = structValue
                      loc = myBuild.smooth:11
                    )
                  )
                  loc = myBuild.smooth:12
                )
                evalT = MyStruct
                loc = myBuild.smooth:12
              )
              field = field
              loc = myBuild.smooth:3
            )""");
  }
}