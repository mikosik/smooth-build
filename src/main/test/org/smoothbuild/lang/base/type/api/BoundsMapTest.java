package org.smoothbuild.lang.base.type.api;

import static org.smoothbuild.lang.base.type.TestingTS.A;
import static org.smoothbuild.lang.base.type.TestingTS.B;
import static org.smoothbuild.lang.base.type.TestingTS.BOOL;
import static org.smoothbuild.lang.base.type.TestingTS.LOWER;
import static org.smoothbuild.lang.base.type.TestingTS.STRING;
import static org.smoothbuild.lang.base.type.TestingTS.UPPER;
import static org.smoothbuild.lang.base.type.TestingTS.bm;

import org.junit.jupiter.api.Test;
import org.smoothbuild.testing.TestingContext;

import com.google.common.testing.EqualsTester;

public class BoundsMapTest extends TestingContext {
  @Test
  public void equality() {
    new EqualsTester()
        .addEqualityGroup(bm())
        .addEqualityGroup(bm(A, LOWER, STRING))
        .addEqualityGroup(bm(A, UPPER, STRING))
        .addEqualityGroup(bm(A, LOWER, BOOL))
        .addEqualityGroup(bm(B, LOWER, STRING), bm(B, LOWER, STRING));
  }
}
