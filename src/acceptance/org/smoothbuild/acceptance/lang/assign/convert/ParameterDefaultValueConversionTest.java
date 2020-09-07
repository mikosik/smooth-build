package org.smoothbuild.acceptance.lang.assign.convert;

import static org.smoothbuild.util.Strings.unlines;

public class ParameterDefaultValueConversionTest extends AbstractConversionTestCase {
  @Override
  protected String createTestScript(ConversionTestSpec testSpec) {
    return unlines(
        "  fun(" + testSpec.target.name() + " param = " + testSpec.source.literal() + ") = param; ",
        "  result = fun();  ",
        testSpec.declarations()
    );
  }
}
