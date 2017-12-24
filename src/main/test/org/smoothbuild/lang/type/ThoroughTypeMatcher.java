package org.smoothbuild.lang.type;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

public class ThoroughTypeMatcher extends TypeSafeMatcher<Type> {
  private final Type type;

  public static Matcher<Type> typeMatchingThoroughly(Type type) {
    return new ThoroughTypeMatcher(type);
  }

  private ThoroughTypeMatcher(Type type) {
    this.type = type;
  }

  @Override
  public void describeTo(Description description) {
    description.appendText("is type matching '" + type.toString() + "'.");
  }

  @Override
  protected boolean matchesSafely(Type item) {
    return type.name().equals(item.name())
        && type.hash().equals(item.hash())
        && type.isNothing() == item.isNothing()
        && type.coreDepth() == item.coreDepth()
        && type.hierarchy().equals(item.hierarchy())
        && type.jType().equals(item.jType())
        && type.type().equals(type.type())
        && type.toString().equals(type.toString());
  }
}
