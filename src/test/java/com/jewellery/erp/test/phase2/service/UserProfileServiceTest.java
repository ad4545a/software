package com.jewellery.erp.test.phase2.service;

import com.jewellery.erp.config.AppConfig;
import com.jewellery.erp.master.entity.UserProfile;
import com.jewellery.erp.master.service.UserProfileService;
import com.jewellery.erp.model.UserRole;
import com.jewellery.erp.security.SecurityContext;
import com.jewellery.erp.security.UserSession;
import com.jewellery.erp.test.phase2.TestBase;
import org.junit.jupiter.api.Test;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class UserProfileServiceTest extends TestBase {

    private final UserProfileService userProfileService = new UserProfileService();
    // Note: TestBase sets up Fake Admin Session (userId=999L) in @BeforeEach

    @Test
    public void testCannotDeactivateLoggedInUserProfile() {
        // Enforce session context for this test explicitly
        SecurityContext.setSession(new UserSession(999L, "admin", UserRole.ADMIN, "machine"));

        // Setup: Ensure we have a profile for THIS user (999)
        // Check if exists or create
        UserProfile profile = userProfileService.findByUserId(999L);
        if (profile == null) {
            profile = new UserProfile();
            profile.setUserId(999L); // Matches logged-in user
            profile.setFullName("Admin User " + UUID.randomUUID());
            // profile.setEmail("admin@test.com");
            // profile.setRole(UserRole.ADMIN);
            profile = userProfileService.createProfile(profile);
        }

        Long profileId = profile.getId();

        // Debug checks
        assertEquals(999L, SecurityContext.getSession().getUserId(), "Session UserID must be 999");
        assertEquals(999L, profile.getUserId(), "Profile UserID must be 999");

        try {
            userProfileService.deactivateProfile(profileId);
            fail("Should have thrown IllegalStateException for deactivating own profile");
        } catch (IllegalStateException e) {
            // Expected
            // Expected
            assertTrue(e.getMessage().contains("Cannot deactivate your own profile"),
                    "Error message should match. Got: " + e.getMessage());
        } catch (Exception e) {
            fail("Unexpected exception type: " + e.getClass().getName() + " - " + e.getMessage());
        }
    }

    @Test
    public void testCreateProfile() {
        UserProfile p = new UserProfile();
        p.setUserId(1001L);
        p.setFullName("Test User " + UUID.randomUUID());
        // p.setRole(UserRole.STAFF);

        assertDoesNotThrow(() -> userProfileService.createProfile(p));
    }
}
