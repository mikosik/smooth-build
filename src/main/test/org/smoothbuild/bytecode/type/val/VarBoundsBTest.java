package org.smoothbuild.bytecode.type.val;

import static org.smoothbuild.testing.type.TestingTB.BOOL;
import static org.smoothbuild.testing.type.TestingTB.STRING;
import static org.smoothbuild.testing.type.TestingTB.VAR_A;
import static org.smoothbuild.testing.type.TestingTB.VAR_B;
import static org.smoothbuild.util.type.Side.LOWER;
import static org.smoothbuild.util.type.Side.UPPER;

import org.junit.jupiter.api.Test;
import org.smoothbuild.testing.TestingContext;

import com.google.common.testing.EqualsTester;

public class VarBoundsBTest extends TestingContext {
  @Test
  public void equality() {
    new EqualsTester()
        .addEqualityGroup(vbB())
        .addEqualityGroup(vbB(VAR_A, LOWER, STRING))
        .addEqualityGroup(vbB(VAR_A, UPPER, STRING))
        .addEqualityGroup(vbB(VAR_A, LOWER, BOOL))
        .addEqualityGroup(vbB(VAR_B, LOWER, STRING), vbB(VAR_B, LOWER, STRING));
  }
}
