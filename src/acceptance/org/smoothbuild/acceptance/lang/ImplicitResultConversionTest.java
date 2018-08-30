package org.smoothbuild.acceptance.lang;

public class ImplicitResultConversionTest extends AbstractImplicitConversionTestCase {
  @Override
  protected String createTestScript(String type, String value) {
    return type + " result = " + value + ";";
  }
}
