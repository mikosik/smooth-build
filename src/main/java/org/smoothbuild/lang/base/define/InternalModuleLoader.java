package org.smoothbuild.lang.base.define;

import static org.smoothbuild.lang.base.define.SModule.calculateModuleHash;
import static org.smoothbuild.util.Lists.list;
import static org.smoothbuild.util.Maps.toMap;

import javax.inject.Inject;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.lang.base.type.Typing;
import org.smoothbuild.lang.base.type.api.Type;

import com.google.common.collect.ImmutableMap;

public class InternalModuleLoader {
  private final Typing typing;

  @Inject
  public InternalModuleLoader(Typing typing) {
    this.typing = typing;
  }

  public SModule loadModule() {
    ModulePath modulePath = new ModulePath("internal-module");
    var types = toMap(typing.baseTypes(), Type::name, t -> new DefinedBaseType(modulePath, t));
    Hash hash = calculateModuleHash(modulePath, Hash.of(list()), list());
    return new SModule(modulePath, hash, null, list(), types, referencables(modulePath));
  }

  private ImmutableMap<String, GlobalReferencable> referencables(ModulePath modulePath) {
    Function ifFunction = new IfFunction(modulePath, typing);
    Function mapFunction = new MapFunction(modulePath, typing);
    return toMap(list(ifFunction, mapFunction), Defined::name, f -> f);
  }
}
