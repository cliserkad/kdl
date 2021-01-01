parser grammar kdl;

@header {
  package com.xarql.kdl.antlr;
}

options {
    tokenVocab = kdlLexer;
}

// literals
bool: TRUE | FALSE;
fraction: DIGIT? (DIGIT | SEPARATOR | UNDERSCORE)* DOT DIGIT (DIGIT | SEPARATOR | UNDERSCORE)*;
integer: DIGIT (DIGIT | SEPARATOR | UNDERSCORE)*;
literal: bool | CHAR_LIT | STRING_LIT | integer | fraction | R_NULL;

statement: expression | variableDeclaration | assignment | returnStatement | conditional;
block: BODY_OPEN statement* BODY_CLOSE;

// for loop
for_loop: FOR IDENTIFIER ASSIGN range block;
for_each_loop: FOR IDENTIFIER ASSIGN expression block;
range: expression? DOT DOT expression;

// conditionals
conditional: r_if | assertion | r_while | for_loop | for_each_loop;
r_if: R_IF condition block r_else?;
r_else: R_ELSE block;
assertion: ASSERT condition;
r_while: WHILE condition block;

value: literal | IDENTIFIER | methodCall | indexAccess | subSequence | THIS;
operator: PLUS | MINUS | SLASH | MULTIPLY | MODULUS | DOT;
expression: value (operator? expression)?;

condition: expression (comparator expression)?;
comparator: EQUAL | NOT_EQUAL | MORE_THAN | LESS_THAN | MORE_OR_EQUAL | LESS_OR_EQUAL;
appender: AND | OR;

variableDeclaration: details (SEPARATOR IDENTIFIER)* (ASSIGN expression)?;
assignment: expression operator? ASSIGN expression;
details: type MUTABLE? IDENTIFIER;

subSequence: BRACE_OPEN range BRACE_CLOSE;
indexAccess: BRACE_OPEN expression BRACE_CLOSE;

methodCall: IDENTIFIER argumentSet;
argumentSet: PARAM_OPEN (expression (SEPARATOR expression)*)? PARAM_CLOSE;

// method definitions
methodDefinition: (details | IDENTIFIER MUTABLE?) parameterSet block;
parameterSet: PARAM_OPEN ((THIS | param) (SEPARATOR param)*)? PARAM_CLOSE;
param: details (ASSIGN expression)?;

returnStatement: RETURN expression?;

type: (basetype | IDENTIFIER) (BRACE_OPEN BRACE_CLOSE)*;
basetype: BOOLEAN | BYTE | SHORT | CHAR | INT | FLOAT | LONG | DOUBLE | STRING;

source: path? use* clazz EOF;
use: USE pathLit;
pathLit: IDENTIFIER (SLASH IDENTIFIER)*;
path: PATH pathLit;
clazz: TYPE IDENTIFIER BODY_OPEN (constantDef | fieldDef | main | methodDefinition)* BODY_CLOSE;
constantDef: CONST IDENTIFIER ASSIGN expression;
fieldDef: variableDeclaration;
main: MAIN block;
