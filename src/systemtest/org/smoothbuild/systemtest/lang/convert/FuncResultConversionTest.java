package org.smoothbuild.systemtest.lang.convert;

import static org.smoothbuild.util.Strings.unlines;

import org.smoothbuild.lang.base.type.TestedAssignS;

public class FuncResultConversionTest extends AbstractConversionTestCase {
  @Override
  protected String createTestScript(TestedAssignS assignment) {
    return unlines(
        assignment.target().name() + " myFunc() = " + assignment.source().literal() + ";",
        "result = myFunc();",
        assignment.declarations()
    );
  }
}
