package org.smoothbuild.lang.base.type.api;

import static org.smoothbuild.lang.base.type.TestingTS.A;
import static org.smoothbuild.lang.base.type.TestingTS.B;
import static org.smoothbuild.lang.base.type.TestingTS.BOOL;
import static org.smoothbuild.lang.base.type.TestingTS.LOWER;
import static org.smoothbuild.lang.base.type.TestingTS.STRING;
import static org.smoothbuild.lang.base.type.TestingTS.UPPER;
import static org.smoothbuild.lang.base.type.TestingTS.vb;

import org.junit.jupiter.api.Test;
import org.smoothbuild.testing.TestingContext;

import com.google.common.testing.EqualsTester;

public class VarBoundsTest extends TestingContext {
  @Test
  public void equality() {
    new EqualsTester()
        .addEqualityGroup(vb())
        .addEqualityGroup(vb(A, LOWER, STRING))
        .addEqualityGroup(vb(A, UPPER, STRING))
        .addEqualityGroup(vb(A, LOWER, BOOL))
        .addEqualityGroup(vb(B, LOWER, STRING), vb(B, LOWER, STRING));
  }
}
