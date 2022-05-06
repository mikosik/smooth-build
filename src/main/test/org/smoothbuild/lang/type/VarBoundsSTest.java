package org.smoothbuild.lang.type;

import static org.smoothbuild.testing.type.TestingTS.BOOL;
import static org.smoothbuild.testing.type.TestingTS.STRING;
import static org.smoothbuild.testing.type.TestingTS.VAR_A;
import static org.smoothbuild.testing.type.TestingTS.VAR_B;
import static org.smoothbuild.util.type.Side.LOWER;
import static org.smoothbuild.util.type.Side.UPPER;

import org.junit.jupiter.api.Test;
import org.smoothbuild.testing.TestingContext;

import com.google.common.testing.EqualsTester;

public class VarBoundsSTest extends TestingContext {
  @Test
  public void equality() {
    new EqualsTester()
        .addEqualityGroup(vbS())
        .addEqualityGroup(vbS(VAR_A, LOWER, STRING))
        .addEqualityGroup(vbS(VAR_A, UPPER, STRING))
        .addEqualityGroup(vbS(VAR_A, LOWER, BOOL))
        .addEqualityGroup(vbS(VAR_B, LOWER, STRING), vbS(VAR_B, LOWER, STRING));
  }
}
