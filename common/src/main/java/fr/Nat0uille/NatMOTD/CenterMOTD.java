package fr.Nat0uille.NatMOTD;

public class CenterMOTD {

    public static String CenterMOTD(String message) {
        if (message == null || message.equals("")) return "";

        int CENTER_PX = 130;
        int messagePxSize = 0;

        boolean previousCode = false;
        boolean isBold = false;

        for (char c : message.toCharArray()) {
            if (c == '§') {
                previousCode = true;
                continue;
            } else if (previousCode) {
                previousCode = false;
                if (c == 'l' || c == 'L') {
                    isBold = true;
                } else {
                    isBold = false;
                }
                continue;
            }

            int charPx = GetDefaultCharWidth.GetDefaultCharWidth(c);
            messagePxSize += isBold ? charPx + 1 : charPx;
            messagePxSize++;
        }

        int halvedMessageSize = messagePxSize / 2;
        int toCompensate = CENTER_PX - halvedMessageSize;
        int spaceWidth = GetDefaultCharWidth.GetDefaultCharWidth(' ');
        int compensated = 0;
        StringBuilder sb = new StringBuilder();

        while (compensated < toCompensate) {
            sb.append(" ");
            compensated += spaceWidth;
        }

        return sb + message;
    }
}
