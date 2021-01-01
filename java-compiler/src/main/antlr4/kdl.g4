parser grammar kdl;

@header {
  package com.xarql.kdl.antlr;
}

options {
    tokenVocab = kdlLexer;
}

// literals
bool: TRUE | FALSE;
fraction: DIGIT? (DIGIT | COMMA | UNDERSCORE)* DOT DIGIT (DIGIT | COMMA | UNDERSCORE)*;
integer: DIGIT (DIGIT | COMMA | UNDERSCORE)*;
literal: bool | CHAR_LIT | STRING_LIT | integer | fraction | NULL;

statement: expression | reservation | assignment | returnStatement | conditional;
block: CURL_L statement* CURL_R;

methodCall: IDENTIFIER argumentSet;
argumentSet: PAREN_L (expression (COMMA expression)*)? PAREN_R;

value: literal | IDENTIFIER | methodCall | THIS;
operator: PLUS | MINUS | SLASH | MULTIPLY | MODULUS | DOT;
expression: value ((operator expression) | indexAccess | subSequence)?;
indexAccess: BRACE_L expression BRACE_R;
range: expression? DOT DOT expression;
subSequence: BRACE_L range BRACE_R;

condition: expression (comparator expression)?;
comparator: EQUAL | NOT_EQUAL | MORE_THAN | LESS_THAN | MORE_OR_EQUAL | LESS_OR_EQUAL;
appender: AND | OR;

// for loop
for_loop: FOR IDENTIFIER COLON range block;
for_each_loop: FOR IDENTIFIER COLON expression block;

// conditionals
conditional: branch | assertion | loop | for_loop | for_each_loop;
branch: IF condition block inverse?;
inverse: ELSE block;
assertion: ASSERT condition;
loop: WHILE condition block;

details: type TILDE? IDENTIFIER;
reservation: details (COMMA IDENTIFIER)* (COLON expression)?;
assignment: expression operator? COLON expression;

// method definitions
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
clazz: TYPE IDENTIFIER CURL_L (constantDef | fieldDef | main | methodDefinition)* CURL_R;
constantDef: CONST IDENTIFIER COLON expression;
fieldDef: reservation;
main: MAIN block;
