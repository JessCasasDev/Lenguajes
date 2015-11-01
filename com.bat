@echo off

cls
java org.antlr.v4.Tool C.g4
javac C*.java Main.java
java org.antlr.v4.runtime.misc.TestRig %* C compilationUnit -gui
java Main test.in