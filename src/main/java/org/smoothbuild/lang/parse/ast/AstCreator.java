package org.smoothbuild.lang.parse.ast;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static org.smoothbuild.lang.parse.LocationHelpers.locationOf;
import static org.smoothbuild.util.Lists.concat;
import static org.smoothbuild.util.Lists.list;
import static org.smoothbuild.util.Lists.map;
import static org.smoothbuild.util.Lists.sane;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.smoothbuild.antlr.lang.SmoothBaseVisitor;
import org.smoothbuild.antlr.lang.SmoothParser.ArgContext;
import org.smoothbuild.antlr.lang.SmoothParser.ArgListContext;
import org.smoothbuild.antlr.lang.SmoothParser.ArrayTypeContext;
import org.smoothbuild.antlr.lang.SmoothParser.ChainCallContext;
import org.smoothbuild.antlr.lang.SmoothParser.ChainContext;
import org.smoothbuild.antlr.lang.SmoothParser.ChainPartContext;
import org.smoothbuild.antlr.lang.SmoothParser.ExprContext;
import org.smoothbuild.antlr.lang.SmoothParser.ExprHeadContext;
import org.smoothbuild.antlr.lang.SmoothParser.FieldContext;
import org.smoothbuild.antlr.lang.SmoothParser.FieldListContext;
import org.smoothbuild.antlr.lang.SmoothParser.FieldReadContext;
import org.smoothbuild.antlr.lang.SmoothParser.FunctionTypeContext;
import org.smoothbuild.antlr.lang.SmoothParser.LiteralContext;
import org.smoothbuild.antlr.lang.SmoothParser.ModuleContext;
import org.smoothbuild.antlr.lang.SmoothParser.NatContext;
import org.smoothbuild.antlr.lang.SmoothParser.ParamContext;
import org.smoothbuild.antlr.lang.SmoothParser.ParamListContext;
import org.smoothbuild.antlr.lang.SmoothParser.ReferencableContext;
import org.smoothbuild.antlr.lang.SmoothParser.StructContext;
import org.smoothbuild.antlr.lang.SmoothParser.TypeContext;
import org.smoothbuild.antlr.lang.SmoothParser.TypeListContext;
import org.smoothbuild.antlr.lang.SmoothParser.TypeNameContext;
import org.smoothbuild.io.fs.space.FilePath;
import org.smoothbuild.lang.base.define.Location;

import com.google.common.collect.ImmutableList;

