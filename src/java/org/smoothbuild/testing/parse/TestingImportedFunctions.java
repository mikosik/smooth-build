package org.smoothbuild.testing.parse;

import static org.smoothbuild.lang.function.FullyQualifiedName.simpleName;

import java.util.Map;
import java.util.Set;

import org.smoothbuild.function.Function;
import org.smoothbuild.function.FunctionSignature;
import org.smoothbuild.lang.function.Type;
import org.smoothbuild.parse.SymbolTable;

import com.google.common.collect.ImmutableMap;

public class TestingImportedFunctions implements SymbolTable {
  public static final String IMPORTED_NAME = "imported";

  private final Map<String, Function> map;

  public TestingImportedFunctions() {
    FunctionSignature signature = new FunctionSignature(Type.FILE, simpleName(IMPORTED_NAME), null);
    Function function = new Function(signature, null);
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

  @Override
  public Set<String> names() {
    return map.keySet();
  }
}
