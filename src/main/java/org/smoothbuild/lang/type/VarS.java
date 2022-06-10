package org.smoothbuild.lang.type;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.util.collect.Sets.set;

import java.util.function.Function;

/**
 * Type variable.
 *
 * This class is immutable.
 */
public final class VarS extends TypeS {
  private static final String PREFIX_SEPARATOR = ".";
  private final VarSetS vars;

  public VarS(String name) {
    super(name, null);
    this.vars = new VarSetS(set(this));
  }

  @Override
  public VarSetS vars() {
    return vars;
  }

  @Override
  public TypeS mapVars(Function<VarS, VarS> varMapper) {
    return varMapper.apply(this);
  }

  public VarS prefixed(String prefix) {
    checkArgument(!prefix.contains(PREFIX_SEPARATOR));
    return new VarS(prefix + PREFIX_SEPARATOR + name());
  }

  public VarS unprefixed() {
    String name = name();
    int index = name.indexOf(PREFIX_SEPARATOR);
    if (0 <= index) {
      return new VarS(name.substring(index + 1));
    } else {
      throw new IllegalStateException("Var `" + name + "` doesn't have prefix.");
    }
  }
}
