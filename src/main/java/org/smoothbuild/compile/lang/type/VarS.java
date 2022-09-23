package org.smoothbuild.compile.lang.type;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.util.collect.Sets.set;

import java.util.function.Function;

/**
 * Type variable.
 * This class is immutable.
 */
public final class VarS extends TypeS {
  private static final char PREFIX_SEPARATOR = '.';
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
  public TypeS mapVars(Function<VarS, TypeS> varMapper) {
    return varMapper.apply(this);
  }

  public VarS prefixed(String prefix) {
    checkArgument(!containsPrefixSeparator(prefix));
    return new VarS(prefix + PREFIX_SEPARATOR + name());
  }

  public boolean hasPrefix() {
    return containsPrefixSeparator(name());
  }

  private boolean containsPrefixSeparator(String string) {
    return string.indexOf(PREFIX_SEPARATOR) != -1;
  }
}
