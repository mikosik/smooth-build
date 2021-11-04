package org.smoothbuild.lang.base.type.api;

import static org.smoothbuild.lang.base.type.TestingSTypes.A;
import static org.smoothbuild.lang.base.type.TestingSTypes.B;
import static org.smoothbuild.lang.base.type.TestingSTypes.BOOL;
import static org.smoothbuild.lang.base.type.TestingSTypes.LOWER;
import static org.smoothbuild.lang.base.type.TestingSTypes.STRING;
import static org.smoothbuild.lang.base.type.TestingSTypes.UPPER;

import org.junit.jupiter.api.Test;
import org.smoothbuild.testing.TestingContextImpl;

import com.google.common.testing.EqualsTester;

public class BoundsMapTest extends TestingContextImpl {
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
