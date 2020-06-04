// Generated from kdl.g4 by ANTLR 4.7.1

package main.com.xarql.kdl;

import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link kdlParser}.
 */
public interface kdlListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link kdlParser#string}.
	 * @param ctx the parse tree
	 */
	void enterString(kdlParser.StringContext ctx);
	/**
	 * Exit a parse tree produced by {@link kdlParser#string}.
	 * @param ctx the parse tree
	 */
	void exitString(kdlParser.StringContext ctx);
	/**
	 * Enter a parse tree produced by {@link kdlParser#number}.
	 * @param ctx the parse tree
	 */
	void enterNumber(kdlParser.NumberContext ctx);
	/**
	 * Exit a parse tree produced by {@link kdlParser#number}.
	 * @param ctx the parse tree
	 */
	void exitNumber(kdlParser.NumberContext ctx);
	/**
	 * Enter a parse tree produced by {@link kdlParser#bool}.
	 * @param ctx the parse tree
	 */
	void enterBool(kdlParser.BoolContext ctx);
	/**
	 * Exit a parse tree produced by {@link kdlParser#bool}.
	 * @param ctx the parse tree
	 */
	void exitBool(kdlParser.BoolContext ctx);
	/**
	 * Enter a parse tree produced by {@link kdlParser#literal}.
	 * @param ctx the parse tree
	 */
	void enterLiteral(kdlParser.LiteralContext ctx);
	/**
	 * Exit a parse tree produced by {@link kdlParser#literal}.
	 * @param ctx the parse tree
	 */
	void exitLiteral(kdlParser.LiteralContext ctx);
	/**
	 * Enter a parse tree produced by {@link kdlParser#type}.
	 * @param ctx the parse tree
	 */
	void enterType(kdlParser.TypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link kdlParser#type}.
	 * @param ctx the parse tree
	 */
	void exitType(kdlParser.TypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link kdlParser#basetype}.
	 * @param ctx the parse tree
	 */
	void enterBasetype(kdlParser.BasetypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link kdlParser#basetype}.
	 * @param ctx the parse tree
	 */
	void exitBasetype(kdlParser.BasetypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link kdlParser#source}.
	 * @param ctx the parse tree
	 */
	void enterSource(kdlParser.SourceContext ctx);
	/**
	 * Exit a parse tree produced by {@link kdlParser#source}.
	 * @param ctx the parse tree
	 */
	void exitSource(kdlParser.SourceContext ctx);
	/**
	 * Enter a parse tree produced by {@link kdlParser#clazz}.
	 * @param ctx the parse tree
	 */
	void enterClazz(kdlParser.ClazzContext ctx);
	/**
	 * Exit a parse tree produced by {@link kdlParser#clazz}.
	 * @param ctx the parse tree
	 */
	void exitClazz(kdlParser.ClazzContext ctx);
	/**
	 * Enter a parse tree produced by {@link kdlParser#constant}.
	 * @param ctx the parse tree
	 */
	void enterConstant(kdlParser.ConstantContext ctx);
	/**
	 * Exit a parse tree produced by {@link kdlParser#constant}.
	 * @param ctx the parse tree
	 */
	void exitConstant(kdlParser.ConstantContext ctx);
}