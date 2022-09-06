package org.smoothbuild.compile.lang.type;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.compile.lang.type.VarSetS.varSetS;

import org.junit.jupiter.api.Test;
import org.smoothbuild.testing.TestContext;

import com.google.common.testing.EqualsTester;

public class SchemaSTest extends TestContext {
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
    var schemaS = new SchemaS(varSetS(varA()), funcTS(varA(), intTS()));
    assertThat(schemaS.toString())
        .isEqualTo("<A>A(Int)");
  }
}
