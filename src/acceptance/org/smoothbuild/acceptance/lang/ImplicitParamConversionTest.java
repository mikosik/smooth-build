package org.smoothbuild.acceptance.lang;

public class ImplicitParamConversionTest extends AbstractImplicitConversionTestCase {
  @Override
  protected String createTestScript(String type, String value) {
    return "testFunction(" + type + " param) = param;\n"
        + " result = testFunction(" + value + ");";
  }
}
