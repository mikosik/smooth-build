package org.smoothbuild.lang.parse;

import static com.google.common.collect.ImmutableMap.toImmutableMap;
import static java.lang.String.join;
import static java.util.Comparator.comparing;
import static org.smoothbuild.lang.base.type.Types.isVariableName;
import static org.smoothbuild.lang.parse.ParseError.parseError;
import static org.smoothbuild.lang.parse.ast.FunctionTypeNode.countFunctionVariables;
import static org.smoothbuild.util.Lists.map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.smoothbuild.cli.console.Log;
import org.smoothbuild.cli.console.Logger;
import org.smoothbuild.cli.console.MemoryLogger;
import org.smoothbuild.lang.base.define.Definitions;
import org.smoothbuild.lang.base.define.Location;
import org.smoothbuild.lang.base.like.ReferencableLike;
import org.smoothbuild.lang.parse.ast.ArrayTypeNode;
import org.smoothbuild.lang.parse.ast.Ast;
import org.smoothbuild.lang.parse.ast.AstVisitor;
import org.smoothbuild.lang.parse.ast.BlobNode;
import org.smoothbuild.lang.parse.ast.FuncNode;
import org.smoothbuild.lang.parse.ast.FunctionTypeNode;
import org.smoothbuild.lang.parse.ast.ItemNode;
import org.smoothbuild.lang.parse.ast.Named;
import org.smoothbuild.lang.parse.ast.NamedNode;
import org.smoothbuild.lang.parse.ast.RefNode;
import org.smoothbuild.lang.parse.ast.ReferencableNode;
import org.smoothbuild.lang.parse.ast.StringNode;
import org.smoothbuild.lang.parse.ast.StructNode;
import org.smoothbuild.lang.parse.ast.StructNode.ConstructorNode;
import org.smoothbuild.lang.parse.ast.TypeNode;
import org.smoothbuild.lang.parse.ast.ValueNode;
import org.smoothbuild.util.CountersMap;
import org.smoothbuild.util.DecodingHexException;
import org.smoothbuild.util.Scope;
import org.smoothbuild.util.Sets;
import org.smoothbuild.util.UnescapingFailedException;

import com.google.common.collect.ImmutableList;

public class AnalyzeSemantically {
  public static List<Log> analyzeSemantically(Definitions imported, Ast ast) {
    var logger = new MemoryLogger();
    unescapeStringLiterals(logger, ast);
    decodeBlobLiterals(logger, ast);
    resolveReferences(logger, imported, ast);
    detectUndefinedTypes(logger, imported, ast);
    detectDuplicateGlobalNames(logger, imported, ast);
    detectDuplicateFieldNames(logger, ast);
    detectDuplicateParamNames(logger, ast);
    detectStructNameWithSingleCapitalLetter(logger, ast);
    detectIllegalPolytypes(logger, ast);
    detectNativesWithBodyAndNonNativesWithoutBody(logger, ast);
    return logger.logs();
  }

  private static void unescapeStringLiterals(Logger logger, Ast ast) {
    new AstVisitor() {
      @Override
      public void visitStringLiteral(StringNode string) {
        super.visitStringLiteral(string);
        try {
          string.calculateUnescaped();
        } catch (UnescapingFailedException e) {
          logger.log(parseError(string, e.getMessage()));
        }
      }
    }.visitAst(ast);
  }

  private static void decodeBlobLiterals(Logger logger, Ast ast) {
    new AstVisitor() {
      @Override
      public void visitBlobLiteral(BlobNode blob) {
        super.visitBlobLiteral(blob);
        try {
          blob.decodeByteString();
        } catch (DecodingHexException e) {
          logger.log(parseError(blob, "Illegal Blob literal. " + e.getMessage()));
        }
      }
    }.visitAst(ast);
  }

