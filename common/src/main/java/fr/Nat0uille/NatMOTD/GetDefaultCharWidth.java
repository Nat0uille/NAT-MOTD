package fr.Nat0uille.NatMOTD;

public class GetDefaultCharWidth {

    public static int GetDefaultCharWidth(char c) {
        switch (c) {
            case 'i': case 'l': case '.': case ',': case '!': case '|': return 2;
            case '\'': return 3;
            case ' ': return 4;
            case 't': case 'I': case '[': case ']': return 4;
            case 'k': case '{': case '}': case '<': case '>': return 5;
            default: return 6;
        }
    }
}
