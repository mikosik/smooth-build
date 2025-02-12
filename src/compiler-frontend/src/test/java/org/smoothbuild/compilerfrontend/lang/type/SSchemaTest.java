package org.smoothbuild.compilerfrontend.lang.type;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.compilerfrontend.lang.type.SVarSet.sVarSet;

import com.google.common.testing.EqualsTester;
import org.junit.jupiter.api.Test;
import org.smoothbuild.compilerfrontend.testing.FrontendCompilerTestContext;

public class SSchemaTest extends FrontendCompilerTestContext {
  @Test
  void equals_and_hashcode() {
    new EqualsTester()
        .addEqualityGroup(new SSchema(sVarSet(), sIntType()))
        .addEqualityGroup(new SSchema(sVarSet(), sBlobType()))
        .addEqualityGroup(new SSchema(sVarSet(), sIntArrayT()))
        .addEqualityGroup(new SSchema(sVarSet(), sIntFuncType()))
        .addEqualityGroup(new SSchema(sVarSet(), sFuncType(sIntType(), sIntType())))
        .addEqualityGroup(new SSchema(sVarSet(varA()), sVarAArrayT()))
        .addEqualityGroup(new SSchema(sVarSet(varA()), sVarAFuncType()))
        .addEqualityGroup(new SSchema(sVarSet(varA()), sFuncType(varA(), varA())))
        .testEquals();
  }

  @Test
  void to_string() {
    var aVar = sVar("module:func:A");
    var sSchema = new SSchema(sVarSet(aVar), sFuncType(sIntType(), aVar));
    assertThat(sSchema.toString()).isEqualTo("<module:func:A>(Int)->module:func:A");
  }

  @Test
  void to_short_string() {
    var aVar = sVar("module:func:A");
    var sSchema = new SSchema(sVarSet(aVar), sFuncType(sIntType(), aVar));
    assertThat(sSchema.toShortString()).isEqualTo("<A>(Int)->A");
  }
}
