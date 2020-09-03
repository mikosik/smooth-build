package org.smoothbuild.acceptance.lang.assign.nongeneric;

import static org.smoothbuild.util.Strings.unlines;

public class ParameterDefaultValueAssignmentTest extends AbstractAssignmentTestCase {
  @Override
  protected String createTestScript(AssignmentTestSpec testSpec) {
    return unlines(
        "  fun(" + testSpec.target.name + " param = " + testSpec.source.literal + ") = param; ",
        "  result = fun();  ",
        testSpec.declarations()
    );
  }

  @Override
  protected void assertAssignmentError(String targetType, String sourceType) {
    assertSysOutContainsParseError(1,
        "Parameter 'param' is of type '" + targetType
            + "' so it cannot have default value of type '" + sourceType + "'.");
  }
}
