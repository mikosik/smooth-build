package org.smoothbuild.lang.type.api;

import static org.smoothbuild.lang.type.api.Side.LOWER;
import static org.smoothbuild.lang.type.api.Side.UPPER;
import static org.smoothbuild.testing.type.TestingTS.BOOL;
import static org.smoothbuild.testing.type.TestingTS.STRING;
import static org.smoothbuild.testing.type.TestingTS.VAR_A;
import static org.smoothbuild.testing.type.TestingTS.VAR_B;
import static org.smoothbuild.testing.type.TestingTS.vb;

import org.junit.jupiter.api.Test;
import org.smoothbuild.testing.TestingContext;

import com.google.common.testing.EqualsTester;

public class VarBoundsTest extends TestingContext {
  @Test
  public void equality() {
    new EqualsTester()
        .addEqualityGroup(vb())
        .addEqualityGroup(vb(VAR_A, LOWER, STRING))
        .addEqualityGroup(vb(VAR_A, UPPER, STRING))
        .addEqualityGroup(vb(VAR_A, LOWER, BOOL))
        .addEqualityGroup(vb(VAR_B, LOWER, STRING), vb(VAR_B, LOWER, STRING));
  }
}
