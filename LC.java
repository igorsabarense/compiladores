import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;


public class LC {
    public static void main(String [] args) throws FileNotFoundException {


        if(args.length == 2) {

            String sourceFilePath = args[0];
            //String outputFilePath = args[1];

            File sourceFile =  new File(sourceFilePath);
            //File outputFile =  new File(outputFilePath);

            if (Objects.nonNull(sourceFile)) {

                try {
                    String source = Files.readString(sourceFile.toPath(), StandardCharsets.US_ASCII);

                    Lexer lexer = new Lexer(source);
                    while ( lexer.lexicalAnalysis() != null ) ;

                } catch (IOException e) {
                    e.printStackTrace();
                }

            } else {
                System.exit(1);
            }
        }

    }
}





enum Type {
    CHAR,INT,BOOLEAN;
}
enum Token {
    FINAL("final"),
    INT("int"),
    CHAR("char"),
    FOR("for"),
    IF("if"),
    ELSE("else"),
    TRUE("TRUE"),
    FALSE("FALSE"),
    AND("and"),
    OR("or"),
    NOT("not"),
    ATTRIBUTION(":="),
    EQUAL ("="),
    OPENING_PARENTHESIS ("("),
    CLOSING_PARENTHESIS (")"),
    LT ("<"),
    GT (">"),
    NOT_EQUAL("<>"),
    GTOE (">="),
    LTOE ("<="),
    COMMA (","),
    PLUS_SIGN ("+"),
    MINUS_SIGN ("-"),
    ASTERISK ("*"),
    SLASH ("/"),
    SEMICOLON (";"),
    OPENING_BRACES ("{"),
    CLOSING_BRACES ("}"),
    THEN ("then"),
    READLN ("readln"),
    WRITE ("write"),
    WRITELN ("writeln"),
    PERCENTAGE ("%"),
    OPENING_BRACKETS ("["),
    CLOSING_BRACKETS ("]"),
    MAIN("main"),


    //CONSTANT({0,1,2,3...}) , IDENTIFIER
    CONST("const"),
    IDENTIFIER("identifier");



    private String description;

    Token(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

}





class Symbol  {



    private Token token;
    private Type type;
    private String lexeme;

    public Symbol(){

    }

    public Symbol( String lexeme, Token token ) {
        this.token = token;
        this.lexeme = lexeme;
    }

    public Symbol(Token token , Type type ,String lexeme) {
        this.token = token;
        this.lexeme = lexeme;
        this.type = type;
    }

    public Token getToken() {
        return token;
    }

    public void setToken(Token token) {
        this.token = token;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getLexeme() {
        return lexeme;
    }

    public void setLexeme(String lexeme) {
        this.lexeme = lexeme;
    }

    @Override
    public String toString() {
        return type != null ? "Symbol{" +
                "token=" + token +
                ", type=" + type +
                ", lexeme='" + lexeme + '\'' +
                '}' :
                "Symbol{" +
                        "token=" + token +
                        " lexeme='" + lexeme + '\'' +
                        '}'  ;
    }

}

class SymbolTable extends HashSet<Symbol>{

    public SymbolTable(){
        this.add(new Symbol("final",Token.FINAL));
        this.add(new Symbol("int",Token.INT));
        this.add(new Symbol("char",Token.CHAR));
        this.add(new Symbol("for",Token.FOR));
        this.add(new Symbol("if",Token.IF));
        this.add(new Symbol("else",Token.ELSE));
        this.add(new Symbol(Token.TRUE, Type.BOOLEAN, "TRUE"));
        this.add(new Symbol(Token.FALSE, Type.BOOLEAN, "FALSE"));
        this.add(new Symbol("and",Token.AND));
        this.add(new Symbol("or",Token.OR));
        this.add(new Symbol("not",Token.NOT));
        this.add(new Symbol(":=",Token.ATTRIBUTION));
        this.add(new Symbol("=",Token.EQUAL));
        this.add(new Symbol("(",Token.OPENING_PARENTHESIS));
        this.add(new Symbol(")",Token.CLOSING_PARENTHESIS));
        this.add(new Symbol("<",Token.LT));
        this.add(new Symbol(">",Token.GT));
        this.add(new Symbol("<>",Token.NOT_EQUAL));
        this.add(new Symbol(">=",Token.GTOE));
        this.add(new Symbol("<=",Token.LTOE));
        this.add(new Symbol(",",Token.COMMA));
        this.add(new Symbol("+",Token.PLUS_SIGN));
        this.add(new Symbol("-",Token.MINUS_SIGN));
        this.add(new Symbol("*",Token.ASTERISK));
        this.add(new Symbol("/",Token.SLASH));
        this.add(new Symbol(";",Token.SEMICOLON));
        this.add(new Symbol("{",Token.OPENING_BRACES));
        this.add(new Symbol("}",Token.CLOSING_BRACES));
        this.add(new Symbol("then",Token.THEN));
        this.add(new Symbol("readln",Token.READLN));
        this.add(new Symbol("write",Token.WRITE));
        this.add(new Symbol("writeln",Token.WRITELN));
        this.add(new Symbol("%",Token.PERCENTAGE));
        this.add(new Symbol("[",Token.OPENING_BRACKETS));
        this.add(new Symbol("]", Token.CLOSING_BRACKETS));
        this.add(new Symbol("main",Token.MAIN));

    }


