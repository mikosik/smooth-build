package org.smoothbuild.acceptance.lang.convert;

import static org.smoothbuild.util.Strings.unlines;

import org.smoothbuild.lang.base.type.TestedAssignment;

public class FuncResultConversionTest extends AbstractConversionTestCase {
  @Override
  protected String createTestScript(TestedAssignment testSpec) {
    return unlines(
        testSpec.target().name() + " myFunc() = " + testSpec.source().literal() + ";",
        "result = myFunc();",
        testSpec.declarations()
    );
  }
}
