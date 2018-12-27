package org.smoothbuild.acceptance.assign;

public class ResultAssignmentTest extends AbstractAssignmentTestCase {
  @Override
  protected String createTestScript(String type, String value) {
    return type + " result = " + value + ";";
  }

  @Override
  protected void thenAssignmentError(String target, String source) {
    thenOutputContainsError(1,
        "Function 'result' has body which type is '" + source +
            "' and it is not convertible to function's declared result type '" + target + "'.\n");
  }
}
