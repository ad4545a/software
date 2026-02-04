package com.jewellery.erp.service;

import com.jewellery.erp.config.AppConfig;
import com.jewellery.erp.model.LoginAudit;
import com.jewellery.erp.model.User;
import com.jewellery.erp.repository.LoginAuditRepository;
import com.jewellery.erp.repository.UserRepository;
import com.jewellery.erp.security.AuthException;
import com.jewellery.erp.security.SecurityContext;
import com.jewellery.erp.security.UserSession;
import com.jewellery.erp.util.MachineUtil;
import com.jewellery.erp.util.PasswordUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import java.util.Optional;

public class AuthService {

    private final UserRepository userRepository = new UserRepository();
    private final LoginAuditRepository auditRepository = new LoginAuditRepository();

    // Dummy hash for timing-safe verification (prevents user enumeration)
    private static final String DUMMY_HASH = "$2a$12$C6UzMDM.H6dfI/f/IKcEeO5PmF4wZ6Xr2yQzZvKqH8YrZvKqH8Yr";

    public UserSession login(String username, String password) {
        Transaction tx = null;
        try (Session session = AppConfig.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            // 1. Fetch User
            Optional<User> userOpt = userRepository.findByUsername(session, username);

            // 2. Timing-safe password verification
            String hash = userOpt.isPresent() ? userOpt.get().getPasswordHash() : DUMMY_HASH;
            boolean passwordMatch = PasswordUtil.checkPassword(password, hash);

            // 3. Validate User, Password, and Active Status
            if (!userOpt.isPresent() || !passwordMatch) {
                if (userOpt.isPresent()) {
                    logAudit(session, userOpt.get().getId(), false);
                }
                tx.commit();
                throw new AuthException("Invalid username or password");
            }

            User user = userOpt.get();

            if (!user.isActive()) {
                logAudit(session, user.getId(), false);
                tx.commit();
                throw new AuthException("User account is disabled");
            }

            // 4. Success -> Create Session
            String machineId = MachineUtil.getMachineId();
            logAudit(session, user.getId(), true);

            UserSession userSession = new UserSession(user.getId(), user.getUsername(), user.getRole(), machineId);
            SecurityContext.setSession(userSession);

            tx.commit();
            return userSession;

        } catch (AuthException e) {
            throw e;
        } catch (Exception e) {
            if (tx != null && tx.isActive())
                tx.rollback();
            e.printStackTrace();
            throw new AuthException("System Error During Login");
        }
    }

    private void logAudit(Session session, Long userId, boolean success) {
        LoginAudit audit = new LoginAudit();
        audit.setUserId(userId);
        audit.setSuccess(success);
        audit.setMachineId(MachineUtil.getMachineId());
        auditRepository.save(session, audit);
    }

    public void logout() {
        SecurityContext.clear();
    }
}
