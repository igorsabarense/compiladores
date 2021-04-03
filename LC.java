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

            File sourceFile =  new File(sourceFilePath);;
            //File outputFile =  new File(outputFilePath);

            if (Objects.nonNull(sourceFile)) {

                try {
                    String source = Files.readString(sourceFile.toPath(), StandardCharsets.US_ASCII);
                    Lexer lexer = new Lexer(source);
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
    INTEGER("int"),
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
        this.add(new Symbol("integer",Token.INTEGER));
        this.add(new Symbol("char",Token.CHAR));
        this.add(new Symbol("for",Token.FOR));
        this.add(new Symbol("if",Token.IF));
        this.add(new Symbol("else",Token.ELSE));
        this.add(new Symbol("TRUE",Token.TRUE));
        this.add(new Symbol("FALSE",Token.FALSE));
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
        AtomicReference<Symbol> symbolToBeFound = null;

        this.forEach( symbol -> {
            if(lexeme.equals(symbol.getLexeme())){
                symbolToBeFound.set(symbol);
            }
        });

        return symbolToBeFound.get() != null ? symbolToBeFound.get() : null;
    }


}

class Lexer {

    private SymbolTable symbolTable;
    private String symbols = "=.(),+-*;{}%[]";
    private String lexeme;

    private char[] sourceCode;
    private char currentChar = ' ';
    private Character previousChar = null;

    private Byte INITIAL_STATE = 0;
    private Byte CURRENT_STATE = 0;
    private Byte FINAL_STATE = 127;

    private int lines = 0;
    private int index = 0;
    private final boolean EOF = index >= sourceCode.length;

    private boolean lexemeNotFound = false;


    public Lexer (String sourceCode){
        this.sourceCode = sourceCode.toCharArray();
    }


    public char[] getSourceCode() {
        return sourceCode;
    }

    public void lexicalAnalysis(){

        int sourceCodeLength = this.sourceCode.length-1;

        while (index <= sourceCodeLength){



                switch (CURRENT_STATE) {
                    case INITIAL_STATE:
                        initialState();
                        break;

                    default:
                        throw new IllegalStateException("Unexpected value: " + CURRENT_STATE);
                }

            if (currentChar != EOF && previousChar == null && currentChar != '\n' && currentChar != ' ') {
                lexeme = lexeme.concat(currentChar + "");
            }

        }
    }

    private char s0() {
        index++;
        if(this.sourceCode[index] == '*'){
            currentChar = sourceCode[index];
            commentState0(currentChar, index);
        }else{

        }
    }

    private void initialState(){
       if(checkSymbols(currentChar)){
           CURRENT_STATE = FINAL_STATE;
       }
       else if (currentChar == ' '){
           CURRENT_STATE = INITIAL_STATE;
       }
        else if (currentChar.isNumeric()){
           CURRENT_STATE = 1;
       }
        else if (currentChar == '0'){
            CURRENT_STATE = 2;
       }
        else if (currentChar >= 'a' && currentChar <= 'Z'){
            CURRENT_STATE = 3;
       }
        else if (currentChar =='_'){
            CURRENT_STATE = 4;
       }

        else if (currentChar == '>' && currentChar == '='){
            CURRENT_STATE = 5;
       }

        else if (currentChar == '\''){
            CURRENT_STATE = 6;
       }
        else{

       }
    }

    private boolean checkSymbols(char currentChar) {
        StringBuilder str = new StringBuilder(currentChar);
        if(symbols.contains(str.toString())){
            return true;
        }
        return false;

    }

    private char commentState0(Character currentChar) {
        return 'c';
    }


    private char getCurrentChar(){
        return index < sourceCode.length ? sourceCode[index] : ' ';
    }


    private void readChar() {

            if (previousChar == null) {
                currentChar = sourceCode[index];
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



