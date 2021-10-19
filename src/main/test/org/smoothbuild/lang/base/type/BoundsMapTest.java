package org.smoothbuild.lang.base.type;

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
        .addEqualityGroup(bm(A, lower(), STRING))
        .addEqualityGroup(bm(A, upper(), STRING))
        .addEqualityGroup(bm(A, lower(), BOOL))
        .addEqualityGroup(
            bm(B, lower(), STRING), bm(B, lower(), STRING));
  }
}
