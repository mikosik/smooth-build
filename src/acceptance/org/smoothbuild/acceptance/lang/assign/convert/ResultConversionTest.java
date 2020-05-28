package org.smoothbuild.acceptance.lang.assign.convert;

import static org.smoothbuild.util.Strings.unlines;

public class ResultConversionTest extends AbstractConversionTestCase {
  @Override
  protected String createTestScript(ConversionTestSpec testSpec) {
    return unlines(
        testSpec.target.name + " result = " + testSpec.source.literal + ";",
        testSpec.declarations()
    );
  }
}
