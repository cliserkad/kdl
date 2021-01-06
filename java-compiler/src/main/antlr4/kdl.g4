parser grammar kdl;

@header {
  package com.xarql.kdl.antlr;
}

options {
    tokenVocab = kdlLexer;
}

// literals
bool: TRUE | FALSE;
number: DIGIT | NUMBER;
fraction: number DOT number;
integer: number | binary | hex;
binary: BIN_PREFIX number;
hex: HEX_PREFIX (DIGIT | HEX_CHAR)+;
literal: bool | CHAR_LIT | STRING_LIT | integer | fraction | NULL;

statement: expression | reservation | assignment | returnStatement | conditional;
block: CURL_L statement* CURL_R;

methodCall: IDENTIFIER argumentSet;
argumentSet: PAREN_L (expression (COMMA expression)*)? PAREN_R;

value: literal | IDENTIFIER | methodCall | THIS | indexAccess | subSequence;
operator: PLUS | MINUS | SLASH | MULTIPLY | MODULUS | DOT | NOT | INCREMENT | DECREMENT
		| BIT_SHIFT_LEFT | BIT_SHIFT_RIGHT | BIT_SHIFT_RIGHT_UNSIGNED | BIT_AND | BIT_OR | BIT_XOR;
expression: operator? value (operator expression?)?;
indexAccess: BRACE_L expression BRACE_R;
range: expression? DOT DOT expression;
subSequence: BRACE_L range BRACE_R;

condition: expression (comparator expression)? (appender condition)?;
comparator: EQUAL | NOT_EQUAL | MORE_THAN | LESS_THAN | MORE_OR_EQUAL | LESS_OR_EQUAL |
			ADDRESS_EQUAL | ADDRESS_NOT_EQUAL | IS_A | IS_NOT_A;
appender: AND | OR | XOR;

// for loop
for_loop: FOR IDENTIFIER COLON range block;
for_each_loop: FOR IDENTIFIER COLON expression block;

// conditionals
conditional: branch | assertion | loop | for_loop | for_each_loop;
branch: IF condition block inverse?;
inverse: ELSE block;
assertion: ASSERT condition;
loop: WHILE condition block;

details: (type TILDE? QUESTION_MARK? | CONST) IDENTIFIER;
reservation: details (COMMA IDENTIFIER)* (COLON expression)?;
assignment: expression operator? COLON expression;

// method definitions
main: MAIN block;
methodDefinition: (details | IDENTIFIER TILDE?) parameterSet block;
parameterSet: PAREN_L ((THIS | param) (COMMA param)*)? PAREN_R;
param: details (COLON expression)?;
returnStatement: RETURN expression?;

type: (basetype | IDENTIFIER) (BRACE_L BRACE_R)*;
basetype: BOOLEAN | BYTE | SHORT | CHAR | INT | FLOAT | LONG | DOUBLE | STRING;

source: path? use* clazz EOF;
use: USE pathLit;
pathLit: IDENTIFIER (SLASH IDENTIFIER)*;
path: PATH pathLit;
clazz: TYPE IDENTIFIER CURL_L (reservation | main | methodDefinition)* CURL_R;
