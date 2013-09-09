package org.smoothbuild.testing.parse;

import static org.smoothbuild.testing.function.base.TestSignature.testSignature;

import java.util.Map;
import java.util.Set;

import org.smoothbuild.function.base.Function;
import org.smoothbuild.function.plugin.PluginFunction;
import org.smoothbuild.parse.SymbolTable;

import com.google.common.collect.ImmutableMap;

public class TestImportedFunctions implements SymbolTable {
  public static final String IMPORTED_NAME = "imported";

  private final Map<String, Function> map;

  public TestImportedFunctions() {
    Function function = new PluginFunction(testSignature(IMPORTED_NAME), null);
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
