package org.smoothbuild.acceptance.lang.convert;

import static org.smoothbuild.util.Strings.unlines;

import org.smoothbuild.lang.base.type.TestedAssignment;

public class ValueResultConversionTest extends AbstractConversionTestCase {
  @Override
  protected String createTestScript(TestedAssignment testSpec) {
    return unlines(
        testSpec.target().name() + " value = " + testSpec.source().literal() + ";",
        "result = value;",
        testSpec.declarations()
    );
  }
}
