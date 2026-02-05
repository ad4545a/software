package com.jewellery.erp.test.phase2.service;

import com.jewellery.erp.master.entity.Counter;
import com.jewellery.erp.master.entity.UserProfile;
import com.jewellery.erp.master.service.CounterService;
import com.jewellery.erp.master.service.UserProfileService;
import com.jewellery.erp.test.phase2.TestBase;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TEST K1: Cannot Deactivate Assigned Counter
 */
public class CounterServiceTest extends TestBase {

    private final CounterService counterService = new CounterService();
    private final UserProfileService userProfileService = new UserProfileService();

    @Test
    public void testCannotDeactivateAssignedCounter() {
        // Create counter
        Counter counter = new Counter();
        counter.setName("Counter 1");
        counter.setLocation("Main Hall");
        counter = counterService.createCounter(counter);

        // Create user profile assigned to this counter
        UserProfile profile = new UserProfile();
        profile.setUserId(100L);
        profile.setFullName("John Doe");
        profile.setAssignedCounter(counter.getId());
        userProfileService.createProfile(profile);

        // Attempt to deactivate counter
        Long counterId = counter.getId();
        Exception ex = assertThrows(Exception.class, () -> {
            counterService.deactivateCounter(counterId);
        }, "Should not allow deactivating counter assigned to active users");

        String message = ex.getMessage();
        assertTrue(message.contains("counter") && message.contains("user profile"),
                "Error message should mention counter assignment to user profiles. Got: " + message);
    }

    @Test
    public void testCanDeactivateUnassignedCounter() {
        Counter counter = new Counter();
        counter.setName("Counter 2");
        counter.setLocation("Side Hall");
        counter = counterService.createCounter(counter);

        Long counterId = counter.getId();
        assertDoesNotThrow(() -> {
            counterService.deactivateCounter(counterId);
        }, "Should allow deactivating unassigned counter");
    }

    @Test
    public void testNameUniqueness() {
        Counter c1 = new Counter();
        c1.setName("Counter A");
        c1.setLocation("Floor 1");
        counterService.createCounter(c1);

        Counter c2 = new Counter();
        c2.setName("Counter A"); // Duplicate
        c2.setLocation("Floor 2");

        assertThrows(Exception.class, () -> {
            counterService.createCounter(c2);
        }, "Should reject duplicate counter name");
    }
}
