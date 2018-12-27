package org.smoothbuild.acceptance.assign;

public class PositionalParameterAssignmentTest extends AbstractAssignmentTestCase {
  @Override
  protected String createTestScript(String type, String value) {
    return "testFunction(" + type + " param) = param;    \n"
        + " result = testFunction(" + value + ");        \n";
  }

  @Override
  protected void thenAssignmentError(String target, String source) {
    thenOutputContainsError(2, "Cannot assign argument of type '" + source +
        "' to parameter 'param' of type '" + target + "'.");
  }
}
