package org.smoothbuild.compile.ps;

import static java.util.Optional.empty;
import static org.smoothbuild.compile.lang.define.ItemS.toTypes;
import static org.smoothbuild.compile.ps.infer.TypeInferrer.inferStructType;
import static org.smoothbuild.out.log.Level.ERROR;
import static org.smoothbuild.out.log.Maybe.maybe;
import static org.smoothbuild.out.log.Maybe.maybeLogs;

import java.util.Optional;

import org.smoothbuild.compile.lang.define.DefsS;
import org.smoothbuild.compile.lang.define.ItemS;
import org.smoothbuild.compile.lang.define.ModFiles;
import org.smoothbuild.compile.lang.define.ModuleS;
import org.smoothbuild.compile.lang.define.NamedEvaluableS;
import org.smoothbuild.compile.lang.define.StructDefS;
import org.smoothbuild.compile.lang.define.SyntCtorS;
import org.smoothbuild.compile.lang.define.TDefS;
import org.smoothbuild.compile.lang.type.FuncSchemaS;
import org.smoothbuild.compile.lang.type.FuncTS;
import org.smoothbuild.compile.lang.type.StructTS;
import org.smoothbuild.compile.ps.ast.Ast;
import org.smoothbuild.compile.ps.ast.StructP;
import org.smoothbuild.compile.ps.ast.refable.ItemP;
import org.smoothbuild.compile.ps.ast.refable.NamedFuncP;
import org.smoothbuild.compile.ps.ast.refable.NamedValueP;
import org.smoothbuild.compile.ps.ast.refable.RefableP;
import org.smoothbuild.compile.ps.infer.TypeInferrer;
import org.smoothbuild.out.log.LogBuffer;
import org.smoothbuild.out.log.Maybe;
import org.smoothbuild.util.bindings.Bindings;
import org.smoothbuild.util.bindings.ImmutableBindings;
import org.smoothbuild.util.bindings.OptionalScopedBindings;
import org.smoothbuild.util.bindings.ScopedBindings;

public class ModuleCreator {
  private final ScopedBindings<Optional<TDefS>> types;
  private final ScopedBindings<Optional<NamedEvaluableS>> bindings;
  private final LogBuffer logBuffer;
  private final PsTranslator psTranslator;

  public static Maybe<ModuleS> createModuleS(ModFiles modFiles, Ast ast, DefsS imported) {
    var logBuffer = new LogBuffer();
    var types = newOptionalMutableBindings(imported.tDefs());
    var evaluables = newOptionalMutableBindings(imported.evaluables());
    var moduleCreator = new ModuleCreator(types, evaluables, logBuffer);
    ast.structs().forEach(moduleCreator::visitStruct);
    ast.evaluables().forEach(moduleCreator::visitRefable);

    if (logBuffer.containsAtLeast(ERROR)) {
      return maybeLogs(logBuffer);
    } else {
      var modS = new ModuleS(modFiles, types.innerScopeBindings(), evaluables.innerScopeBindings());
      return maybe(modS, logBuffer);
    }
  }

  private static <T> OptionalScopedBindings<T> newOptionalMutableBindings(Bindings<T> bindings) {
    return new OptionalScopedBindings<>(bindings.map(Optional::of));
  }

  private ModuleCreator(ScopedBindings<Optional<TDefS>> types,
      ScopedBindings<Optional<NamedEvaluableS>> bindings, LogBuffer logBuffer) {
    this.types = types;
    this.bindings = bindings;
    this.logBuffer = logBuffer;
    this.psTranslator = new PsTranslator(bindings);
  }

  public void visitStruct(StructP struct) {
    Optional<StructTS> structTS = inferStructType(types, bindings, logBuffer, struct);
    Optional<TDefS> structDefS = structTS.map(s -> new StructDefS(s, struct.loc()));
    types.add(struct.name(), structDefS);
    var ctorS = structTS.map(st -> loadSyntCtor(struct, st));
    bindings.add(struct.ctor().name(), ctorS);
  }

  private static NamedEvaluableS loadSyntCtor(StructP structP, StructTS structT) {
    var ctorP = structP.ctor();
    var name = ctorP.name();
    var fieldSigs = structT.fields();
    var params = structP.fields().map(
        f -> new ItemS(fieldSigs.get(f.name()).type(), f.name(), empty(), f.loc()));
    var funcTS = new FuncTS(toTypes(params), structT);
    var schema = new FuncSchemaS(funcTS);
    var loc = structP.loc();
    return new SyntCtorS(schema, name, params, loc);
  }

  public void visitRefable(RefableP refableP) {
    switch (refableP) {
      case NamedFuncP namedFuncP -> visitFunc(namedFuncP);
      case NamedValueP namedValueP -> visitValue(namedValueP);
      case ItemP itemP -> throw new RuntimeException("shouldn't happen");
    }
  }

  public void visitValue(NamedValueP namedValueP) {
    var valS = new TypeInferrer(types, bindings, logBuffer)
        .inferValueSchema(namedValueP)
        .flatMap(s -> psTranslator.translateValue(namedValueP, s.type()));
    bindings.add(namedValueP.name(), valS);
  }

  public void visitFunc(NamedFuncP namedFuncP) {
    var funcS = new TypeInferrer(types, bindings, logBuffer)
        .inferFuncSchema(namedFuncP)
        .flatMap(s -> psTranslator.translateFunc(namedFuncP, s.type()));
    bindings.add(namedFuncP.name(), funcS);
  }
}
