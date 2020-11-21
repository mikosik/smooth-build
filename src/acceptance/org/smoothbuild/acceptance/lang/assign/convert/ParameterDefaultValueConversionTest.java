package org.smoothbuild.acceptance.lang.assign.convert;

import static org.smoothbuild.util.Strings.unlines;

import org.smoothbuild.lang.base.type.TestedAssignment;

public class ParameterDefaultValueConversionTest extends AbstractConversionTestCase {
  @Override
  protected String createTestScript(TestedAssignment testSpec) {
    return unlines(
        "  fun(" + testSpec.target.name() + " param = " + testSpec.source.literal() + ") = param; ",
        "  result = fun();  ",
        testSpec.declarations()
    );
  }
}
