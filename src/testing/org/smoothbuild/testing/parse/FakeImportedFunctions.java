package org.smoothbuild.testing.parse;

import static org.mockito.Mockito.mock;
import static org.smoothbuild.function.base.Name.name;
import static org.smoothbuild.testing.function.base.FakeSignature.fakeSignature;

import org.smoothbuild.function.base.Function;
import org.smoothbuild.function.base.Name;
import org.smoothbuild.function.nativ.Invoker;
import org.smoothbuild.function.nativ.NativeFunction;
import org.smoothbuild.parse.SymbolTable;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

public class FakeImportedFunctions implements SymbolTable {
  public static final Name IMPORTED_NAME = name("imported");

  private final ImmutableMap<Name, Function> map;

  public FakeImportedFunctions() {
    Function function = new NativeFunction(fakeSignature(IMPORTED_NAME), mock(Invoker.class), true);
    this.map = ImmutableMap.of(IMPORTED_NAME, function);
  }

  @Override
  public boolean containsFunction(Name name) {
    return map.containsKey(name);
  }

  @Override
  public Function getFunction(Name name) {
    return map.get(name);
  }

  @Override
  public ImmutableSet<Name> names() {
    return map.keySet();
  }
}
