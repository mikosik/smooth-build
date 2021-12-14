package org.smoothbuild.acceptance.lang.convert;

import static org.smoothbuild.util.Strings.unlines;

import org.smoothbuild.lang.base.type.TestedAssignS;

public class PositionalArgConversionTest extends AbstractConversionTestCase {
  @Override
  protected String createTestScript(TestedAssignS assignment) {
    return unlines(
        "  testFunc(" + assignment.target().name() + " param) = param;    ",
        "  result = testFunc(" + assignment.source().literal() + ");      ",
        assignment.declarations()
    );
  }
}
