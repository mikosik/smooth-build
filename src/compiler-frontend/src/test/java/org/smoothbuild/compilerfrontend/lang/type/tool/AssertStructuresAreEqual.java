package org.smoothbuild.compilerfrontend.lang.type.tool;

import static com.google.common.truth.Truth.assertWithMessage;

import java.util.HashMap;
import org.smoothbuild.compilerfrontend.lang.type.SType;

public class AssertStructuresAreEqual {
  public static void assertStructuresAreEqual(SType actual, SType expected) {
    assertWithMessage("Structure of " + actual.q() + " and " + expected.q() + " should be equal.")
        .that(structuresAreEqual(actual, expected))
        .isTrue();
  }

  public static boolean structuresAreEqual(SType actual, SType expected) {
    var actualCanonical = toCanonicalForm(actual);
    var expectedCanonical = toCanonicalForm(expected);
    return actualCanonical.equals(expectedCanonical);
  }

  private static SType toCanonicalForm(SType sType) {
    var tempMap = new HashMap<SType, SType>();
    var tempVarGenerator = new TempVarGenerator();
    return sType.mapTemps((temp) -> tempMap.computeIfAbsent(temp, t -> tempVarGenerator.next()));
  }
}
