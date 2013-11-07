package org.smoothbuild.parse;

import org.smoothbuild.function.base.Function;
import org.smoothbuild.function.base.Name;

import com.google.common.collect.ImmutableSet;

public interface SymbolTable {

  public boolean containsFunction(Name name);

  public Function getFunction(Name name);

  public ImmutableSet<Name> names();
}
