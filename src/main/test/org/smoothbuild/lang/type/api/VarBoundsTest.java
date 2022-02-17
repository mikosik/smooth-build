package org.smoothbuild.lang.type.api;

import static org.smoothbuild.testing.type.TestingTS.BOOL;
import static org.smoothbuild.testing.type.TestingTS.LOWER;
import static org.smoothbuild.testing.type.TestingTS.OPEN_A;
import static org.smoothbuild.testing.type.TestingTS.OPEN_B;
import static org.smoothbuild.testing.type.TestingTS.STRING;
import static org.smoothbuild.testing.type.TestingTS.UPPER;
import static org.smoothbuild.testing.type.TestingTS.vb;

import org.junit.jupiter.api.Test;
import org.smoothbuild.testing.TestingContext;

import com.google.common.testing.EqualsTester;

public class VarBoundsTest extends TestingContext {
  @Test
  public void equality() {
    new EqualsTester()
        .addEqualityGroup(vb())
        .addEqualityGroup(vb(OPEN_A, LOWER, STRING))
        .addEqualityGroup(vb(OPEN_A, UPPER, STRING))
        .addEqualityGroup(vb(OPEN_A, LOWER, BOOL))
        .addEqualityGroup(vb(OPEN_B, LOWER, STRING), vb(OPEN_B, LOWER, STRING));
  }
}
