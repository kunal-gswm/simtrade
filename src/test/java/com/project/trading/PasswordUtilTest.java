package com.project.trading;

import com.project.trading.util.PasswordUtil;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PasswordUtilTest {

    @Test
    void testDynamicHashAndVerify() {
        String password = "TestPassword@123";
        String hash = PasswordUtil.hash(password.toCharArray());
        assertNotNull(hash, "Generated hash should not be null");
        assertTrue(hash.contains(":"), "Generated hash should contain salt:hash delimiter");
        assertTrue(PasswordUtil.verify(password.toCharArray(), hash), "Verification with correct password should pass");
        assertFalse(PasswordUtil.verify("WrongPassword".toCharArray(), hash), "Verification with wrong password should fail");
    }

    @Test
    void testSeedAdminHashVerification() {
        String adminHash = "0+3DUVGW/WjfLstenwOF3w==:opvQZCOrEjgqCLsOOnh8rXweOBhILK0+zdi/c1ELKOk=";
        assertTrue(PasswordUtil.verify("Admin@123".toCharArray(), adminHash), "Admin@123 verification failed against seed hash");
        assertFalse(PasswordUtil.verify("WrongPass".toCharArray(), adminHash), "Wrong password matched admin hash");
    }

    @Test
    void testSeedRahulHashVerification() {
        String rahulHash = "GkhX+Tj0sacIqQQ0GZC6XA==:Uqu0H3XXIMG9lS3wtbuIwdmk6fbIXMCdBHtsSaZJi4E=";
        assertTrue(PasswordUtil.verify("Demo@123".toCharArray(), rahulHash), "Demo@123 verification failed for rahul hash");
        assertFalse(PasswordUtil.verify("WrongPass".toCharArray(), rahulHash), "Wrong password matched rahul hash");
    }

    @Test
    void testSeedPriyaHashVerification() {
        String priyaHash = "wBU07JGZc/buhmqgpMrkaw==:t2pmqPYP34CL8gfaMhpqRZt89Vn7RuVsK7nONk4yOWk=";
        assertTrue(PasswordUtil.verify("Demo@123".toCharArray(), priyaHash), "Demo@123 verification failed for priya hash");
        assertFalse(PasswordUtil.verify("WrongPass".toCharArray(), priyaHash), "Wrong password matched priya hash");
    }
}
