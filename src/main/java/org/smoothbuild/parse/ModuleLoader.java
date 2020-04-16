package org.smoothbuild.parse;

import static org.smoothbuild.parse.FindNatives.findNatives;
import static org.smoothbuild.parse.FindSemanticErrors.findSemanticErrors;
import static org.smoothbuild.parse.FunctionLoader.loadFunction;
import static org.smoothbuild.parse.InferTypesAndParamAssignment.inferTypesAndParamAssignment;
import static org.smoothbuild.parse.ScriptParser.parseScript;
import static org.smoothbuild.parse.ast.AstCreator.fromParseTree;
import static org.smoothbuild.util.Paths.changeExtension;

import java.util.List;

import org.smoothbuild.ModulePath;
import org.smoothbuild.antlr.SmoothParser.ModuleContext;
import org.smoothbuild.cli.console.LoggerImpl;
import org.smoothbuild.lang.base.Constructor;
import org.smoothbuild.lang.base.Parameter;
import org.smoothbuild.lang.base.Signature;
import org.smoothbuild.lang.object.type.Type;
import org.smoothbuild.lang.runtime.SRuntime;
import org.smoothbuild.parse.ast.Ast;
import org.smoothbuild.parse.ast.FieldNode;
import org.smoothbuild.parse.ast.FuncNode;
import org.smoothbuild.parse.ast.StructNode;

import com.google.common.collect.ImmutableList;

public class ModuleLoader {
  public static void loadModule(SRuntime runtime, ModulePath path, LoggerImpl logger) {
    Natives natives = findNatives(changeExtension(path.fullPath(), "jar"), logger);
    if (logger.hasProblems()) {
      return;
    }
    ModuleContext moduleContext = parseScript(path, logger);
    if (logger.hasProblems()) {
      return;
    }
    Ast ast = fromParseTree(path, moduleContext);
    findSemanticErrors(runtime, ast, logger);
    if (logger.hasProblems()) {
      return;
    }
    ast.sortFuncsByDependencies(runtime.functions(), logger);
    if (logger.hasProblems()) {
      return;
    }
    ast.sortTypesByDependencies(runtime.objectFactory(), logger);
    if (logger.hasProblems()) {
      return;
    }
    inferTypesAndParamAssignment(runtime, ast, logger);
    if (logger.hasProblems()) {
      return;
    }
    natives.assignNatives(ast, logger);
    if (logger.hasProblems()) {
      return;
    }
    loadFunctions(runtime, ast);
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
