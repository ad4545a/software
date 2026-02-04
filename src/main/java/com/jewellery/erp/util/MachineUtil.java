package com.jewellery.erp.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.io.File;

public class MachineUtil {

    public static String getMachineId() {
        try {
            StringBuilder sb = new StringBuilder();

            // 1. OS Name (Stable)
            sb.append(System.getProperty("os.name")).append("|");

            // 2. OS Arch (Stable)
            sb.append(System.getProperty("os.arch")).append("|");

            // 3. User Home (Stable per user)
            sb.append(System.getProperty("user.home")).append("|");

            // 4. Disk Root (Stable) - usually C:\ or /
            File[] roots = File.listRoots();
            if (roots != null && roots.length > 0) {
                sb.append(roots[0].getAbsolutePath()).append("|");
            }

            // 5. JVM Vendor (Stable for this installation)
            sb.append(System.getProperty("java.vendor"));

            // Hash it
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(sb.toString().getBytes(StandardCharsets.UTF_8));

            return bytesToHex(hash);

        } catch (Exception e) {
            // Fallback (Should rarely happen in standard JVM)
            return "UNKNOWN_MACHINE_ID";
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
