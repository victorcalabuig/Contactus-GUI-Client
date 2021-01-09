package sample;

public class ClientUtils {

    /**
     * Constrye mensaje a partir de un userId, command y una serie de argumentos.
     */
    public String buildMessage(int userId, String command, String args){
        StringBuilder msg = new StringBuilder();

        msg.append(userId);
        msg.append(" ").append(command);
        msg.append(" ").append(args);

        return msg.toString();
    }

}
