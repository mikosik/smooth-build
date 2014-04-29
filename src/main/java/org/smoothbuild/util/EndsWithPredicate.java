package org.smoothbuild.util;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Predicate;

public class EndsWithPredicate implements Predicate<String> {
  private final String suffix;

  public EndsWithPredicate(String suffix) {
    this.suffix = checkNotNull(suffix);
  }

  @Override
  public boolean apply(String string) {
    return string.endsWith(suffix);
  }
}
