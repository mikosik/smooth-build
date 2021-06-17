package org.smoothbuild.lang.base.define;

import static com.google.common.collect.ImmutableMap.toImmutableMap;
import static org.smoothbuild.lang.base.type.Types.BASE_TYPES;
import static org.smoothbuild.util.Lists.list;

import org.smoothbuild.lang.base.type.Type;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public record SModule(
    ModulePath path,
    ModuleFiles files,
    ImmutableList<SModule> referencedModules,
    ImmutableMap<String, ? extends Defined> types,
    ImmutableMap<String, ? extends Referencable> referencables) {

  public static SModule baseTypesModule() {
    var types = BASE_TYPES.stream().collect(toImmutableMap(Type::name, BaseTypeDefinition::new));
    return new SModule(new ModulePath("internal-module"), null, list(), types, ImmutableMap.of());
  }
}
