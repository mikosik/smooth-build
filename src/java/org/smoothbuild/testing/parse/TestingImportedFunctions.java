package org.smoothbuild.testing.parse;

import static org.smoothbuild.lang.function.FullyQualifiedName.simpleName;

import java.util.Map;

import org.smoothbuild.lang.function.Type;
import org.smoothbuild.parse.SymbolTable;
import org.smoothbuild.registry.instantiate.Function;

import com.google.common.collect.ImmutableMap;

public class TestingImportedFunctions implements SymbolTable {
  public static final String IMPORTED_NAME = "imported";

  private final Map<String, Function> map;

  public TestingImportedFunctions() {
    Function function = new Function(simpleName(IMPORTED_NAME), Type.FILE, null);
    this.map = ImmutableMap.of(IMPORTED_NAME, function);
  }

  @Override
  public boolean containsFunction(String name) {
    return map.containsKey(name);
  }

  @Override
  public Function getFunction(String name) {
    return map.get(name);
  }
}
