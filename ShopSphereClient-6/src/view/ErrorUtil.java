package view;

/**
 * Extrait le vrai message d'erreur metier d'une exception RMI.
 *
 * Probleme resolu : quand une methode distante (UserServiceImpl,
 * OrderServiceImpl, etc.) fait "throw new RemoteException(\"...\")" pour
 * signaler une erreur metier (ex: "Email ou mot de passe incorrect."), le
 * runtime RMI enveloppe automatiquement cette RemoteException dans une
 * java.rmi.ServerException dont le message est le texte generique
 * "RemoteException occurred in server thread; nested exception is: ...".
 * Cote client, un SwingWorker enveloppe ensuite cette ServerException dans
 * une ExecutionException. Sans deballage, l'utilisateur voit ce texte
 * technique au lieu du vrai message.
 *
 * rootMessage() descend jusqu'a la cause la plus profonde de la chaine
 * d'exceptions pour recuperer le message metier original.
 *
 * @author ShopSphere
 */
public final class ErrorUtil {

    private ErrorUtil() {}

    /**
     * Retourne le message de la cause la plus profonde de {@code t}.
     * Fonctionne quel que soit le nombre de couches d'enveloppe
     * (ExecutionException > ServerException > RemoteException > ...).
     */
    public static String rootMessage(Throwable t) {
        if (t == null) return "Erreur inconnue.";
        Throwable current = t;
        while (current.getCause() != null && current.getCause() != current) {
            current = current.getCause();
        }
        String msg = current.getMessage();
        return (msg != null && !msg.isEmpty()) ? msg : "Erreur de connexion au serveur.";
    }
}
