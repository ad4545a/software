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

    public UserSession login(String username, String password) {
        Transaction tx = null;
        try (Session session = AppConfig.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            // 1. Fetch User (Generic Error on failure to prevent enumeration)
            Optional<User> userOpt = userRepository.findByUsername(session, username);

            if (!userOpt.isPresent()) {
                throw new AuthException("Invalid username or password");
            }

            User user = userOpt.get();

            // 2. Check Password
            boolean passwordMatch = PasswordUtil.checkPassword(password, user.getPasswordHash());
            if (!passwordMatch) {
                logAudit(session, user.getId(), false);
                tx.commit();
                throw new AuthException("Invalid username or password");
            }

            // 3. Check Active Status
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
            // Transaction managed inside logic for Audit consistency
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
