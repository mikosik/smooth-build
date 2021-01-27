package org.smoothbuild.acceptance.lang.convert;

import static org.smoothbuild.util.Strings.unlines;

import org.smoothbuild.lang.base.type.TestedAssignment;

public class NamedArgumentConversionTest extends AbstractConversionTestCase {
  @Override
  protected String createTestScript(TestedAssignment testSpec) {
    return unlines(
        "  testFunction(" + testSpec.target().name() + " param) = param;      ",
        "  result = testFunction(param=" + testSpec.source().literal() + ");  ",
        testSpec.declarations()
    );
  }
}
