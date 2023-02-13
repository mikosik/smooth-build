package org.smoothbuild.compile.fs.lang.type.tool;

import static com.google.common.truth.Truth.assertWithMessage;

import java.util.HashMap;

import org.smoothbuild.compile.fs.lang.type.TypeS;

public class AssertStructuresAreEqual {
  public static void assertStructuresAreEqual(TypeS actual, TypeS expected) {
    assertWithMessage("Structure of " + actual.q() + " and " + expected.q() + " should be equal.")
        .that(structuresAreEqual(actual, expected))
        .isTrue();
  }

  public static boolean structuresAreEqual(TypeS actual, TypeS expected) {
    var actualCanonical = toCanonicalForm(actual);
    var expectedCanonical = toCanonicalForm(expected);
    return actualCanonical.equals(expectedCanonical);
  }

  private static TypeS toCanonicalForm(TypeS typeS) {
    var tempMap = new HashMap<TypeS, TypeS>();
    var tempVarGenerator = new TempVarGenerator();
    return typeS.mapTemps((temp) -> tempMap.computeIfAbsent(temp, t -> tempVarGenerator.next()));
  }
}
