package org.smoothbuild.acceptance.lang.convert;

import static org.smoothbuild.util.Strings.unlines;

import org.smoothbuild.lang.base.type.TestedAssignment;

public class PositionalArgConversionTest extends AbstractConversionTestCase {
  @Override
  protected String createTestScript(TestedAssignment testSpec) {
    return unlines(
        "  testFunc(" + testSpec.target().name() + " param) = param;    ",
        "  result = testFunc(" + testSpec.source().literal() + ");      ",
        testSpec.declarations()
    );
  }
}
