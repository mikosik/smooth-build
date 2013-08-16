package org.smoothbuild.parse;

import org.smoothbuild.registry.instantiate.Function;

public interface SymbolTable {

  public boolean containsFunction(String name);

  public Function getFunction(String name);
}
