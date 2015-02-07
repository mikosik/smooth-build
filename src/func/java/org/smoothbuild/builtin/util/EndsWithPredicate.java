package org.smoothbuild.builtin.util;

import static org.smoothbuild.builtin.util.Utils.checkNotNull;

public class EndsWithPredicate implements Predicate<String> {
  private final String suffix;

  public EndsWithPredicate(String suffix) {
    this.suffix = checkNotNull(suffix);
  }

  @Override
  public boolean test(String string) {
    return string.endsWith(suffix);
  }
}
