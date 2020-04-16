package org.smoothbuild.acceptance.assign;

import org.smoothbuild.acceptance.AcceptanceTestCase;

public class NamedParameterAssignmentTest extends AbstractAssignmentTestCase {
  @Override
  protected String createTestScript(String type, String value) {
    return type + " testFunction(" + type + " param) = param;      \n"
        + type + "  result = testFunction(param=" + value + ");    \n";
  }

  @Override
  protected void thenAssignmentError(AcceptanceTestCase test, String type, String valueType) {
    test.thenSysOutContainsParseError(2, "Cannot assign argument of type '" + valueType +
        "' to parameter 'param' of type '" + type + "'.");
  }
}