public class AstCreator {
  public static Ast fromParseTree(FilePath filePath, ModuleContext module) {
    List<StructNode> structs = new ArrayList<>();
    List<ReferencableNode> referencables = new ArrayList<>();
    new SmoothBaseVisitor<Void>() {
      @Override
      public Void visitStruct(StructContext struct) {
        String name = struct.TNAME().getText();
        Location location = locationOf(filePath, struct.TNAME().getSymbol());
        List<ItemNode> fields = createFields(struct.fieldList());
        structs.add(new StructNode(name, fields, location));
        return null;
      }

      private List<ItemNode> createFields(FieldListContext fieldList) {
        if (fieldList != null) {
          return sane(fieldList.field())
              .stream()
              .map(this::createField)
              .collect(toImmutableList());
        }
        return new ArrayList<>();
      }

      private ItemNode createField(FieldContext field) {
        TypeNode type = createType(field.type());
        TerminalNode nameNode = field.NAME();
        String name = nameNode.getText();
        Location location = locationOf(filePath, nameNode);
        return new ItemNode(type, name, Optional.empty(), location);
      }

      @Override
      public Void visitReferencable(ReferencableContext referencable) {
        TerminalNode nameNode = referencable.NAME();
        visitChildren(referencable);
        Optional<TypeNode> type = createTypeSane(referencable.type());
        String name = nameNode.getText();
        Optional<ExprNode> expr = createExprSane(referencable.expr());
        Optional<NativeNode> nativ = createNativeSane(referencable.nat());
        Location location = locationOf(filePath, nameNode);
        if (referencable.paramList() == null) {
          referencables.add(new ValueNode(type, name, expr, nativ, location));
        } else {
          List<ItemNode> params = createParams(referencable.paramList());
          referencables.add(new RealFuncNode(type, name, params, expr, nativ, location));
        }
        return null;
      }

      private Optional<NativeNode> createNativeSane(NatContext nativ) {
        if (nativ == null) {
          return Optional.empty();
        } else {
          return Optional.of(new NativeNode(
              createStringNode(nativ, nativ.STRING()),
              isPure(nativ),
              locationOf(filePath, nativ)));
        }
      }

      private boolean isPure(NatContext atNative) {
        return atNative.pure != null || atNative.impure == null;
      }

      private List<ItemNode> createParams(ParamListContext paramList) {
        ArrayList<ItemNode> result = new ArrayList<>();
        if (paramList != null) {
          return sane(paramList.param())
              .stream().map(this::createParam)
              .collect(toImmutableList());
        }
        return result;
      }

      private ItemNode createParam(ParamContext param) {
        var type = createType(param.type());
        var name = param.NAME().getText();
        var defaultArgument = Optional.ofNullable(param.expr()).map(this::createExpr);
        var location = locationOf(filePath, param);
        return new ItemNode(type, name, defaultArgument, location);
      }

      private Optional<ExprNode> createExprSane(ExprContext expr) {
        return expr == null ? Optional.empty() : Optional.of(createExpr(expr));
      }

      private ExprNode createExpr(ExprContext expr) {
        ExprNode result = createExprHead(expr.exprHead());
        List<ChainCallContext> chainCallsInPipe = expr.chainCall();
        for (int i = 0; i < chainCallsInPipe.size(); i++) {
          var pipedArg = pipedArgument(result, expr.p.get(i));
          ChainCallContext chain = chainCallsInPipe.get(i);
          result = createChainCallExpr(pipedArg, chain);
        }
        return result;
      }

      private ExprNode createExprHead(ExprHeadContext expr) {
        if (expr.chain() != null) {
          return createChainExpr(expr.chain());
        }
        if (expr.literal() != null) {
          return createLiteral(expr.literal());
        }
        throw newRuntimeException(ExprHeadContext.class);
      }

      private ArgNode pipedArgument(ExprNode result, Token pipeCharacter) {
        // Location of nameless piped argument is set to the location of pipe character '|'.
        Location location = locationOf(filePath, pipeCharacter);
        return new ArgNode(null, result, location);
      }

      private ExprNode createLiteral(LiteralContext expr) {
        if (expr.array() != null) {
          List<ExprNode> elements = map(expr.array().expr(), this::createExpr);
          return new ArrayNode(elements, locationOf(filePath, expr));
        }
        if (expr.STRING() != null) {
          return createStringNode(expr, expr.STRING());
        }
        if (expr.BLOB() != null) {
          return new BlobNode(expr.BLOB().getText().substring(2), locationOf(filePath, expr));
        }
        throw newRuntimeException(LiteralContext.class);
      }

      private StringNode createStringNode(ParserRuleContext expr, TerminalNode quotedString) {
        String unquoted = unquote(quotedString.getText());
        Location location = locationOf(filePath, expr);
        return new StringNode(unquoted, location);
      }

      private ExprNode createChainExpr(ChainContext chain) {
        ExprNode result = newRefNode(chain.NAME());
        return createChainParts(result, chain.chainPart());
      }

      private ExprNode createChainCallExpr(ArgNode pipedArg, ChainCallContext chainCall) {
        ExprNode result = newRefNode(chainCall.NAME());
        for (FieldReadContext fieldRead : chainCall.fieldRead()) {
          result = createFieldRead(result, fieldRead);
        }

        var args = createArgList(chainCall.argList());
        result = createCall(result, concat(pipedArg, args), chainCall.argList());

        return createChainParts(result, chainCall.chainPart());
      }

      private RefNode newRefNode(TerminalNode name) {
        return new RefNode(name.getText(), locationOf(filePath, name));
      }

      private FieldReadNode createFieldRead(ExprNode result, FieldReadContext fieldRead) {
        String name = fieldRead.NAME().getText();
        Location location = locationOf(filePath, fieldRead);
        return new FieldReadNode(result, name, location);
      }

      private ExprNode createChainParts(ExprNode expr, List<ChainPartContext> chainParts) {
        ExprNode result = expr;
        for (ChainPartContext chainPart : chainParts) {
          if (chainPart.argList() != null) {
            var args = createArgList(chainPart.argList());
            result = createCall(result, args, chainPart.argList());
          } else if (chainPart.fieldRead() != null) {
            result = createFieldRead(result, chainPart.fieldRead());
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
          ExprNode exprNode = createExpr(expr);
          result.add(new ArgNode(name, exprNode, locationOf(filePath, arg)));
        }
        return result;
      }

      private ExprNode createCall(
          ExprNode function, List<ArgNode> args, ArgListContext argListContext) {
        Location location = locationOf(filePath, argListContext);
        return new CallNode(function, args, location);
      }

      private Optional<TypeNode> createTypeSane(TypeContext type) {
        return type == null ? Optional.empty() : Optional.of(createType(type));
      }

      private TypeNode createType(TypeContext type) {
        if (type instanceof TypeNameContext typeIdentifier) {
          return createType(typeIdentifier);
        }
        if (type instanceof ArrayTypeContext arrayType) {
          return createArrayType(arrayType);
        }
        if (type instanceof FunctionTypeContext functionType) {
          return createFunctionType(functionType);
        }
        throw new RuntimeException("Illegal parse tree: " + TypeContext.class.getSimpleName()
            + " without children.");
      }

      private TypeNode createType(TypeNameContext type) {
        return new TypeNode(type.getText(), locationOf(filePath, type.TNAME()));
      }

      private TypeNode createArrayType(ArrayTypeContext arrayType) {
        TypeNode elementType = createType(arrayType.type());
        return new ArrayTypeNode(elementType, locationOf(filePath, arrayType));
      }

      private TypeNode createFunctionType(FunctionTypeContext functionType) {
        TypeNode resultType = createType(functionType.type());
        return new FunctionTypeNode(resultType, createTypeList(functionType.typeList()),
            locationOf(filePath, functionType));
      }

      private ImmutableList<TypeNode> createTypeList(TypeListContext typeList) {
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
