package org.smoothbuild.lang.base.define;

import static com.google.common.collect.ImmutableMap.toImmutableMap;
import static org.smoothbuild.lang.base.define.Location.internal;
import static org.smoothbuild.lang.base.define.SModule.calculateModuleHash;
import static org.smoothbuild.lang.base.type.Types.BASE_TYPES;
import static org.smoothbuild.lang.base.type.Types.bool;
import static org.smoothbuild.util.Lists.list;

import java.util.Optional;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.lang.base.type.Type;
import org.smoothbuild.lang.base.type.Variable;

import com.google.common.collect.ImmutableMap;

public class InternalModule {
  public static final String IF_FUNCTION_NAME = "if";

  public static SModule internalModule() {
    ModulePath modulePath = new ModulePath("internal-module");
    var types = BASE_TYPES.stream()
        .collect(toImmutableMap(Type::name, t -> new BaseTypeDefinition(modulePath, t)));
    Hash hash = calculateModuleHash(modulePath, Hash.of(list()), list());
    return new SModule(modulePath, hash, null, list(), types, referencables(modulePath));
  }

  private static ImmutableMap<String, Referencable> referencables(ModulePath modulePath) {
    RealFunction ifFunction = ifFunction(modulePath);
    return ImmutableMap.of(ifFunction.name(), ifFunction);
  }

  private static RealFunction ifFunction(ModulePath modulePath) {
    Variable a = new Variable("A");
    var parameters = list(
        parameter(bool(), "condition"),
        parameter(a, "then"),
        parameter(a, "else"));
    return new RealFunction(a, modulePath, IF_FUNCTION_NAME, parameters, null, internal());
  }

  private static Item parameter(Type type, String name) {
    return new Item(type, name, Optional.empty());
  }
}
