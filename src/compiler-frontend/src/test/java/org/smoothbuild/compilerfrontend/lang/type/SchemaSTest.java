package org.smoothbuild.compilerfrontend.lang.type;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.compilerfrontend.lang.type.SVarSet.varSetS;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.sArrayType;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.sBlobType;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.sFuncType;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.sIntType;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.varA;

import com.google.common.testing.EqualsTester;
import org.junit.jupiter.api.Test;

public class SchemaSTest {
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