    public Symbol searchByLexeme(String lexeme){
        AtomicReference<Symbol> symbolToBeFound = new AtomicReference<>();

        this.forEach( symbol -> {
            if(lexeme.equals(symbol.getLexeme())){
                symbolToBeFound.set(symbol);
            }
        });

        return symbolToBeFound.get() != null ? symbolToBeFound.get() : null;
    }


}

class Lexer {



    private SymbolTable symbolTable = new SymbolTable();
    private String symbols = "=.(),+-*;{}%[]";


    //Symbol creation;
    private String lexeme = "";
    private Type type;
    private Token token;


    private char[] sourceCode;
    private char currentChar = ' ';
    private Character previousChar = null;

    private Byte INITIAL_STATE = 0;
    private Byte CURRENT_STATE = 0;
    private Byte FINAL_STATE = 127;

    private int lines = 0;
    private int index;
    private final char EOF = (char) -1;

    private boolean lexemeNotFound = false;


    public Lexer (String sourceCode){
         this.sourceCode = sourceCode.stripTrailing().replaceAll("\r\n","\n").toCharArray();
         this.index = 0 ;
    }


    public char[] getSourceCode() {
        return sourceCode;
    }

    public Symbol lexicalAnalysis(){
        Symbol symbol = new Symbol();

        while (CURRENT_STATE != FINAL_STATE && !lexemeNotFound && index < this.sourceCode.length){

            readCharacter();
            switch (CURRENT_STATE) {
                case 0:
                    initialState();
                    break;
                case 1:
                    state1();
                    break;
                case 2:
                    state2();
                    break;
                case 3:
                    state3();
                    break;
                case 4:
                    state4();
                    break;
                case 5:
                    state5();
                    break;
                case 6:
                    state6();
                    break;
                case 7:
                    state7();
                    break;
                case 8:
                    state8();
                    break;
                case 9:
                    state9();
                    break;
                case 10:
                    state10();
                    break;
                case 12:
                    state12();
                    break;
                case 13:
                    state13();
                    break;
                case 14:
                    state14();
                    break;
                case 15:
                    state15();
                    break;
                case 16:
                    state16();
                    break;
                case 17:
                    state17();
                    break;
            }
            if (  currentChar != EOF && previousChar == null && currentChar != '\n' && currentChar != ' ') {
                lexeme = lexeme.concat(String.valueOf(currentChar));
            }



        }


        System.out.println(lexeme);

        Symbol symbolFromTable = symbolTable.searchByLexeme(lexeme);


        if(symbolFromTable == null){
            symbol.setLexeme(lexeme);
            if(type != null) symbol.setType(type);
            symbol.setToken(token);
            symbolTable.add(symbol);
        }else {
            System.out.println("ok");
            symbol = symbolFromTable;
        }

        CURRENT_STATE = INITIAL_STATE;
        lexeme = "";
        token = null;
        return index <= sourceCode.length  ? symbol : null;



    }



