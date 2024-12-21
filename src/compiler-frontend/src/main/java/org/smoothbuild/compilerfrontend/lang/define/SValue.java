package org.smoothbuild.compilerfrontend.lang.define;

public sealed interface SValue extends SEvaluable permits SNamedValue {
  public default String fieldsToString() {
    return "schema = " + schema() + "\nlocation = " + location();
  }
}
