package org.smoothbuild.compilerfrontend.lang.type;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.compilerfrontend.lang.type.STypeVarSet.sTypeVarSet;

import com.google.common.testing.EqualsTester;
import org.junit.jupiter.api.Test;
import org.smoothbuild.compilerfrontend.testing.FrontendCompilerTestContext;

public class SSchemaTest extends FrontendCompilerTestContext {
  @Test
  void equals_and_hashcode() {
    new EqualsTester()
        .addEqualityGroup(new SSchema(sTypeVarSet(), sIntType()))
        .addEqualityGroup(new SSchema(sTypeVarSet(), sBlobType()))
        .addEqualityGroup(new SSchema(sTypeVarSet(), sIntArrayT()))
        .addEqualityGroup(new SSchema(sTypeVarSet(), sIntFuncType()))
        .addEqualityGroup(new SSchema(sTypeVarSet(), sFuncType(sIntType(), sIntType())))
        .addEqualityGroup(new SSchema(sTypeVarSet(varA()), sVarAArrayT()))
        .addEqualityGroup(new SSchema(sTypeVarSet(varA()), sVarAFuncType()))
        .addEqualityGroup(new SSchema(sTypeVarSet(varA()), sFuncType(varA(), varA())))
        .testEquals();
  }

  @Test
  void to_string() {
    var aVar = sVar("module:func:A");
    var sSchema = new SSchema(sTypeVarSet(aVar), sFuncType(sIntType(), aVar));
    assertThat(sSchema.toString()).isEqualTo("<module:func:A>(Int)->module:func:A");
  }

  @Test
  void to_short_string() {
    var aVar = sVar("module:func:A");
    var sSchema = new SSchema(sTypeVarSet(aVar), sFuncType(sIntType(), aVar));
    assertThat(sSchema.toShortString()).isEqualTo("<A>(Int)->A");
  }
}