  private static void resolveReferences(Logger logger, Definitions imported, Ast ast) {
    var importedScope = new Scope<ReferencableLike>(imported.referencables());
    Scope<ReferencableLike> localScope = new Scope<>(importedScope, ast.referencablesMap());

    new AstVisitor() {
      Scope<ReferencableLike> scope = localScope;
      @Override
      public void visitFunc(FuncNode func) {
        func.typeNode().ifPresent(this::visitType);

        var nameToParam = func.params()
            .stream()
            .collect(toImmutableMap(ReferencableLike::name, p -> p, (a, b) -> a));
        scope = new Scope<>(scope, nameToParam);
        func.expr().ifPresent(this::visitExpr);
        scope = scope.outerScope();

        visitCallable(func);
      }

      @Override
      public void visitRef(RefNode ref) {
        super.visitRef(ref);
        String name = ref.name();
        if (scope.contains(name)) {
          ref.setReferenced(scope.get(name));
        } else {
          logger.log(parseError(ref.location(), "`" + name + "` is undefined."));
        }
      }
    }.visitAst(ast);
  }

  private static void detectUndefinedTypes(Logger logger, Definitions imported, Ast ast) {
    Set<String> structNames = Sets.map(ast.structs(), NamedNode::name);
    new AstVisitor() {
      @Override
      public void visitConstructor(ConstructorNode constructor) {
        // intentionally empty to avoid calling visitParams() as synthetic constructor
        // should not be analyzed for semantic problems. Such problems are reported for
        // struct fields.
      }

      @Override
      public void visitFunc(FuncNode func) {
        super.visitFunc(func);
        func.typeNode().ifPresent(this::assertTypeIsDefined);
      }

      @Override
      public void visitValue(ValueNode value) {
        super.visitValue(value);
        value.typeNode().ifPresent(this::assertTypeIsDefined);
      }

      @Override
      public void visitParam(int index, ItemNode param) {
        param.typeNode().ifPresent(this::assertTypeIsDefined);
      }

      @Override
      public void visitField(ItemNode field) {
        field.typeNode().ifPresent(this::assertTypeIsDefined);
      }

      private void assertTypeIsDefined(TypeNode type) {
        if (type instanceof ArrayTypeNode array) {
          assertTypeIsDefined(array.elementType());
        } else if (type instanceof FunctionTypeNode function) {
          assertTypeIsDefined(function.resultType());
          function.parameterTypes().forEach(this::assertTypeIsDefined);
        } else if (!isDefinedType(type)) {
          logger.log(parseError(type.location(), "Undefined type " + type.q() + "."));
        }
      }

      private boolean isDefinedType(TypeNode type) {
        return isVariableName(type.name())
            || structNames.contains(type.name())
            || imported.types().containsKey(type.name());
      }
    }.visitAst(ast);
  }

  private static void detectDuplicateGlobalNames(Logger logger, Definitions imported, Ast ast) {
    List<Named> nameds = new ArrayList<>();
    nameds.addAll(ast.structs());
    nameds.addAll(map(ast.structs(), StructNode::constructor));
    nameds.addAll(ast.referencable());
    nameds.sort(comparing(n -> n.location().line()));

    for (Named named : nameds) {
      logIfDuplicate(logger, imported.types(), named);
      logIfDuplicate(logger, imported.referencables(), named);
    }
    Map<String, Named> checked = new HashMap<>();
    for (Named named : nameds) {
      logIfDuplicate(logger, checked, named);
      checked.put(named.name(), named);
    }
  }

  private static void logIfDuplicate(
      Logger logger, Map<String, ? extends Named> others, Named named) {
    String name = named.name();
    if (others.containsKey(name)) {
      Named otherDefinition = others.get(name);
      Location location = otherDefinition.location();
      logger.log(alreadyDefinedError(named, location));
    }
  }

  private static void detectDuplicateFieldNames(Logger logger, Ast ast) {
    new AstVisitor() {
      @Override
      public void visitFields(List<ItemNode> fields) {
        super.visitFields(fields);
        findDuplicateNames(logger, fields);
      }
    }.visitAst(ast);
  }

