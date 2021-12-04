package org.smoothbuild.lang.base.type.api;

import static org.smoothbuild.lang.base.type.TestingTsS.A;
import static org.smoothbuild.lang.base.type.TestingTsS.B;
import static org.smoothbuild.lang.base.type.TestingTsS.BOOL;
import static org.smoothbuild.lang.base.type.TestingTsS.LOWER;
import static org.smoothbuild.lang.base.type.TestingTsS.STRING;
import static org.smoothbuild.lang.base.type.TestingTsS.UPPER;
import static org.smoothbuild.lang.base.type.TestingTsS.bm;

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
