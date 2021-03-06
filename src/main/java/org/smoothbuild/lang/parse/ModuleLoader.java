package org.smoothbuild.lang.parse;

import static com.google.common.collect.ImmutableMap.toImmutableMap;
import static org.smoothbuild.lang.parse.FindSemanticErrors.findSemanticErrors;
import static org.smoothbuild.lang.parse.FunctionLoader.loadFunction;
import static org.smoothbuild.lang.parse.InferTypesAndParamAssignments.inferTypesAndParamAssignment;
import static org.smoothbuild.lang.parse.ast.AstCreator.fromParseTree;

import java.util.HashMap;
import java.util.List;

import org.smoothbuild.antlr.lang.SmoothParser.ModuleContext;
import org.smoothbuild.cli.console.LoggerImpl;
import org.smoothbuild.lang.base.Callable;
import org.smoothbuild.lang.base.Constructor;
import org.smoothbuild.lang.base.ModulePath;
import org.smoothbuild.lang.base.Parameter;
import org.smoothbuild.lang.base.Signature;
import org.smoothbuild.lang.base.type.Type;
import org.smoothbuild.lang.parse.ast.Ast;
import org.smoothbuild.lang.parse.ast.FuncNode;
import org.smoothbuild.lang.parse.ast.ItemNode;
import org.smoothbuild.lang.parse.ast.StructNode;
import org.smoothbuild.nativ.Natives;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class ModuleLoader {
  public static Definitions loadModule(Definitions imported, ModulePath modulePath,
      LoggerImpl logger) {
    Natives natives = FindNatives.findNatives(modulePath.nativ().path(), logger);
    if (logger.hasProblems()) {
      return Definitions.empty();
    }
    ModuleContext moduleContext = ModuleParser.parseModule(modulePath, logger);
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
    inferTypesAndParamAssignment(sortedAst, imported, logger);
    if (logger.hasProblems()) {
      return Definitions.empty();
    }
    natives.assignNatives(sortedAst, logger);
    if (logger.hasProblems()) {
      return Definitions.empty();
    }
    var declaredFunctions = loadFunctions(imported, sortedAst);
    var declaredTypes = sortedAst.structs().stream()
        .map(structNode -> structNode.type().get())
        .collect(toImmutableMap(Type::name, t -> t));
    return new Definitions(declaredTypes, declaredFunctions);
  }

  private static ImmutableMap<String, Callable> loadFunctions(Definitions imported, Ast ast) {
    var localFunctions = new HashMap<String, Callable>();
    for (StructNode struct : ast.structs()) {
      Constructor constructor = loadConstructor(struct);
      localFunctions.put(constructor.name(), constructor);
    }
    for (FuncNode func : ast.funcs()) {
      Callable function = loadFunction(func, imported.callables(), localFunctions);
      localFunctions.put(function.name(), function);
    }
    return ImmutableMap.copyOf(localFunctions);
  }

  private static Constructor loadConstructor(StructNode struct) {
    ImmutableList.Builder<Parameter> builder = ImmutableList.builder();
    List<ItemNode> fields = struct.fields();
    for (int i = 0; i < fields.size(); i++) {
      ItemNode field = fields.get(i);
      builder.add(new Parameter(i, field.type().get(), field.name(), null, field.location()));
    }
    ImmutableList<Parameter> parameters = builder.build();
    Signature signature =
        new Signature(struct.type().get(), struct.constructor().name(), parameters);
    return new Constructor(signature, struct.location());
  }
}