    private void initialState(){
        if(checkSymbols(currentChar)){
            CURRENT_STATE = FINAL_STATE;
        }
        else if (currentChar == ' ' || currentChar == '\n'){
            CURRENT_STATE = INITIAL_STATE;
        }
//        else if (AssertType.isNumeric(currentChar)){
//            CURRENT_STATE = 1;
//        }
//        else if (AssertType.isHexa(currentChar)){
//            CURRENT_STATE = 2;
//        }
        else if (AssertType.isCharacter(currentChar)){
            CURRENT_STATE = 3;
        }
        else if (currentChar =='_'){
            CURRENT_STATE = 4;
        }
        else if (currentChar == '>'){
            CURRENT_STATE = 5;
        }
        else if (currentChar == ':'){
            CURRENT_STATE = 6;
        }
        else if (currentChar == '<'){
            CURRENT_STATE = 7;
        }
        else if (currentChar == '"'){
            CURRENT_STATE = 8;
        }
        else if (currentChar == '/'){
            CURRENT_STATE = 9;
        }
    }

    private void state1() {
//
//        symbol.setType(Type.INT);
//        returnChar();
    }

    private void state2() {
    }

    private void state3() {
        if(AssertType.isCharacter(currentChar) || AssertType.isNumeric(currentChar) || currentChar == '_' ){
            CURRENT_STATE = 3;
        }
        else{
           token = Token.IDENTIFIER;
           returnChar();
        }
    }

    private void state4() {
        if (currentChar == '_'){
            CURRENT_STATE = 4;
        }
        else if(AssertType.isCharacter(currentChar) || AssertType.isNumeric(currentChar)){
            CURRENT_STATE = 10;
        }
        else{
            lexemeNotFound=true;
        }

    }

    private void state5() {
        if(currentChar == '='){
            CURRENT_STATE = FINAL_STATE;
        }
        else {
            returnChar();
        }
    }

    private void state6() {
        if(currentChar == '='){
            CURRENT_STATE = FINAL_STATE;
        }
        lexemeNotFound = true;
    }

    private void state7() {
        if(currentChar == '=' || currentChar == '>'){
            CURRENT_STATE = FINAL_STATE;
        }
        lexemeNotFound = true;
    }

    private void state8() {
        if(currentChar == '"'){
            CURRENT_STATE = FINAL_STATE;
        }
    }

    private void state9() {
        if(currentChar == '*'){
            CURRENT_STATE = 16;
        }else{
            returnChar();
        }

    }

    private void state10() {
        if(AssertType.isNumeric(currentChar) || AssertType.isCharacter(currentChar)|| currentChar == '_'){
            CURRENT_STATE = 10;
        }
        else{
            returnChar();
        }
    }

//    private void state11() {
//        if(currentChar == '"'){
//            CURRENT_STATE = FINAL_STATE;
//        }
//        else{
//            lexemeNotFound = true;
//        }
//    }

    private void state12(){
    }

    private void state13() {
    }

    private void state14() {
    }

    private void state15() {
    }

    private void state16() {
        if(currentChar == '*'){
            CURRENT_STATE = 17;
        }else{
            CURRENT_STATE = 16;
        }
    }

    private void state17() {
        if(currentChar != '/'){
            CURRENT_STATE = 16;
        }else{
            CURRENT_STATE = INITIAL_STATE;
            lexeme = "";
            readCharacter();

        }

    }



    private boolean checkSymbols(char currentChar) {
        String str = String.valueOf(currentChar);
        if(symbols.contains(str.toString())){
            return true;
        }
        return false;

    }


    private void readCharacter() {

        if (previousChar == null) {
            currentChar = sourceCode[index++];
            if (currentChar == '\n') {
                lines++;
            }
        } else {
            currentChar = previousChar;
            previousChar = null;
        }

    }

    private void returnChar () {
        previousChar = currentChar;
        CURRENT_STATE = FINAL_STATE;
    }


}


class AssertType {
    public static final char EOF = (char)-1;

    public static boolean isCharacter(char c) {
        return (c >= 'a' && c <= 'z') || (c >='A' && c <='Z');
    }
    public static boolean isNumeric(char c) {
        return (c >= '0' && c <= '9');
    }
    public static boolean isHexa(char c) {
        return (isNumeric(c)) || (c >= 'a' && c <= 'f') || (c >= 'A' && c <= 'F');
    }
    public static boolean isValidChar(char c) {
        return isCharacter(c) || isNumeric(c) ;
    }
}
