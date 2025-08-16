package milou.service;

import milou.exception.AppException;
import milou.model.User;
import milou.repo.UserRepository;

import java.util.Objects;

public class AuthService {
    private final UserRepository userRepo = new UserRepository();

    public String completeEmail(String input) {
        String e = input.trim().toLowerCase();
        if (!e.contains("@"))
            e = e + "@milou.com";
        return e;
    }

    public User signup(String name, String emailInput, String password) {
        String email = completeEmail(emailInput);
        if (password == null || password.length() < 8) {
            throw new AppException("Password must contain at least 8 characters!");
        }
        if (userRepo.findByEmail(email) != null) {
            throw new AppException("This email is already registered!");
        }
        String hash = hash(password);
        return userRepo.save(new User(name, email, hash));
    }

    public User login(String emailInput, String password) {
        String email = completeEmail(emailInput);
        User u = userRepo.findByEmail(email);
        if (u == null || !Objects.equals(u.getPasswordHash(), hash(password))) {
            throw new AppException("Wrong email or password!");
        }
        return u;
    }

    private String hash(String s) {
        return Integer.toHexString(Objects.hash(s)); }
}
