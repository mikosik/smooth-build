package org.smoothbuild.parse;

import static com.google.common.collect.ImmutableMap.toImmutableMap;
import static org.smoothbuild.parse.FindNatives.findNatives;
import static org.smoothbuild.parse.FindSemanticErrors.findSemanticErrors;
import static org.smoothbuild.parse.FunctionLoader.loadFunction;
import static org.smoothbuild.parse.InferTypesAndParamAssignment.inferTypesAndParamAssignment;
import static org.smoothbuild.parse.ModuleParser.parseModule;
import static org.smoothbuild.parse.ast.AstCreator.fromParseTree;

import java.util.HashMap;
import java.util.List;

import org.smoothbuild.antlr.lang.SmoothParser.ModuleContext;
import org.smoothbuild.cli.console.LoggerImpl;
import org.smoothbuild.lang.base.Constructor;
import org.smoothbuild.lang.base.Function;
import org.smoothbuild.lang.base.ModulePath;
import org.smoothbuild.lang.base.Parameter;
import org.smoothbuild.lang.base.Signature;
import org.smoothbuild.lang.object.db.ObjectFactory;
import org.smoothbuild.lang.object.type.Type;
import org.smoothbuild.lang.runtime.SRuntime;
import org.smoothbuild.parse.ast.Ast;
import org.smoothbuild.parse.ast.FieldNode;
import org.smoothbuild.parse.ast.FuncNode;
import org.smoothbuild.parse.ast.StructNode;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class ModuleLoader {
  public static Definitions loadModule(SRuntime runtime, Definitions imported,
      ModulePath modulePath, LoggerImpl logger) {
    Natives natives = findNatives(modulePath.nativ().path(), logger);
    if (logger.hasProblems()) {
      return Definitions.empty();
    }
    ModuleContext moduleContext = parseModule(modulePath, logger);
    if (logger.hasProblems()) {
      return Definitions.empty();
    }
    Ast ast = fromParseTree(modulePath, moduleContext);
    findSemanticErrors(imported, ast, logger);
    if (logger.hasProblems()) {
      return Definitions.empty();
    }
    Ast sortedAst = ast.sortedByDependencies(logger);
    if (logger.hasProblems()) {
      return Definitions.empty();
    }
    inferTypesAndParamAssignment(sortedAst, imported, runtime.objectFactory(), logger);
    if (logger.hasProblems()) {
      return Definitions.empty();
    }
    natives.assignNatives(sortedAst, logger);
    if (logger.hasProblems()) {
      return Definitions.empty();
    }
    var declaredFunctions = loadFunctions(imported, runtime, sortedAst, runtime.objectFactory());
    var declaredTypes = sortedAst.structs().stream()
        .map(n -> n.get(Type.class))
        .collect(toImmutableMap(Type::name, t -> t));
    return new Definitions(declaredTypes, declaredFunctions);
  }

  private static ImmutableMap<String, Function> loadFunctions(
      Definitions imported, SRuntime runtime, Ast ast, ObjectFactory objectFactory) {
    var localFunctions = new HashMap<String, Function>();
    for (StructNode struct : ast.structs()) {
      Constructor constructor = loadConstructor(struct);
      localFunctions.put(constructor.name(), constructor);
      runtime.functions().add(constructor);
    }
    for (FuncNode func : ast.funcs()) {
      Function function = loadFunction(func, imported.functions(), localFunctions, objectFactory);
      runtime.functions().add(function);
      localFunctions.put(function.name(), function);
    }
    return ImmutableMap.copyOf(localFunctions);
  }

  private static Constructor loadConstructor(StructNode struct) {
    ImmutableList.Builder<Parameter> builder = ImmutableList.builder();
    List<FieldNode> fields = struct.fields();
    for (int i = 0; i < fields.size(); i++) {
      FieldNode field = fields.get(i);
      builder.add(new Parameter(i, field.get(Type.class), field.name(), null));
    }
    ImmutableList<Parameter> parameters = builder.build();
    Signature signature =
        new Signature(struct.get(Type.class), struct.constructor().name(), parameters);
    return new Constructor(signature, struct.location());
  }
}
