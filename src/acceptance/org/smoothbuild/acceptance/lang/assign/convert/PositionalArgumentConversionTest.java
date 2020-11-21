package org.smoothbuild.acceptance.lang.assign.convert;

import static org.smoothbuild.util.Strings.unlines;

import org.smoothbuild.lang.base.type.TestedAssignment;

public class PositionalArgumentConversionTest extends AbstractConversionTestCase {
  @Override
  protected String createTestScript(TestedAssignment testSpec) {
    return unlines(
        "  testFunction(" + testSpec.target.name() + " param) = param;    ",
        "  result = testFunction(" + testSpec.source.literal() + ");      ",
        testSpec.declarations()
    );
  }
}
