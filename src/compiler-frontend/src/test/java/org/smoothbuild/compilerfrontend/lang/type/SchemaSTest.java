package org.smoothbuild.compilerfrontend.lang.type;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.compilerfrontend.lang.type.SVarSet.varSetS;

import com.google.common.testing.EqualsTester;
import org.junit.jupiter.api.Test;
import org.smoothbuild.compilerfrontend.testing.FrontendCompilerTestContext;

public class SchemaSTest extends FrontendCompilerTestContext {
  @Test
  void equals_and_hashcode() {
    new EqualsTester()
        .addEqualityGroup(new SchemaS(varSetS(), sIntType()))
        .addEqualityGroup(new SchemaS(varSetS(), sBlobType()))
        .addEqualityGroup(new SchemaS(varSetS(), sArrayType(sIntType())))
        .addEqualityGroup(new SchemaS(varSetS(), sFuncType(sIntType())))
        .addEqualityGroup(new SchemaS(varSetS(), sFuncType(sIntType(), sIntType())))
        .addEqualityGroup(new SchemaS(varSetS(varA()), sArrayType(varA())))
        .addEqualityGroup(new SchemaS(varSetS(varA()), sFuncType(varA())))
        .addEqualityGroup(new SchemaS(varSetS(varA()), sFuncType(varA(), varA())))
        .testEquals();
  }

  @Test
  void to_string() {
    var schemaS = new SchemaS(varSetS(varA()), sFuncType(sIntType(), varA()));
    assertThat(schemaS.toString()).isEqualTo("<A>(Int)->A");
  }
}
