package org.smoothbuild.testing.parse;

import static org.mockito.Mockito.mock;
import static org.smoothbuild.testing.function.base.FakeSignature.testSignature;

import java.util.Map;
import java.util.Set;

import org.smoothbuild.function.base.Function;
import org.smoothbuild.function.nativ.Invoker;
import org.smoothbuild.function.nativ.NativeFunction;
import org.smoothbuild.object.ResultDb;
import org.smoothbuild.parse.SymbolTable;

import com.google.common.collect.ImmutableMap;

public class FakeImportedFunctions implements SymbolTable {
  public static final String IMPORTED_NAME = "imported";

  private final Map<String, Function> map;

  public FakeImportedFunctions() {
    Function function = new NativeFunction(mock(ResultDb.class), testSignature(IMPORTED_NAME),
        mock(Invoker.class));
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
