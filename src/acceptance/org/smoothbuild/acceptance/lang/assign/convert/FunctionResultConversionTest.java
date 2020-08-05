package org.smoothbuild.acceptance.lang.assign.convert;

import static org.smoothbuild.util.Strings.unlines;

public class FunctionResultConversionTest extends AbstractConversionTestCase {
  @Override
  protected String createTestScript(ConversionTestSpec testSpec) {
    return unlines(
        testSpec.target.name + " myFunction() = " + testSpec.source.literal + ";",
        "result = myFunction();",
        testSpec.declarations()
    );
  }
}
