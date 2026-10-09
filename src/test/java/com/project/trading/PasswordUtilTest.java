package com.project.trading;

import com.project.trading.util.PasswordUtil;

public class PasswordUtilTest {

    public static void main(String[] args) {
        System.out.println("Running PasswordUtil verification tests...");

        // Test 1: Dynamic hash and verify
        String password = "TestPassword@123";
        String hash = PasswordUtil.hash(password.toCharArray());
        if (hash == null || !hash.contains(":")) {
            throw new AssertionError("Test 1 Failed: Generated hash is invalid");
        }
        if (!PasswordUtil.verify(password.toCharArray(), hash)) {
            throw new AssertionError("Test 1 Failed: Password verification returned false");
        }
        if (PasswordUtil.verify("WrongPassword".toCharArray(), hash)) {
            throw new AssertionError("Test 1 Failed: Wrong password was accepted");
        }
        System.out.println("Test 1 (Dynamic Hash & Verify): PASSED");

        // Test 2: Verify against seed.sql Admin@123 hash
        String adminHash = "0+3DUVGW/WjfLstenwOF3w==:opvQZCOrEjgqCLsOOnh8rXweOBhILK0+zdi/c1ELKOk=";
        if (!PasswordUtil.verify("Admin@123".toCharArray(), adminHash)) {
            throw new AssertionError("Test 2 Failed: Admin@123 verification failed against seed hash");
        }
        if (PasswordUtil.verify("WrongPass".toCharArray(), adminHash)) {
            throw new AssertionError("Test 2 Failed: Wrong password matched admin hash");
        }
        System.out.println("Test 2 (Seed Admin Hash Verification): PASSED");

        // Test 3: Verify against seed.sql Demo@123 hash for rahul
        String rahulHash = "GkhX+Tj0sacIqQQ0GZC6XA==:Uqu0H3XXIMG9lS3wtbuIwdmk6fbIXMCdBHtsSaZJi4E=";
        if (!PasswordUtil.verify("Demo@123".toCharArray(), rahulHash)) {
            throw new AssertionError("Test 3 Failed: Demo@123 verification failed for rahul hash");
        }
        System.out.println("Test 3 (Seed Rahul Hash Verification): PASSED");

        // Test 4: Verify against seed.sql Demo@123 hash for priya
        String priyaHash = "wBU07JGZc/buhmqgpMrkaw==:t2pmqPYP34CL8gfaMhpqRZt89Vn7RuVsK7nONk4yOWk=";
        if (!PasswordUtil.verify("Demo@123".toCharArray(), priyaHash)) {
            throw new AssertionError("Test 4 Failed: Demo@123 verification failed for priya hash");
        }
        System.out.println("Test 4 (Seed Priya Hash Verification): PASSED");

        System.out.println("\nALL PASSWORDUTIL VERIFICATION TESTS PASSED SUCCESSFULLY!");
    }
}
