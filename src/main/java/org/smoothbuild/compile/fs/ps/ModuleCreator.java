package org.smoothbuild.compile.fs.ps;

import static org.smoothbuild.compile.fs.lang.define.ScopeS.scopeS;
import static org.smoothbuild.compile.fs.lang.type.VarSetS.varSetS;
import static org.smoothbuild.out.log.Level.ERROR;
import static org.smoothbuild.out.log.Maybe.maybe;
import static org.smoothbuild.out.log.Maybe.maybeLogs;

import java.util.Optional;

import org.smoothbuild.compile.fs.lang.define.ConstructorS;
import org.smoothbuild.compile.fs.lang.define.ItemS;
import org.smoothbuild.compile.fs.lang.define.ModuleS;
import org.smoothbuild.compile.fs.lang.define.NamedEvaluableS;
import org.smoothbuild.compile.fs.lang.define.ScopeS;
import org.smoothbuild.compile.fs.lang.define.TypeDefinitionS;
import org.smoothbuild.compile.fs.lang.type.FuncSchemaS;
import org.smoothbuild.compile.fs.lang.type.FuncTS;
import org.smoothbuild.compile.fs.lang.type.StructTS;
import org.smoothbuild.compile.fs.ps.ast.expr.ItemP;
import org.smoothbuild.compile.fs.ps.ast.expr.ModuleP;
import org.smoothbuild.compile.fs.ps.ast.expr.NamedFuncP;
import org.smoothbuild.compile.fs.ps.ast.expr.NamedValueP;
import org.smoothbuild.compile.fs.ps.ast.expr.RefableP;
import org.smoothbuild.compile.fs.ps.ast.expr.StructP;
import org.smoothbuild.compile.fs.ps.infer.TypeInferrer;
import org.smoothbuild.out.log.LogBuffer;
import org.smoothbuild.out.log.Maybe;
import org.smoothbuild.util.bindings.OptionalBindings;

public class ModuleCreator {
  private final OptionalBindings<TypeDefinitionS> types;
  private final OptionalBindings<NamedEvaluableS> bindings;
  private final LogBuffer logBuffer;
  private final PsConverter psConverter;

  public static Maybe<ModuleS> createModuleS(ModuleP moduleP, ScopeS imported) {
    var logBuffer = new LogBuffer();
    var types = new OptionalBindings<>(imported.types().map(Optional::of));
    var evaluables = new OptionalBindings<>(imported.evaluables().map(Optional::of));
    var moduleCreator = new ModuleCreator(types, evaluables, logBuffer);
    moduleP.structs().forEach(moduleCreator::visitStruct);
    moduleP.evaluables().forEach(moduleCreator::visitRefable);

    if (logBuffer.containsAtLeast(ERROR)) {
      return maybeLogs(logBuffer);
    } else {
      var members = new ScopeS(
          types.innerScopeBindingsReduced(),
          evaluables.innerScopeBindingsReduced());
      var scope = scopeS(imported, members);
      var moduleS = new ModuleS(members, scope);
      return maybe(moduleS, logBuffer);
    }
  }

  private ModuleCreator(
      OptionalBindings<TypeDefinitionS> types,
      OptionalBindings<NamedEvaluableS> bindings,
      LogBuffer logBuffer) {
    this.types = types;
    this.bindings = bindings;
    this.logBuffer = logBuffer;
    this.psConverter = new PsConverter(bindings);
  }

  public void visitStruct(StructP structP) {
    var structTS = TypeInferrer.inferStructType(types, bindings, logBuffer, structP);
    var structDefinitionS = structTS.map(s -> new TypeDefinitionS(s, structP.location()));
    types.add(structP.name(), structDefinitionS);
    var constructorS = structTS.map(st -> loadConstructor(structP, st));
    bindings.add(structP.constructor().name(), constructorS);
  }

  private static NamedEvaluableS loadConstructor(StructP structP, StructTS structT) {
    var constructorP = structP.constructor();
    var name = constructorP.name();
    var fieldSigs = structT.fields();
    var params = structP.fields().map(
        f -> new ItemS(fieldSigs.get(f.name()).type(), f.name(), Optional.empty(), f.location()));
    var funcTS = new FuncTS(ItemS.toTypes(params), structT);
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
      bindings.add(namedValueP.name(), psConverter.convertNamedValue(namedValueP).map(v -> v));
    } else {
      bindings.add(namedValueP.name(), Optional.empty());
    }
  }

  public void visitFunc(NamedFuncP namedFuncP) {
    var typeInferrer = new TypeInferrer(types, bindings, logBuffer);
    if (typeInferrer.inferNamedFuncSchema(namedFuncP)) {
      var funcS = psConverter.convertNamedFunc(namedFuncP);
      @SuppressWarnings("unchecked") // safe as NamedFuncS is immutable
      var namedEvaluableS = (Optional<NamedEvaluableS>) (Object) funcS;
      bindings.add(namedFuncP.name(), namedEvaluableS);
      funcS.ifPresent(f -> f.params().forEach(this::addDefaultValueToBindings));
    } else {
      bindings.add(namedFuncP.name(), Optional.empty());
    }
  }

  private void addDefaultValueToBindings(ItemS paramS) {
    paramS.defaultValue().ifPresent(v -> bindings.add(v.name(), Optional.of(v)));
  }
}
