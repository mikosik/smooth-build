package org.smoothbuild.lang.parse.ast;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static org.smoothbuild.lang.parse.LocationHelpers.locationOf;
import static org.smoothbuild.util.collect.Lists.concat;
import static org.smoothbuild.util.collect.Lists.list;
import static org.smoothbuild.util.collect.Lists.map;
import static org.smoothbuild.util.collect.Lists.sane;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.smoothbuild.antlr.lang.SmoothBaseVisitor;
import org.smoothbuild.antlr.lang.SmoothParser.AnnotationContext;
import org.smoothbuild.antlr.lang.SmoothParser.ArgContext;
import org.smoothbuild.antlr.lang.SmoothParser.ArgListContext;
import org.smoothbuild.antlr.lang.SmoothParser.ArrayTypeContext;
import org.smoothbuild.antlr.lang.SmoothParser.ChainCallContext;
import org.smoothbuild.antlr.lang.SmoothParser.ChainContext;
import org.smoothbuild.antlr.lang.SmoothParser.ChainPartContext;
import org.smoothbuild.antlr.lang.SmoothParser.EvaluableContext;
import org.smoothbuild.antlr.lang.SmoothParser.ExprContext;
import org.smoothbuild.antlr.lang.SmoothParser.ExprHeadContext;
import org.smoothbuild.antlr.lang.SmoothParser.FieldContext;
import org.smoothbuild.antlr.lang.SmoothParser.FieldListContext;
import org.smoothbuild.antlr.lang.SmoothParser.FunctionTypeContext;
import org.smoothbuild.antlr.lang.SmoothParser.LiteralContext;
import org.smoothbuild.antlr.lang.SmoothParser.ModuleContext;
import org.smoothbuild.antlr.lang.SmoothParser.ParamContext;
import org.smoothbuild.antlr.lang.SmoothParser.ParamListContext;
import org.smoothbuild.antlr.lang.SmoothParser.SelectContext;
import org.smoothbuild.antlr.lang.SmoothParser.StructContext;
import org.smoothbuild.antlr.lang.SmoothParser.TypeContext;
import org.smoothbuild.antlr.lang.SmoothParser.TypeListContext;
import org.smoothbuild.antlr.lang.SmoothParser.TypeNameContext;
import org.smoothbuild.io.fs.space.FilePath;
import org.smoothbuild.lang.base.define.Location;

import com.google.common.collect.ImmutableList;

