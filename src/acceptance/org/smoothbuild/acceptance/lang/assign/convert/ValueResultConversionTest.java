package org.smoothbuild.acceptance.lang.assign.convert;

import static org.smoothbuild.util.Strings.unlines;

public class ValueResultConversionTest extends AbstractConversionTestCase {
  @Override
  protected String createTestScript(ConversionTestSpec testSpec) {
    return unlines(
        testSpec.target.name() + " value = " + testSpec.source.literal() + ";",
        "result = value;",
        testSpec.declarations()
    );
  }
}
