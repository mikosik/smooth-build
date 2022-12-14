package org.smoothbuild.compile.ps;

import static org.smoothbuild.compile.lang.define.ItemS.toTypes;
import static org.smoothbuild.compile.lang.type.VarSetS.varSetS;
import static org.smoothbuild.compile.ps.infer.TypeInferrer.inferStructType;
import static org.smoothbuild.out.log.Level.ERROR;
import static org.smoothbuild.out.log.Maybe.maybe;
import static org.smoothbuild.out.log.Maybe.maybeLogs;

import java.util.Optional;

import org.smoothbuild.compile.lang.define.ConstructorS;
import org.smoothbuild.compile.lang.define.DefsS;
import org.smoothbuild.compile.lang.define.ItemS;
import org.smoothbuild.compile.lang.define.ModFiles;
import org.smoothbuild.compile.lang.define.ModuleS;
import org.smoothbuild.compile.lang.define.NamedEvaluableS;
import org.smoothbuild.compile.lang.define.StructDefS;
import org.smoothbuild.compile.lang.define.TDefS;
import org.smoothbuild.compile.lang.type.FuncSchemaS;
import org.smoothbuild.compile.lang.type.FuncTS;
import org.smoothbuild.compile.lang.type.StructTS;
import org.smoothbuild.compile.ps.ast.Ast;
import org.smoothbuild.compile.ps.ast.expr.ItemP;
import org.smoothbuild.compile.ps.ast.expr.NamedFuncP;
import org.smoothbuild.compile.ps.ast.expr.NamedValueP;
import org.smoothbuild.compile.ps.ast.expr.RefableP;
import org.smoothbuild.compile.ps.ast.expr.StructP;
import org.smoothbuild.compile.ps.infer.TypeInferrer;
import org.smoothbuild.out.log.LogBuffer;
import org.smoothbuild.out.log.Maybe;
import org.smoothbuild.util.bindings.Bindings;
import org.smoothbuild.util.bindings.OptionalScopedBindings;

public class ModuleCreator {
  private final OptionalScopedBindings<TDefS> types;
  private final OptionalScopedBindings<NamedEvaluableS> bindings;
  private final LogBuffer logBuffer;
  private final PsTranslator psTranslator;

  public static Maybe<ModuleS> createModuleS(ModFiles modFiles, Ast ast, DefsS imported) {
    var logBuffer = new LogBuffer();
    var types = newOptionalScopedBindings(imported.tDefs());
    var evaluables = newOptionalScopedBindings(imported.evaluables());
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

  public static <T> OptionalScopedBindings<T> newOptionalScopedBindings(
      Bindings<? extends T> outerScopeBindings) {
    return new OptionalScopedBindings<>(outerScopeBindings.map(Optional::of));
  }

  private ModuleCreator(
      OptionalScopedBindings<TDefS> types,
      OptionalScopedBindings<NamedEvaluableS> bindings,
      LogBuffer logBuffer) {
    this.types = types;
    this.bindings = bindings;
    this.logBuffer = logBuffer;
    this.psTranslator = new PsTranslator(bindings);
  }

  public void visitStruct(StructP struct) {
    Optional<StructTS> structTS = inferStructType(types, bindings, logBuffer, struct);
    Optional<TDefS> structDefS = structTS.map(s -> new StructDefS(s, struct.location()));
    types.add(struct.name(), structDefS);
    var ctorS = structTS.map(st -> loadConstructor(struct, st));
    bindings.add(struct.constructor().name(), ctorS);
  }

  private static NamedEvaluableS loadConstructor(StructP structP, StructTS structT) {
    var constructorP = structP.constructor();
    var name = constructorP.name();
    var fieldSigs = structT.fields();
    var params = structP.fields().map(
        f -> new ItemS(fieldSigs.get(f.name()).type(), f.name(), Optional.empty(), f.location()));
    var funcTS = new FuncTS(toTypes(params), structT);
    var schema = new FuncSchemaS(varSetS(), funcTS);
    var location = structP.location();
    return new ConstructorS(schema, name, params, location);
  }

  public void visitRefable(RefableP refableP) {
    switch (refableP) {
      case NamedFuncP namedFuncP -> visitFunc(namedFuncP);
      case NamedValueP namedValueP -> visitValue(namedValueP);
      case ItemP itemP -> throw new RuntimeException("shouldn't happen");
    }
  }

  public void visitValue(NamedValueP namedValueP) {
    var typeInferrer = new TypeInferrer(types, bindings, logBuffer);
    if (typeInferrer.inferNamedValueSchema(namedValueP)) {
      var valueS = psTranslator.translateNamedValue(namedValueP);
      bindings.add(namedValueP.name(), valueS);
    } else {
      bindings.add(namedValueP.name(), Optional.empty());
    }
  }

  public void visitFunc(NamedFuncP namedFuncP) {
    var typeInferrer = new TypeInferrer(types, bindings, logBuffer);
    if (typeInferrer.inferNamedFuncSchema(namedFuncP)) {
      var funcS = psTranslator.translateNamedFunc(namedFuncP);
      @SuppressWarnings("unchecked") // safe as NamedFuncS is immutable
      var namedEvaluableS = (Optional<NamedEvaluableS>) (Object) funcS;
      bindings.add(namedFuncP.name(), namedEvaluableS);
      funcS.ifPresent(f -> f.params().forEach(this::addDefaultValueToBindings));
    } else {
      bindings.add(namedFuncP.name(), Optional.empty());
    }
  }

  private void addDefaultValueToBindings(ItemS param) {
    param.defaultValue().ifPresent(v -> bindings.add(v.name(), Optional.of(v)));
  }
}
