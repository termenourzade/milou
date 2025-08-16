package milou.tui;

import milou.model.User;
import milou.exception.AppException;
import milou.service.AuthService;
import milou.service.MailService;

import java.util.*;

public class ConsoleUI {
    private final Scanner scanner = new Scanner(System.in);
    private final AuthService auth = new AuthService();
    private final MailService mail = new MailService();

    public void run() {
        System.out.println("Welcome To Milou Email Service!");

        while (true) {
            System.out.print("\n[L]ogin, [S]ign up: ");
            String choice = scanner.nextLine().trim().toLowerCase();

            if (equalsAny(choice, "l", "login")) {
                User loggedInUser = doLogin();
                afterLogin(loggedInUser);
            } else if (equalsAny(choice, "s", "sign up")) {
                doSignup();
            } else {
                System.out.println("Invalid choice. Please choose from the options given: ");
            }
        }
    }

    private User doLogin() {
        while (true) {
            try {
                System.out.print("Email: ");
                String email = scanner.nextLine().trim().toLowerCase();
                System.out.print("Password: ");
                String pass = scanner.nextLine().trim();
                User u = auth.login(email, pass);
                System.out.println("\nWelcome back, " + u.getName() + "!");
                showUnread(u);
                return u;
            } catch (AppException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private void doSignup() {
        while (true) {
            try {
                System.out.print("Name: ");
                String name = scanner.nextLine().trim();
                System.out.print("Email: ");
                String email = scanner.nextLine().trim().toLowerCase();
                System.out.print("Password: ");
                String pass = scanner.nextLine().trim();
                auth.signup(name, email, pass);
                return;
            } catch (AppException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private void afterLogin(User u) {
        while (true) {
            System.out.print("[S]end, [V]iew, [R]eply, [F]orward, [E]xit: ");
            String cmd = scanner.nextLine().trim().toLowerCase();
            try {
                if (equalsAny(cmd, "s", "send")) {
                    doSend(u);
                } else if (equalsAny(cmd, "v", "view")) {
                    doView(u);
                } else if (equalsAny(cmd, "r", "reply")) {
                    doReply(u);
                } else if (equalsAny(cmd, "f", "forward")) {
                    doForward(u);
                }
                else if (equalsAny(cmd, "e", "exit")){
                    break;
                }
            } catch (AppException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private void doSend(User u) {
        System.out.print("Recipient(s): ");
        List<String> recipients = parseEmails(scanner.nextLine().trim().toLowerCase());
        System.out.print("Subject: ");
        String subject = scanner.nextLine().trim();
        System.out.print("Body: ");
        String body = scanner.nextLine().trim();
        String code = mail.send(u, recipients, subject, body);
        System.out.println("Successfully sent your email.\nCode: " + code);
    }

    private void doView(User u) {
        while (true) {
            System.out.print("[A]ll emails, [U]nread emails, [S]ent emails, Read by [C]ode, [E]xit: ");
            String choice = scanner.nextLine().trim().toLowerCase();
            if (equalsAny(choice, "a", "all")) {
                System.out.println("All Emails:");
                mail.listAll(u).forEach(System.out::println);
            } else if (equalsAny(choice, "u", "unread")) {
                System.out.println("Unread Emails:");
                mail.listUnread(u).forEach(System.out::println);
            } else if (equalsAny(choice, "s", "sent")) {
                System.out.println("Sent Emails:");
                mail.listSent(u).forEach(System.out::println);
            } else if (equalsAny(choice, "c", "code")) {
                System.out.print("Code: ");
                String code = scanner.nextLine().trim().toLowerCase();
                String out = mail.readByCode(u, code);
                System.out.println(out);
            } else if (equalsAny(choice, "e", "exit")) {
                return;
            }
        }
    }

    private void doReply(User u) {
        System.out.print("Code: ");
        String code = scanner.nextLine().trim().toLowerCase();
        System.out.print("Body: ");
        String body = scanner.nextLine().trim();
        String out = mail.reply(u, code, body);
        System.out.println(out);
    }

    private void doForward(User u) {
        System.out.print("Code: ");
        String code = scanner.nextLine().trim().toLowerCase();
        System.out.print("Recipient(s): ");
        List<String> recipients = parseEmails(scanner.nextLine().trim().toLowerCase());
        String out = mail.forward(u, code, recipients);
        System.out.println(out);
    }

    private void showUnread(User u) {
        List<String> unread = mail.listUnread(u);
        System.out.println("Unread Emails:");
        if (!unread.isEmpty()) {
            System.out.println(unread.size() + " unread emails:");
            unread.forEach(System.out::println);
        }
    }

    private boolean equalsAny(String s, String... opts) {
        String input;
        if (s == null) {
            input = "";
        } else {
            input = s.trim().toLowerCase();
        }

        for (String option : opts) {
            if (input.equals(option)) {
                return true;
            }
        }
        return false;
    }

    private List<String> parseEmails(String line) {
        String[] parts = line.split(",");
        List<String> emails = new ArrayList<>();
        for (String p : parts) {
            String e = p.trim();
            if (!e.isEmpty()) emails.add(e);
        }
        return emails;
    }
}