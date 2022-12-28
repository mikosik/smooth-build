package org.smoothbuild.compile.ps;

import static org.smoothbuild.compile.lang.define.ItemS.toTypes;
import static org.smoothbuild.compile.lang.type.VarSetS.varSetS;
import static org.smoothbuild.compile.ps.infer.TypeInferrer.inferStructType;
import static org.smoothbuild.out.log.Level.ERROR;
import static org.smoothbuild.out.log.Maybe.maybe;
import static org.smoothbuild.out.log.Maybe.maybeLogs;

import java.util.Optional;

import org.smoothbuild.compile.lang.define.ConstructorS;
import org.smoothbuild.compile.lang.define.DefinitionsS;
import org.smoothbuild.compile.lang.define.ItemS;
import org.smoothbuild.compile.lang.define.ModFiles;
import org.smoothbuild.compile.lang.define.ModuleS;
import org.smoothbuild.compile.lang.define.NamedEvaluableS;
import org.smoothbuild.compile.lang.define.TypeDefinitionS;
import org.smoothbuild.compile.lang.type.FuncSchemaS;
import org.smoothbuild.compile.lang.type.FuncTS;
import org.smoothbuild.compile.lang.type.StructTS;
import org.smoothbuild.compile.ps.ast.expr.ItemP;
import org.smoothbuild.compile.ps.ast.expr.ModuleP;
import org.smoothbuild.compile.ps.ast.expr.NamedFuncP;
import org.smoothbuild.compile.ps.ast.expr.NamedValueP;
import org.smoothbuild.compile.ps.ast.expr.RefableP;
import org.smoothbuild.compile.ps.ast.expr.StructP;
import org.smoothbuild.compile.ps.infer.TypeInferrer;
import org.smoothbuild.out.log.LogBuffer;
import org.smoothbuild.out.log.Maybe;
import org.smoothbuild.util.bindings.OptionalBindings;

public class ModuleCreator {
  private final OptionalBindings<TypeDefinitionS> types;
  private final OptionalBindings<NamedEvaluableS> bindings;
  private final LogBuffer logBuffer;
  private final PsTranslator psTranslator;

  public static Maybe<ModuleS> createModuleS(
      ModFiles modFiles, ModuleP moduleP, DefinitionsS imported) {
    var logBuffer = new LogBuffer();
    var types = new OptionalBindings<>(imported.types().map(Optional::of));
    var evaluables = new OptionalBindings<>(imported.evaluables().map(Optional::of));
    var moduleCreator = new ModuleCreator(types, evaluables, logBuffer);
    moduleP.structs().forEach(moduleCreator::visitStruct);
    moduleP.evaluables().forEach(moduleCreator::visitRefable);

    if (logBuffer.containsAtLeast(ERROR)) {
      return maybeLogs(logBuffer);
    } else {
      var typeBindings = types.innerScopeBindingsReduced();
      var evaluableBindings = evaluables.innerScopeBindingsReduced();
      var modS = new ModuleS(modFiles, typeBindings, evaluableBindings);
      return maybe(modS, logBuffer);
    }
  }

  private ModuleCreator(
      OptionalBindings<TypeDefinitionS> types,
      OptionalBindings<NamedEvaluableS> bindings,
      LogBuffer logBuffer) {
    this.types = types;
    this.bindings = bindings;
    this.logBuffer = logBuffer;
    this.psTranslator = new PsTranslator(bindings);
  }

  public void visitStruct(StructP structP) {
    var structTS = inferStructType(types, bindings, logBuffer, structP);
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
      funcS.ifPresent(f -> f.params()
          .forEach(paramS -> addDefaultValueToBindings(f.name(), paramS)));
    } else {
      bindings.add(namedFuncP.name(), Optional.empty());
    }
  }

  private void addDefaultValueToBindings(String name, ItemS paramS) {
    paramS.defaultValue().ifPresent(v -> bindings.add(name + ":" + v.name(), Optional.of(v)));
  }
}
