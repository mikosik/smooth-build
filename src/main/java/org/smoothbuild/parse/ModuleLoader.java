package org.smoothbuild.parse;

import static java.util.stream.Collectors.toSet;
import static org.smoothbuild.parse.FindNatives.findNatives;
import static org.smoothbuild.parse.FindSemanticErrors.findSemanticErrors;
import static org.smoothbuild.parse.FunctionLoader.loadFunction;
import static org.smoothbuild.parse.InferTypesAndParamAssignment.inferTypesAndParamAssignment;
import static org.smoothbuild.parse.ModuleParser.parseModule;
import static org.smoothbuild.parse.ast.AstCreator.fromParseTree;

import java.util.List;
import java.util.Set;

import org.smoothbuild.antlr.lang.SmoothParser.ModuleContext;
import org.smoothbuild.cli.console.LoggerImpl;
import org.smoothbuild.lang.base.Constructor;
import org.smoothbuild.lang.base.ModulePath;
import org.smoothbuild.lang.base.Parameter;
import org.smoothbuild.lang.base.Signature;
import org.smoothbuild.lang.object.type.Type;
import org.smoothbuild.lang.runtime.SRuntime;
import org.smoothbuild.parse.ast.Ast;
import org.smoothbuild.parse.ast.FieldNode;
import org.smoothbuild.parse.ast.FuncNode;
import org.smoothbuild.parse.ast.NamedNode;
import org.smoothbuild.parse.ast.StructNode;

import com.google.common.collect.ImmutableList;

public class ModuleLoader {
  public static Set<String> loadModule(SRuntime runtime, Set<String> declaredTypes,
      ModulePath modulePath, LoggerImpl logger) {
    Natives natives = findNatives(modulePath.nativ().path(), logger);
    if (logger.hasProblems()) {
      return Set.of();
    }
    ModuleContext moduleContext = parseModule(modulePath, logger);
    if (logger.hasProblems()) {
      return Set.of();
    }
    Ast ast = fromParseTree(modulePath, moduleContext);
    findSemanticErrors(runtime, ast, logger);
    if (logger.hasProblems()) {
      return Set.of();
    }
    ast.sortFuncsByDependencies(runtime.functions(), logger);
    if (logger.hasProblems()) {
      return Set.of();
    }
    ast.sortTypesByDependencies(declaredTypes, logger);
    if (logger.hasProblems()) {
      return Set.of();
    }
    inferTypesAndParamAssignment(runtime, ast, logger);
    if (logger.hasProblems()) {
      return Set.of();
    }
    natives.assignNatives(ast, logger);
    if (logger.hasProblems()) {
      return Set.of();
    }
    loadFunctions(runtime, ast);
    return ast.structs().stream()
        .map(NamedNode::name)
        .collect(toSet());
  }

  private static void loadFunctions(SRuntime runtime, Ast ast) {
    for (StructNode struct : ast.structs()) {
      runtime.functions().add(loadConstructor(struct));
    }
    for (FuncNode func : ast.funcs()) {
      runtime.functions().add(loadFunction(runtime, func));
    }
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
