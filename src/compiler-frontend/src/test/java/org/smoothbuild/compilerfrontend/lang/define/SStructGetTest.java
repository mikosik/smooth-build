package org.smoothbuild.compilerfrontend.lang.define;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.compilerfrontend.lang.name.NList.nlist;

import org.junit.jupiter.api.Test;
import org.smoothbuild.compilerfrontend.dagger.FrontendCompilerTestContext;

public class SStructGetTest extends FrontendCompilerTestContext {
  @Test
  void to_source_code() {
    assertThat(createSStructGet().toSourceCode()).isEqualTo("structValue<>.field");
  }

  @Test
  void to_string() {
    assertThat(createSStructGet().toString()).isEqualTo("""
            SStructGet(
              structExpr = SInstantiate(
                typeArgs = <>
                polymorphic = SPolyReference(
                  typeScheme = <>MyStruct
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

  private SStructGet createSStructGet() {
    var annotation = new SAnnotation("myAnnotation", sString(7, "myPath"), location(17));
    var structType = sStructType("MyStruct", nlist(sSig(sIntType(), "field")));
    var structValue = sPoly(sAnnotatedValue(11, annotation, structType, "structValue"));
    return sStructGet(3, sInstantiate(12, structValue), "field");
  }
}
