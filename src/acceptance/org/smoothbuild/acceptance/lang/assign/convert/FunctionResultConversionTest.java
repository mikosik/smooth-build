package org.smoothbuild.acceptance.lang.assign.convert;

import static org.smoothbuild.util.Strings.unlines;

import org.smoothbuild.lang.base.type.TestedAssignment;

public class FunctionResultConversionTest extends AbstractConversionTestCase {
  @Override
  protected String createTestScript(TestedAssignment testSpec) {
    return unlines(
        testSpec.target().name() + " myFunction() = " + testSpec.source().literal() + ";",
        "result = myFunction();",
        testSpec.declarations()
    );
  }
}
