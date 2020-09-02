package org.smoothbuild.acceptance.lang.assign.nongeneric;

import static org.smoothbuild.util.Strings.unlines;

public class ValueResultAssignmentTest extends AbstractAssignmentTestCase {
  @Override
  protected String createTestScript(AssignmentTestSpec testSpec) {
    return unlines(
        testSpec.target.name + " result = " + testSpec.source.literal + ";",
        testSpec.declarations()
    );
  }

  @Override
  protected void assertAssignmentError(String targetType, String sourceType) {
    assertSysOutContainsParseError(1,
        "`result` has body which type is '" + sourceType +
            "' and it is not convertible to its declared type '" + targetType + "'.");
  }
}
