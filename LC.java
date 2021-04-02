import java.io.File;
import java.io.FileInputStream;
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

    public Symbol(Token token , String lexeme) {
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
class SymbolTable extends HashMap<Token,List<Symbol>>{


    public SymbolTable(){

        this.put(Token.FINAL, createSymbolList("final",Token.FINAL));
        this.put(Token.INTEGER,createSymbolList("integer",Token.INTEGER));
        this.put(Token.CHAR,createSymbolList("char",Token.CHAR));
        this.put(Token.FOR,createSymbolList("for",Token.FOR));
        this.put(Token.IF,createSymbolList("if",Token.IF));
        this.put(Token.ELSE,createSymbolList("else",Token.ELSE));
        this.put(Token.TRUE,createSymbolList("TRUE",Token.TRUE));
        this.put(Token.FALSE,createSymbolList("FALSE",Token.FALSE));
        this.put(Token.AND,createSymbolList("and",Token.AND));
        this.put(Token.OR,createSymbolList("or",Token.OR));
        this.put(Token.NOT,createSymbolList("not",Token.NOT));
        this.put(Token.ATTRIBUTION,createSymbolList(":=",Token.ATTRIBUTION));
        this.put(Token.EQUAL,createSymbolList("=",Token.EQUAL));
        this.put(Token.OPENING_PARENTHESIS,createSymbolList("(",Token.OPENING_PARENTHESIS));
        this.put(Token.CLOSING_PARENTHESIS,createSymbolList(")",Token.CLOSING_PARENTHESIS));
        this.put(Token.LT,createSymbolList("<",Token.LT));
        this.put(Token.GT,createSymbolList(">",Token.GT));
        this.put(Token.NOT_EQUAL,createSymbolList("<>",Token.NOT_EQUAL));
        this.put(Token.GTOE ,createSymbolList(">=",Token.GTOE));
        this.put(Token.LTOE ,createSymbolList("<=",Token.LTOE));
        this.put(Token.COMMA,createSymbolList(",",Token.COMMA));
        this.put(Token.PLUS_SIGN,createSymbolList("+",Token.PLUS_SIGN));
        this.put(Token.MINUS_SIGN,createSymbolList("-",Token.MINUS_SIGN));
        this.put(Token.ASTERISK,createSymbolList("*",Token.ASTERISK));
        this.put(Token.SLASH,createSymbolList("/",Token.SLASH));
        this.put(Token.SEMICOLON,createSymbolList(";",Token.SEMICOLON));
        this.put(Token.OPENING_BRACES,createSymbolList("{",Token.OPENING_BRACES));
        this.put(Token.CLOSING_BRACES,createSymbolList("}",Token.CLOSING_BRACES));
        this.put(Token.THEN ,createSymbolList("then",Token.THEN));
        this.put(Token.READLN,createSymbolList("readln",Token.READLN));
        this.put(Token.WRITE ,createSymbolList("write",Token.WRITE));
        this.put(Token.WRITELN ,createSymbolList("writeln",Token.WRITELN));
        this.put(Token.PERCENTAGE,createSymbolList("%",Token.PERCENTAGE));
        this.put(Token.OPENING_BRACKETS,createSymbolList("[",Token.OPENING_BRACKETS));
        this.put(Token.CLOSING_BRACKETS,createSymbolList("]", Token.CLOSING_BRACKETS));
        this.put(Token.MAIN,createSymbolList("main",Token.MAIN));
        this.put(Token.IDENTIFIER , new ArrayList<>());
        this.put(Token.CONST , new ArrayList<>());
    }


    public Symbol searchByLexeme(String lexeme){
        AtomicReference<Symbol> symbolToBeFound = new AtomicReference<>();

        this.values().forEach( symbolList -> symbolList.forEach( symbol -> {
            if(symbol.getLexeme().equals(lexeme)){
                symbolToBeFound.set(symbol);
            }
        }));

        return symbolToBeFound.get() != null ? symbolToBeFound.get() : null;
    }


    private List<Symbol> createSymbolList(String lexeme, Token token , Type type){
        return new ArrayList(Collections.singleton(new Symbol(token, type, lexeme)));
    }

    private List<Symbol> createSymbolList(String lexeme, Token token ){
        return new ArrayList(Collections.singleton(new Symbol(token, lexeme)));
    }


}


class Lexer {
    //String  commentRegex = "\\/\\*([\\s\\S]*?)\\*\\/";
    //private static final String symbols = "!\"&'(*)+,-./:;<=>?[]_{} \n\r\t";
    private char[] sourceCode;

    public Lexer (String sourceCode){
        this.sourceCode = sourceCode.toCharArray();
    }


    public char[] getSourceCode() {
        return sourceCode;
    }


}



class Tests {

    public void insertAndPrint(){
        SymbolTable st = new SymbolTable();
        st.get(Token.IDENTIFIER).add(new Symbol(Token.IDENTIFIER,"FRED"));
        st.get(Token.IDENTIFIER).add(new Symbol(Token.IDENTIFIER,"IGOR"));
        st.get(Token.IDENTIFIER).add(new Symbol(Token.IDENTIFIER,"Brenon"));
        st.get(Token.CONST).add(new Symbol(Token.CONST,Type.BOOLEAN,"1"));
        st.get(Token.CONST).add(new Symbol(Token.CONST,Type.CHAR,"C"));

        st.entrySet()
                .stream()
                .sorted(Map.Entry.<Token, List<Symbol>>comparingByKey())
                .forEach(System.out::println);

        Symbol sm = st.searchByLexeme("KAKA");

        System.out.println(Objects.isNull(sm) ? "null" :  sm.toString());
    }



}
