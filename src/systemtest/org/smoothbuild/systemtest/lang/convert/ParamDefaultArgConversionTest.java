package org.smoothbuild.systemtest.lang.convert;

import static org.smoothbuild.util.Strings.unlines;

import org.smoothbuild.lang.base.type.TestedAssignS;

public class ParamDefaultArgConversionTest extends AbstractConversionTestCase {
  @Override
  protected String createTestScript(TestedAssignS assignment) {
    return unlines(
        "  fun(" + assignment.target().name() + " param = " + assignment.source().literal() + ") = param; ",
        "  result = fun();  ",
        assignment.declarations()
    );
  }
}
