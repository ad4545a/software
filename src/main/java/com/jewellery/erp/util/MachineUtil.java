package com.jewellery.erp.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.io.File;

public class MachineUtil {

    public static String getMachineId() {
        try {
            // Composite fingerprint: OS, Arch, UserHome, JVM Vendor
            String osName = System.getProperty("os.name");
            String osArch = System.getProperty("os.arch");
            String userHome = System.getProperty("user.home");
            String javaVendor = System.getProperty("java.vendor");

            // Validation
            if (osName == null || userHome == null || javaVendor == null) {
                throw new IllegalStateException(
                        "Cannot generate stable machine fingerprint: system properties unavailable");
            }
            if (!new File(userHome).exists()) {
                throw new IllegalStateException("Invalid machine fingerprint source: user.home does not exist");
            }

            String fingerprint = String.join("|", osName, osArch, userHome, javaVendor);

            // Hash it with SHA-256
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(fingerprint.getBytes(StandardCharsets.UTF_8));

            return bytesToHex(hash);

        } catch (Exception e) {
            throw new RuntimeException("Failed to generate machine ID", e);
        }
    }

    private static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
