import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordGenerator {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        System.out.println("=== Password Generator ===\n");
        
        // Generate passwords
        String[] passwords = {"1234", "password", "admin123", "student123", "teacher123"};
        
        for (String password : passwords) {
            String encoded = encoder.encode(password);
            System.out.println("Plain: " + password);
            System.out.println("Encoded: " + encoded);
            System.out.println();
        }
    }
}
