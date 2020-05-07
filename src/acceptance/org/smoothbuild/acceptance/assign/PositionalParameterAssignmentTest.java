package org.smoothbuild.acceptance.assign;

import org.smoothbuild.acceptance.AcceptanceTestCase;

public class PositionalParameterAssignmentTest extends AbstractAssignmentTestCase {
  @Override
  protected String createTestScript(String type, String value) {
    return "testFunction(" + type + " param) = param;    \n"
        + " result = testFunction(" + value + ");        \n";
  }

  @Override
  protected void thenAssignmentError(AcceptanceTestCase test, String type, String valueType) {
    test.thenSysOutContainsParseError(2,
        "In call to `testFunction`: Cannot assign argument of type '" + valueType +
        "' to parameter 'param' of type '" + type + "'.");
  }
}
