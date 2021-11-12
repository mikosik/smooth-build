package org.smoothbuild.lang.parse;

import static java.lang.String.join;
import static java.util.Comparator.comparing;
import static org.smoothbuild.lang.base.type.api.TypeNames.isVariableName;
import static org.smoothbuild.lang.parse.ParseError.parseError;
import static org.smoothbuild.lang.parse.ast.FunctionTypeNode.countFunctionVariables;
import static org.smoothbuild.util.collect.Lists.map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.smoothbuild.cli.console.ImmutableLogs;
import org.smoothbuild.cli.console.Log;
import org.smoothbuild.cli.console.LogBuffer;
import org.smoothbuild.cli.console.Logger;
import org.smoothbuild.lang.base.define.Definitions;
import org.smoothbuild.lang.base.define.Location;
import org.smoothbuild.lang.base.define.Nal;
import org.smoothbuild.lang.parse.ast.ArrayTypeNode;
import org.smoothbuild.lang.parse.ast.Ast;
import org.smoothbuild.lang.parse.ast.AstVisitor;
import org.smoothbuild.lang.parse.ast.BlobNode;
import org.smoothbuild.lang.parse.ast.FunctionTypeNode;
import org.smoothbuild.lang.parse.ast.IntNode;
import org.smoothbuild.lang.parse.ast.ItemNode;
import org.smoothbuild.lang.parse.ast.NamedNode;
import org.smoothbuild.lang.parse.ast.RealFuncNode;
import org.smoothbuild.lang.parse.ast.ReferencableNode;
import org.smoothbuild.lang.parse.ast.StringNode;
import org.smoothbuild.lang.parse.ast.StructNode;
import org.smoothbuild.lang.parse.ast.StructNode.ConstructorNode;
import org.smoothbuild.lang.parse.ast.TypeNode;
import org.smoothbuild.lang.parse.ast.ValueNode;
import org.smoothbuild.util.DecodeHexException;
import org.smoothbuild.util.UnescapingFailedException;
import org.smoothbuild.util.collect.CountersMap;
import org.smoothbuild.util.collect.NamedList;
import org.smoothbuild.util.collect.Sets;

import com.google.common.collect.ImmutableList;

public class AnalyzeSemantically {
  public static ImmutableLogs analyzeSemantically(Definitions imported, Ast ast) {
    var logBuffer = new LogBuffer();
    decodeBlobLiterals(logBuffer, ast);
    decodeIntLiterals(logBuffer, ast);
    decodeStringLiterals(logBuffer, ast);
    detectUndefinedTypes(logBuffer, imported, ast);
    detectDuplicateGlobalNames(logBuffer, imported, ast);
    detectDuplicateFieldNames(logBuffer, ast);
    detectDuplicateParamNames(logBuffer, ast);
    detectStructNameWithSingleCapitalLetter(logBuffer, ast);
    detectIllegalPolytypes(logBuffer, ast);
    detectNativesWithBodyAndNonNativesWithoutBody(logBuffer, ast);
    return logBuffer.toImmutableLogs();
  }

  private static void decodeBlobLiterals(Logger logger, Ast ast) {
    new AstVisitor() {
      @Override
      public void visitBlobLiteral(BlobNode blob) {
        super.visitBlobLiteral(blob);
        try {
          blob.decodeByteString();
        } catch (DecodeHexException e) {
          logger.log(parseError(blob, "Illegal Blob literal. " + e.getMessage()));
        }
      }
    }.visitAst(ast);
  }

  private static void decodeIntLiterals(Logger logger, Ast ast) {
    new AstVisitor() {
      @Override
      public void visitIntLiteral(IntNode intNode) {
        super.visitIntLiteral(intNode);
        try {
          intNode.decodeBigInteger();
        } catch (NumberFormatException e) {
          logger.log(parseError(intNode, "Illegal Int literal: `" + intNode.literal() + "`."));
        }
      }
    }.visitAst(ast);
  }

  private static void decodeStringLiterals(Logger logger, Ast ast) {
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
      public void visitRealFunc(RealFuncNode func) {
        super.visitRealFunc(func);
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
          logger.log(parseError(type.location(), type.q() + " type is undefined."));
        }
      }

      private boolean isDefinedType(TypeNode type) {
        return isVariableName(type.name())
            || structNames.contains(type.name())
            || imported.types().containsName(type.name());
      }
    }.visitAst(ast);
  }

  private static void detectDuplicateGlobalNames(Logger logger, Definitions imported, Ast ast) {
    List<Nal> nals = new ArrayList<>();
    nals.addAll(ast.structs());
    nals.addAll(map(ast.structs(), StructNode::constructor));
    nals.addAll(ast.referencables());
    nals.sort(comparing(n -> n.location().line()));

    for (Nal nal : nals) {
      logIfDuplicate(logger, imported.types(), nal);
      logIfDuplicate(logger, imported.referencables(), nal);
    }
    Map<String, Nal> checked = new HashMap<>();
    for (Nal nal : nals) {
      logIfDuplicate(logger, checked, nal);
      checked.put(nal.name(), nal);
    }
  }

  private static void logIfDuplicate(Logger logger, NamedList<? extends Nal> others, Nal nal) {
    String name = nal.name();
    if (others.containsName(name)) {
      Nal otherNal = others.get(name);
      Location location = otherNal.location();
      logger.log(alreadyDefinedError(nal, location));
    }
  }

  private static void logIfDuplicate(Logger logger, Map<String, ? extends Nal> others, Nal nal) {
    String name = nal.name();
    if (others.containsKey(name)) {
      Nal otherNal = others.get(name);
      Location location = otherNal.location();
      logger.log(alreadyDefinedError(nal, location));
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
      public void visitRealFunc(RealFuncNode func) {
        super.visitRealFunc(func);
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
      public void visitRealFunc(RealFuncNode func) {
        super.visitRealFunc(func);
        check(func, "function");
      }

      @Override
      public void visitValue(ValueNode value) {
        super.visitValue(value);
        check(value, "value");
      }

      private void check(ReferencableNode referencable, String referencableKind) {
        if (referencable.annotation().isPresent() && referencable.body().isPresent()) {
          logger.log(parseError(referencable, "Native " + referencableKind + " cannot have body."));
        }
        if (referencable.annotation().isEmpty() && referencable.body().isEmpty()) {
          logger.log(parseError(referencable,
              "Non native " + referencableKind + " cannot have empty body."));
        }
      }
    }.visitAst(ast);
  }

  private static Log alreadyDefinedError(Nal nal, Location location) {
    String atLocation = location.equals(Location.internal())
        ? " internally."
        : " at " + location + ".";
    return parseError(nal.location(), "`" + nal.name() + "` is already defined" + atLocation);
  }
}