public class AstCreator {
  public static Ast fromParseTree(FilePath filePath, ModuleContext module) {
    List<StructN> structs = new ArrayList<>();
    List<EvaluableN> referencables = new ArrayList<>();
    new SmoothBaseVisitor<Void>() {
      @Override
      public Void visitStruct(StructContext struct) {
        String name = struct.TNAME().getText();
        Location location = locationOf(filePath, struct.TNAME().getSymbol());
        List<ItemN> fields = createFields(struct.fieldList());
        structs.add(new StructN(name, fields, location));
        return null;
      }

      private List<ItemN> createFields(FieldListContext fieldList) {
        if (fieldList != null) {
          return sane(fieldList.field())
              .stream()
              .map(this::createField)
              .collect(toImmutableList());
        }
        return new ArrayList<>();
      }

      private ItemN createField(FieldContext field) {
        TypeN type = createType(field.type());
        TerminalNode nameNode = field.NAME();
        String name = nameNode.getText();
        Location location = locationOf(filePath, nameNode);
        return new ItemN(type, name, Optional.empty(), location);
      }

      @Override
      public Void visitEvaluable(EvaluableContext evaluable) {
        TerminalNode nameNode = evaluable.NAME();
        visitChildren(evaluable);
        Optional<TypeN> type = createTypeSane(evaluable.type());
        String name = nameNode.getText();
        Optional<ExprN> expr = createExprSane(evaluable.expr());
        Optional<AnnotationN> annotation = createNativeSane(evaluable.annotation());
        Location location = locationOf(filePath, nameNode);
        if (evaluable.paramList() == null) {
          referencables.add(new ValueN(type, name, expr, annotation, location));
        } else {
          List<ItemN> params = createParams(evaluable.paramList());
          referencables.add(new RealFuncN(type, name, params, expr, annotation, location));
        }
        return null;
      }

      private Optional<AnnotationN> createNativeSane(AnnotationContext annotation) {
        if (annotation == null) {
          return Optional.empty();
        } else {
          return Optional.of(new AnnotationN(
              createStringNode(annotation, annotation.STRING()),
              isPure(annotation),
              locationOf(filePath, annotation)));
        }
      }

      private boolean isPure(AnnotationContext annotation) {
        return annotation.pure != null || annotation.impure == null;
      }

      private List<ItemN> createParams(ParamListContext paramList) {
        ArrayList<ItemN> result = new ArrayList<>();
        if (paramList != null) {
          return sane(paramList.param())
              .stream().map(this::createParam)
              .collect(toImmutableList());
        }
        return result;
      }

      private ItemN createParam(ParamContext param) {
        var type = createType(param.type());
        var name = param.NAME().getText();
        var defaultArgument = Optional.ofNullable(param.expr()).map(this::createExpr);
        var location = locationOf(filePath, param);
        return new ItemN(type, name, defaultArgument, location);
      }

      private Optional<ExprN> createExprSane(ExprContext expr) {
        return expr == null ? Optional.empty() : Optional.of(createExpr(expr));
      }

      private ExprN createExpr(ExprContext expr) {
        ExprN result = createExprHead(expr.exprHead());
        List<ChainCallContext> chainCallsInPipe = expr.chainCall();
        for (int i = 0; i < chainCallsInPipe.size(); i++) {
          var pipedArg = pipedArgument(result, expr.p.get(i));
          ChainCallContext chain = chainCallsInPipe.get(i);
          result = createChainCallExpr(pipedArg, chain);
        }
        return result;
      }

      private ExprN createExprHead(ExprHeadContext expr) {
        if (expr.chain() != null) {
          return createChainExpr(expr.chain());
        }
        if (expr.literal() != null) {
          return createLiteral(expr.literal());
        }
        throw newRuntimeException(ExprHeadContext.class);
      }

      private ExprN createChainExpr(ChainContext chain) {
        ExprN result = newRefNode(chain.NAME());
        return createChainParts(result, chain.chainPart());
      }

      private ArgNode pipedArgument(ExprN result, Token pipeCharacter) {
        // Location of nameless piped argument is set to the location of pipe character '|'.
        Location location = locationOf(filePath, pipeCharacter);
        return new ArgNode(null, result, location);
      }

      private ExprN createLiteral(LiteralContext expr) {
        if (expr.array() != null) {
          List<ExprN> elems = map(expr.array().expr(), this::createExpr);
          return new ArrayN(elems, locationOf(filePath, expr));
        }
        if (expr.BLOB() != null) {
          return new BlobN(expr.BLOB().getText().substring(2), locationOf(filePath, expr));
        }
        if (expr.INT() != null) {
          return new IntN(expr.INT().getText(), locationOf(filePath, expr));
        }
        if (expr.STRING() != null) {
          return createStringNode(expr, expr.STRING());
        }
        throw newRuntimeException(LiteralContext.class);
      }

      private StringN createStringNode(ParserRuleContext expr, TerminalNode quotedString) {
        String unquoted = unquote(quotedString.getText());
        Location location = locationOf(filePath, expr);
        return new StringN(unquoted, location);
      }

      private ExprN createChainCallExpr(ArgNode pipedArg, ChainCallContext chainCall) {
        ExprN result = newRefNode(chainCall.NAME());
        for (SelectContext fieldRead : chainCall.select()) {
          result = createSelect(result, fieldRead);
        }

        var args = createArgList(chainCall.argList());
        result = createCall(result, concat(pipedArg, args), chainCall.argList());

        return createChainParts(result, chainCall.chainPart());
      }

      private RefN newRefNode(TerminalNode name) {
        return new RefN(name.getText(), locationOf(filePath, name));
      }

      private SelectN createSelect(ExprN result, SelectContext fieldRead) {
        String name = fieldRead.NAME().getText();
        Location location = locationOf(filePath, fieldRead);
        return new SelectN(result, name, location);
      }

      private ExprN createChainParts(ExprN expr, List<ChainPartContext> chainParts) {
        ExprN result = expr;
        for (ChainPartContext chainPart : chainParts) {
          if (chainPart.argList() != null) {
            var args = createArgList(chainPart.argList());
            result = createCall(result, args, chainPart.argList());
          } else if (chainPart.select() != null) {
            result = createSelect(result, chainPart.select());
          } else {
            throw newRuntimeException(ChainContext.class);
          }
        }
        return result;
      }

      private List<ArgNode> createArgList(ArgListContext argList) {
        List<ArgNode> result = new ArrayList<>();
        for (ArgContext arg : argList.arg()) {
          ExprContext expr = arg.expr();
          TerminalNode nameNode = arg.NAME();
          String name = nameNode == null ? null : nameNode.getText();
          ExprN exprN = createExpr(expr);
          result.add(new ArgNode(name, exprN, locationOf(filePath, arg)));
        }
        return result;
      }

      private ExprN createCall(
          ExprN function, List<ArgNode> args, ArgListContext argListContext) {
        Location location = locationOf(filePath, argListContext);
        return new CallN(function, args, location);
      }

      private Optional<TypeN> createTypeSane(TypeContext type) {
        return type == null ? Optional.empty() : Optional.of(createType(type));
      }

      private TypeN createType(TypeContext type) {
        return switch (type) {
          case TypeNameContext typeIdentifier -> createType(typeIdentifier);
          case ArrayTypeContext arrayType -> createArrayType(arrayType);
          case FunctionTypeContext functionType -> createFunctionType(functionType);
          default -> throw new RuntimeException(
              "Illegal parse tree: " + TypeContext.class.getSimpleName() + " without children.");
        };
      }

      private TypeN createType(TypeNameContext type) {
        return new TypeN(type.getText(), locationOf(filePath, type.TNAME()));
      }

      private TypeN createArrayType(ArrayTypeContext arrayType) {
        TypeN elemType = createType(arrayType.type());
        return new ArrayTypeN(elemType, locationOf(filePath, arrayType));
      }

      private TypeN createFunctionType(FunctionTypeContext functionType) {
        TypeN resultType = createType(functionType.type());
        return new FunctionTypeN(resultType, createTypeList(functionType.typeList()),
            locationOf(filePath, functionType));
      }

      private ImmutableList<TypeN> createTypeList(TypeListContext typeList) {
        if (typeList != null) {
          return map(typeList.type(), this::createType);
        } else {
          return list();
        }
      }

      private RuntimeException newRuntimeException(Class<?> clazz) {
        return new RuntimeException("Illegal parse tree: " + clazz.getSimpleName()
            + " without children.");
      }
    }.visit(module);
    return new Ast(structs, referencables);
  }

  private static String unquote(String quotedString) {
    return quotedString.substring(1, quotedString.length() - 1);
  }
}
