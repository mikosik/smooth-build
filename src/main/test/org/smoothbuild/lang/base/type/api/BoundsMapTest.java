package org.smoothbuild.lang.base.type.api;

import static org.smoothbuild.lang.base.type.TestingTypes.A;
import static org.smoothbuild.lang.base.type.TestingTypes.B;
import static org.smoothbuild.lang.base.type.TestingTypes.BOOL;
import static org.smoothbuild.lang.base.type.TestingTypes.STRING;

import org.junit.jupiter.api.Test;
import org.smoothbuild.testing.TestingContextImpl;

import com.google.common.testing.EqualsTester;

public class BoundsMapTest extends TestingContextImpl {
  @Test
  public void equality() {
    new EqualsTester()
        .addEqualityGroup(bm())
        .addEqualityGroup(bm(A, lowerST(), STRING))
        .addEqualityGroup(bm(A, upperST(), STRING))
        .addEqualityGroup(bm(A, lowerST(), BOOL))
        .addEqualityGroup(
            bm(B, lowerST(), STRING), bm(B, lowerST(), STRING));
  }
}
