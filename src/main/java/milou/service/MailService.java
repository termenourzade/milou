package milou.service;

import milou.exception.AppException;
import milou.model.Email;
import milou.model.User;
import milou.repo.EmailRepository;
import milou.repo.UserRepository;
import milou.util.CodeGenerator;

import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class MailService {
    private final EmailRepository emailRepo = new EmailRepository();
    private final UserRepository userRepo = new UserRepository();

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public String send(User sender, List<String> recipientsInput, String subject, String body) {
        validateSubject(subject);
        if (body == null || body.isEmpty()) throw new AppException("Body cannot be empty!");

        List<User> recipients = validateRecipients(recipientsInput);
        if (recipients.isEmpty()) throw new AppException("No valid recipients!");

        Email email = new Email(uniqueCode(), sender, subject, body);
        email = emailRepo.save(email);
        for (User r : recipients) emailRepo.addRecipient(email, r);
        return email.getCode();
    }

    public String readByCode(User current, String code) {
        Email e = findEmail(code);
        boolean isSender = e.getSender().getId().equals(current.getId());
        boolean isRecipient = emailRepo.recipientsForEmail(e.getId()).stream()
                .anyMatch(mail -> mail.equalsIgnoreCase(current.getEmail()));
        if (!isSender && !isRecipient) return "You cannot read this email.";

        if (isRecipient) emailRepo.markRead(e.getId(), current.getId());
        String recipientsCsv = String.join(", ", emailRepo.recipientsForEmail(e.getId()));
        return "Code: " + e.getCode() + "\n" +
               "Recipient(s): " + recipientsCsv + "\n" +
               "Subject: " + e.getSubject() + "\n" +
               "Date: " + DATE_FMT.format(e.getSentAt()) + "\n\n" +
               e.getBody();
    }

    public String reply(User current, String originalCode, String body) {
        Email original = findEmail(originalCode);
        Set<String> set = new LinkedHashSet<>();
        set.add(original.getSender().getEmail());
        set.addAll(emailRepo.recipientsForEmail(original.getId()));
        set.remove(current.getEmail());
        List<String> recipientsRaw = new ArrayList<>(set);
        String subject = prefix("[Re] ", original.getSubject());
        String newCode = send(current, recipientsRaw, subject, body);
        return "Successfully sent your reply to email " + originalCode + ".\nCode: " + newCode;
    }

    public String forward(User current, String originalCode, List<String> newRecipientsRaw) {
        Email original = findEmail(originalCode);
        String subject = prefix("[Fw] ", original.getSubject());
        String newCode = send(current, newRecipientsRaw, subject, original.getBody());
        return "Successfully forwarded your email.\nCode: " + newCode;
    }

    public List<String> listAll(User user) {
        return emailRepo.listAllForUser(user).stream()
                .map(r -> "+ " + r[0] + " - " + r[1] + " (" + r[2] + ")")
                .collect(Collectors.toList());
    }

    public List<String> listUnread(User user) {
        return emailRepo.listUnreadForUser(user).stream()
                .map(r -> "+ " + r[0] + " - " + r[1] + " (" + r[2] + ")")
                .collect(Collectors.toList());
    }

    public List<String> listSent(User user) {
        return emailRepo.listSentByUser(user).stream()
                .map(r -> {
                    Integer emailId = (Integer) r[0];
                    String subject = (String) r[1];
                    String code = (String) r[2];
                    List<String> recips = emailRepo.recipientsForEmail(emailId);
                    return "+ " + String.join(", ", recips) + " - " + subject + " (" + code + ")";
                }).collect(Collectors.toList());
    }

    private List<User> validateRecipients(List<String> input) {
        List<User> validUsers = new ArrayList<>();
        for (String s : input) {
            String e = completeEmail(s);
            User u = userRepo.findByEmail(e);
            if (u == null) throw new AppException("Recipient with email " + e + " was not found!");
            validUsers.add(u);
        }
        return validUsers;
    }

    private void validateSubject(String subject) {
        if (subject == null || subject.isBlank()) throw new AppException("Subject cannot be empty!");
        if (subject.length() > 255) throw new AppException("Subject is too long!");
    }

    private String completeEmail(String input) {
        String e = input.trim().toLowerCase();
        if (!e.contains("@")) e = e + "@milou.com";
        return e;
    }

    private String uniqueCode() {
        for (int i = 0; i < 50; i++) {
            String c = CodeGenerator.next6();
            if (emailRepo.findByCode(c) == null) return c;
        }
        throw new AppException("Could not generate unique code.");
    }

    private Email findEmail(String code) {
        Email e = emailRepo.findByCode(code);
        if (e == null) throw new AppException("Email not found: " + code);
        return e;
    }

    private String prefix(String p, String s) {
        return s.startsWith(p) ? s : p + s;
    }
}
