package org.smoothbuild.compilerfrontend.lang.type;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.compilerfrontend.lang.type.SVarSet.varSetS;

import com.google.common.testing.EqualsTester;
import org.junit.jupiter.api.Test;
import org.smoothbuild.compilerfrontend.testing.FrontendCompilerTestContext;

public class SSchemaTest extends FrontendCompilerTestContext {
  @Test
  void equals_and_hashcode() {
    new EqualsTester()
        .addEqualityGroup(new SSchema(varSetS(), sIntType()))
        .addEqualityGroup(new SSchema(varSetS(), sBlobType()))
        .addEqualityGroup(new SSchema(varSetS(), sIntArrayT()))
        .addEqualityGroup(new SSchema(varSetS(), sIntFuncType()))
        .addEqualityGroup(new SSchema(varSetS(), sFuncType(sIntType(), sIntType())))
        .addEqualityGroup(new SSchema(varSetS(varA()), sVarAArrayT()))
        .addEqualityGroup(new SSchema(varSetS(varA()), sVarAFuncType()))
        .addEqualityGroup(new SSchema(varSetS(varA()), sFuncType(varA(), varA())))
        .testEquals();
  }

  @Test
  void to_string() {
    var sSchema = new SSchema(varSetS(varA()), sFuncType(sIntType(), varA()));
    assertThat(sSchema.toString()).isEqualTo("<A>(Int)->A");
  }
}
