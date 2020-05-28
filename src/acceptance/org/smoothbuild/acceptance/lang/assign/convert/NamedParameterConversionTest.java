package org.smoothbuild.acceptance.lang.assign.convert;

import static org.smoothbuild.util.Strings.unlines;

public class NamedParameterConversionTest extends AbstractConversionTestCase {
  @Override
  protected String createTestScript(ConversionTestSpec testSpec) {
    return unlines(
        "  testFunction(" + testSpec.target.name + " param) = param;      ",
        "  result = testFunction(param=" + testSpec.source.literal + ");  ",
        testSpec.declarations()
    );
  }
}
