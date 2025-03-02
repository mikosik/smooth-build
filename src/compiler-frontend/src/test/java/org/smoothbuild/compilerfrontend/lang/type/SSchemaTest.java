package org.smoothbuild.compilerfrontend.lang.type;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.collect.List.list;

import com.google.common.testing.EqualsTester;
import org.junit.jupiter.api.Test;
import org.smoothbuild.compilerfrontend.testing.FrontendCompilerTestContext;

public class SSchemaTest extends FrontendCompilerTestContext {
  @Test
  void equals_and_hashcode() {
    new EqualsTester()
        .addEqualityGroup(new SSchema(list(), sIntType()))
        .addEqualityGroup(new SSchema(list(), sBlobType()))
        .addEqualityGroup(new SSchema(list(), sIntArrayT()))
        .addEqualityGroup(new SSchema(list(), sIntFuncType()))
        .addEqualityGroup(new SSchema(list(), sFuncType(sIntType(), sIntType())))
        .addEqualityGroup(new SSchema(list(varA()), sVarAArrayT()))
        .addEqualityGroup(new SSchema(list(varA()), sVarAFuncType()))
        .addEqualityGroup(new SSchema(list(varA()), sFuncType(varA(), varA())))
        .testEquals();
  }

  @Test
  void to_string() {
    var aVar = sVar("A");
    var sSchema = new SSchema(list(aVar), sFuncType(sIntType(), aVar));
    assertThat(sSchema.toString()).isEqualTo("<A>(Int)->A");
  }
}
