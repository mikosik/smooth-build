package org.smoothbuild.compile.frontend.compile;

import static org.smoothbuild.common.bindings.Bindings.immutableBindings;
import static org.smoothbuild.common.collect.Maps.toMap;
import static org.smoothbuild.compile.frontend.lang.base.location.Locations.internalLocation;

import java.util.function.Function;
import org.smoothbuild.common.tuple.Tuple0;
import org.smoothbuild.compile.frontend.lang.define.ScopeS;
import org.smoothbuild.compile.frontend.lang.define.TypeDefinitionS;
import org.smoothbuild.compile.frontend.lang.type.TypeFS;
import org.smoothbuild.compile.frontend.lang.type.TypeS;
import org.smoothbuild.out.log.LogBuffer;
import org.smoothbuild.out.log.Try;

public class LoadInternalModuleMembers implements Function<Tuple0, Try<ScopeS>> {
  @Override
  public Try<ScopeS> apply(Tuple0 unused) {
    var logBuffer = new LogBuffer();
    var types = immutableBindings(toMap(TypeFS.baseTs(), TypeS::name, t -> baseTypeDefinitions(t)));
    return Try.of(new ScopeS(types, immutableBindings()), logBuffer);
  }

  private static TypeDefinitionS baseTypeDefinitions(TypeS baseTypeS) {
    return new TypeDefinitionS(baseTypeS, internalLocation());
  }
}
