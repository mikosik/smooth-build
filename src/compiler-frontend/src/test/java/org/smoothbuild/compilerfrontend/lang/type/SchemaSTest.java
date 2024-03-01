package org.smoothbuild.compilerfrontend.lang.type;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.compilerfrontend.lang.type.VarSetS.varSetS;

import com.google.common.testing.EqualsTester;
import org.junit.jupiter.api.Test;
import org.smoothbuild.compilerfrontend.testing.TestExpressionS;

public class SchemaSTest extends TestExpressionS {
  @Test
  public void equals_and_hashcode() {
    new EqualsTester()
        .addEqualityGroup(new SchemaS(varSetS(), intTS()))
        .addEqualityGroup(new SchemaS(varSetS(), blobTS()))
        .addEqualityGroup(new SchemaS(varSetS(), arrayTS(intTS())))
        .addEqualityGroup(new SchemaS(varSetS(), funcTS(intTS())))
        .addEqualityGroup(new SchemaS(varSetS(), funcTS(intTS(), intTS())))
        .addEqualityGroup(new SchemaS(varSetS(varA()), arrayTS(varA())))
        .addEqualityGroup(new SchemaS(varSetS(varA()), funcTS(varA())))
        .addEqualityGroup(new SchemaS(varSetS(varA()), funcTS(varA(), varA())))
        .testEquals();
  }

  @Test
  public void to_string() {
    var schemaS = new SchemaS(varSetS(varA()), funcTS(intTS(), varA()));
    assertThat(schemaS.toString()).isEqualTo("<A>(Int)->A");
  }
}
