package Lexical;

/**
 * 词法单元
 */
public class Token {
    private String token_name;
    private String attribute_value;

    Token(String token_name, String attribute_value) {
        this.token_name = token_name;
        this.attribute_value = attribute_value;
    }

    @Override
    public String toString() {
        return "<" + token_name + "," + attribute_value + ">";
    }
}
