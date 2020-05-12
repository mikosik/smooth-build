package org.smoothbuild.slib.assign;

import org.smoothbuild.slib.AcceptanceTestCase;

public class ResultAssignmentTest extends AbstractAssignmentTestCase {
  @Override
  protected String createTestScript(String type, String value) {
    return type + " result = " + value + ";";
  }

  @Override
  protected void thenAssignmentError(AcceptanceTestCase test, String type, String valueType) {
    test.thenSysOutContainsParseError(1,
        "Function 'result' has body which type is '" + valueType +
            "' and it is not convertible to function's declared result type '" + type + "'.");
  }
}
