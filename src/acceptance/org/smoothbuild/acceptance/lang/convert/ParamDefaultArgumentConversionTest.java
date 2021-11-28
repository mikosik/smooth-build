package org.smoothbuild.acceptance.lang.convert;

import static org.smoothbuild.util.Strings.unlines;

import org.smoothbuild.lang.base.type.TestedAssignment;

public class ParamDefaultArgumentConversionTest extends AbstractConversionTestCase {
  @Override
  protected String createTestScript(TestedAssignment spec) {
    return unlines(
        "  fun(" + spec.target().name() + " param = " + spec.source().literal() + ") = param; ",
        "  result = fun();  ",
        spec.declarations()
    );
  }
}
