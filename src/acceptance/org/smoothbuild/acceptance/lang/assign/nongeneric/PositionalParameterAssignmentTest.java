package org.smoothbuild.acceptance.lang.assign.nongeneric;

import static org.smoothbuild.util.Strings.unlines;

public class PositionalParameterAssignmentTest extends AbstractAssignmentTestCase {
  @Override
  protected String createTestScript(AssignmentTestSpec testSpec) {
    return unlines(
        "  testFunction(" + testSpec.target.name + " param) = param;     ",
        "  result = testFunction(" + testSpec.source.literal + ");       ",
        testSpec.declarations()
    );
  }

  @Override
  protected void assertAssignmentError(String targetType, String sourceType) {
    thenSysOutContainsParseError(2,
        "In call to `testFunction`: Cannot assign argument of type '" + sourceType +
        "' to parameter 'param' of type '" + targetType + "'.");
  }
}
