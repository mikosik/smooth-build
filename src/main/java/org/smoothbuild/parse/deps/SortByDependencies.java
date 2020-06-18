package org.smoothbuild.parse.deps;

import static org.smoothbuild.util.Collections.toMap;
import static org.smoothbuild.util.Lists.map;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

import org.smoothbuild.cli.console.Logger;
import org.smoothbuild.lang.base.Function;
import org.smoothbuild.lang.object.type.Type;
import org.smoothbuild.parse.AstVisitor;
import org.smoothbuild.parse.ast.ArrayTypeNode;
import org.smoothbuild.parse.ast.Ast;
import org.smoothbuild.parse.ast.CallNode;
import org.smoothbuild.parse.ast.FieldNode;
import org.smoothbuild.parse.ast.FuncNode;
import org.smoothbuild.parse.ast.Named;
import org.smoothbuild.parse.ast.StructNode;
import org.smoothbuild.parse.ast.TypeNode;

import com.google.common.collect.ImmutableMap;

public class SortByDependencies {
  public static List<String> sortFunctionsByDependencies(
      ImmutableMap<String, Function> functions, Ast ast, Logger logger) {
    List<FuncNode> funcs = ast.funcs();
    Set<String> globalNames = new HashSet<>(functions.keySet());
    globalNames.addAll(map(ast.structs(), structNode -> structNode.constructor().name()));
    return sortTypesByDependencies("Function call graph", funcs,
        SortByDependencies::funcToStackElem, globalNames::contains, logger);
  }

  private static StackElem funcToStackElem(FuncNode func) {
    Set<Named> dependencies = new HashSet<>();
    new AstVisitor() {
      @Override
      public void visitCall(CallNode call) {
        super.visitCall(call);
        dependencies.add(call);
      }
    }.visitFunc(func);
    return new StackElem(func.name(), dependencies);
  }

  public static List<String> sortTypesByDependencies(
      ImmutableMap<String, Type> types, Ast ast, Logger logger) {
    List<StructNode> structs = ast.structs();
    return sortTypesByDependencies("Type hierarchy", structs, SortByDependencies::structToStackElem,
        types::containsKey, logger);
  }

  private static StackElem structToStackElem(StructNode structNode) {
    Set<Named> dependencies = new HashSet<>();
    new AstVisitor() {
      @Override
      public void visitField(int index, FieldNode field) {
        super.visitField(index, field);
        TypeNode type = field.typeNode();
        addToDependencies(type);
      }

      private void addToDependencies(TypeNode type) {
        if (type.isArray()) {
          addToDependencies(((ArrayTypeNode) type).elementType());
        } else {
          dependencies.add(type);
        }
      }
    }.visitStruct(structNode);
    return new StackElem(structNode.name(), dependencies);
  }

  private static <T extends Named> List<String> sortTypesByDependencies(
      String stackName,
      List<T> nodes,
      java.util.function.Function<T, StackElem> newStackElem,
      Predicate<String> isAlreadyDefined,
      Logger logger) {
    Map<String, T> notSorted = toMap(nodes, Named::name);
    List<String> sorted = new ArrayList<>(nodes.size());
    DependencyStack stack = new DependencyStack(stackName);
    while (!notSorted.isEmpty() || !stack.isEmpty()) {
      if (stack.isEmpty()) {
        T named = notSorted.remove(notSorted.keySet().iterator().next());
        stack.push(newStackElem.apply(named));
      }
      StackElem topElem = stack.peek();
      Named missing = findNotYetProcessedDependency(
          isAlreadyDefined, sorted, topElem.dependencies());
      if (missing == null) {
        sorted.add(stack.pop().name());
      } else {
        topElem.setMissing(missing);
        T next = notSorted.remove(missing.name());
        if (next == null) {
          logger.log(stack.createCycleError());
          return null;
        } else {
          stack.push(newStackElem.apply(next));
        }
      }
    }
    return sorted;
  }

  private static Named findNotYetProcessedDependency(
      Predicate<String> isAlreadyDefined, List<String> sorted, Set<Named> dependencies) {
    for (Named dependency : dependencies) {
      String name = dependency.name();
      if (!(sorted.contains(name) || isAlreadyDefined.test(name))) {
        return dependency;
      }
    }
    return null;
  }
}
