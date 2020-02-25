package org.smoothbuild.parse.deps;

import static org.smoothbuild.util.Collections.toMap;
import static org.smoothbuild.util.Lists.map;
import static org.smoothbuild.util.Maybe.error;
import static org.smoothbuild.util.Maybe.value;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

import org.smoothbuild.lang.object.db.ObjectFactory;
import org.smoothbuild.lang.runtime.Functions;
import org.smoothbuild.parse.AstVisitor;
import org.smoothbuild.parse.ast.ArrayTypeNode;
import org.smoothbuild.parse.ast.Ast;
import org.smoothbuild.parse.ast.CallNode;
import org.smoothbuild.parse.ast.FieldNode;
import org.smoothbuild.parse.ast.FuncNode;
import org.smoothbuild.parse.ast.Named;
import org.smoothbuild.parse.ast.StructNode;
import org.smoothbuild.parse.ast.TypeNode;
import org.smoothbuild.util.Maybe;

public class SortByDependencies {
  public static Maybe<List<String>> sortByDependencies(Functions functions, Ast ast) {
    List<FuncNode> funcs = ast.funcs();
    Set<String> globalNames = new HashSet<>(functions.names());
    globalNames.addAll(map(ast.structs(), structNode -> structNode.constructor().name()));
    return sortByDependencies(
        "Function call graph", funcs, funcToStackElem(), globalNames::contains);
  }

  private static Function<FuncNode, StackElem> funcToStackElem() {
    return func -> {
      Set<Named> dependencies = new HashSet<>();
      new AstVisitor() {
        @Override
        public void visitCall(CallNode call) {
          super.visitCall(call);
          dependencies.add(call);
        }
      }.visitFunc(func);
      return new StackElem(func.name(), dependencies);
    };
  }

  public static Maybe<List<String>> sortByDependencies(ObjectFactory objectFactory, Ast ast) {
    List<StructNode> structs = ast.structs();
    return sortByDependencies(
        "Type hierarchy", structs, structToStackElem(), objectFactory::containsType);
  }

  private static Function<StructNode, StackElem> structToStackElem() {
    return structNode -> {
      Set<Named> dependencies = new HashSet<>();
      new AstVisitor() {
        @Override
        public void visitField(int index, FieldNode field) {
          super.visitField(index, field);
          TypeNode type = field.type();
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
    };
  }

  private static <T extends Named> Maybe<List<String>> sortByDependencies(
      String stackName,
      List<T> nodes,
      Function<T, StackElem> newStackElem,
      Predicate<String> isAlreadyDefined) {
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
          return error(stack.createCycleError());
        } else {
          stack.push(newStackElem.apply(next));
        }
      }
    }
    return value(sorted);
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