  private static void detectDuplicateParamNames(Logger logger, Ast ast) {
    new AstVisitor() {
      @Override
      public void visitParams(List<ItemNode> params) {
        super.visitParams(params);
        findDuplicateNames(logger, params);
      }

      @Override
      public void visitConstructor(ConstructorNode constructor) {
        // intentionally empty to avoid calling visitParams() as synthetic constructor
        // should not be analyzed for semantic problems. Such problems are reported for
        // struct fields.
      }
    }.visitAst(ast);
  }

  private static void findDuplicateNames(Logger logger, List<? extends NamedNode> nodes) {
    Map<String, Location> alreadyDefined = new HashMap<>();
    for (NamedNode named : nodes) {
      String name = named.name();
      if (alreadyDefined.containsKey(name)) {
        logger.log(alreadyDefinedError(named, alreadyDefined.get(name)));
      }
      alreadyDefined.put(name, named.location());
    }
  }

  private static void detectStructNameWithSingleCapitalLetter(Logger logger, Ast ast) {
    new AstVisitor() {
      @Override
      public void visitStruct(StructNode struct) {
        String name = struct.name();
        if (isVariableName(name)) {
          logger.log(parseError(struct.location(),
              "`" + name + "` is illegal struct name. It must have at least two characters."));
        }
      }
    }.visitAst(ast);
  }

  private static void detectIllegalPolytypes(Logger logger, Ast ast) {
    new AstVisitor() {
      @Override
      public void visitValue(ValueNode value) {
        super.visitValue(value);
        if (value.typeNode().isPresent()) {
          logErrorIfNeeded(value, value.typeNode().get().variablesUsedOnce());
        }
      }

      @Override
      public void visitFunc(FuncNode func) {
        super.visitFunc(func);
        if (func.typeNode().isPresent()) {
          var counters = new CountersMap<String>();
          countFunctionVariables(counters, func.typeNode().get(),
              map(func.params(), itemNode -> itemNode.typeNode().get()));
          logErrorIfNeeded(func, counters.keysWithCounter(1));
        }
      }

      @Override
      public void visitStruct(StructNode struct) {
        super.visitStruct(struct);
        List<ItemNode> fields = struct.fields();
        for (ItemNode field : fields) {
          logErrorIfNeeded(field, field.typeNode().get().variablesUsedOnce());
        }
      }

      private void logErrorIfNeeded(
          ReferencableNode node, ImmutableList<String> variablesUsedOnce) {
        if (!variablesUsedOnce.isEmpty()) {
          logError(node, variablesUsedOnce);
        }
      }

      private void logError(ReferencableNode node, List<String> variablesUsedOnce) {
        logger.log(parseError(node.typeNode().get(), "Type variable(s) "
            + join(", ", map(variablesUsedOnce, v -> "`" + v + "`"))
            + " are used once in declaration of " + node.q()
            + ". This means each one can be replaced with `Any`."));
      }
    }.visitAst(ast);
  }

  private static void detectNativesWithBodyAndNonNativesWithoutBody(Logger logger, Ast ast) {
    new AstVisitor() {
      @Override
      public void visitFunc(FuncNode func) {
        super.visitFunc(func);
        check(func, "function");
      }

      @Override
      public void visitValue(ValueNode value) {
        super.visitValue(value);
        check(value, "value");
      }

      private void check(ReferencableNode referencable, String referencableKind) {
        if (referencable.nativ().isPresent() && referencable.expr().isPresent()) {
          logger.log(parseError(referencable, "Native " + referencableKind + " cannot have body."));
        }
        if (referencable.nativ().isEmpty() && referencable.expr().isEmpty()) {
          logger.log(parseError(referencable,
              "Non native " + referencableKind + " cannot have empty body."));
        }
      }
    }.visitAst(ast);
  }

  private static Log alreadyDefinedError(Named named, Location location) {
    String atLocation = location.equals(Location.internal())
        ? ""
        : " at " + location;
    return parseError(
        named.location(), "`" + named.name() + "` is already defined" + atLocation + ".");
  }
}
