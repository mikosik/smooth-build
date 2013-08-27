package org.smoothbuild.parse;

import java.util.Set;

import org.smoothbuild.function.base.Function;

public interface SymbolTable {

  public boolean containsFunction(String name);

  public Function getFunction(String name);

  public Set<String> names();
}
