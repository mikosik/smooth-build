package org.smoothbuild.acceptance.lang.assign.nongeneric;

import static org.smoothbuild.util.Strings.unlines;

public class FunctionResultAssignmentTest extends AbstractAssignmentTestCase {
  @Override
  protected String createTestScript(AssignmentTestSpec testSpec) {
    return unlines(
        testSpec.target.name + " myFunction() = " + testSpec.source.literal + ";",
        "result = myFunction();",
        testSpec.declarations()
    );
  }

  @Override
  protected void assertAssignmentError(String targetType, String sourceType) {
    assertSysOutContainsParseError(1,
        "`myFunction` has body which type is '" + sourceType +
            "' and it is not convertible to its declared type '" + targetType + "'.");
  }
}
