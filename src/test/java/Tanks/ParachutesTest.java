package Tanks;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ParachutesTest {

    private Parachutes parachutes;

    @BeforeEach
    public void setUp() {
        parachutes = new Parachutes(3); // Initialise with 3 parachutes
    }

    @Test
    public void testInitialState() {
        assertEquals(3, parachutes.getCount(), "Initial parachute count should be 3");
        assertFalse(parachutes.isDeployed(), "Parachute should not be deployed initially");
    }

    @Test
    public void testGetDescentInfoWithParachutes() {
        int[] descentInfo = parachutes.getDescentInfo();
        assertEquals(Parachutes.SLOW_DESCENT_RATE, descentInfo[0], "Descent rate should be slow with parachutes");
        assertEquals(0, descentInfo[1], "Damage per pixel should be 0 with parachutes");
    }

    @Test
    public void testGetDescentInfoWithoutParachutes() {
        parachutes = new Parachutes(0); // Initialize with 0 parachutes
        int[] descentInfo = parachutes.getDescentInfo();
        assertEquals(Parachutes.FAST_DESCENT_RATE, descentInfo[0], "Descent rate should be fast without parachutes");
        assertEquals(Parachutes.DAMAGE_PER_PIXEL, descentInfo[1], "Damage per pixel should be one without parachutes");
    }

    @Test
    public void testCheckAndDeployParachute() {
        parachutes.checkAndDeploy();
        assertTrue(parachutes.isDeployed());
        assertEquals(2, parachutes.getCount());
    }

    @Test
    public void testResetDeployment() {
        parachutes.checkAndDeploy();
        parachutes.resetDeployment();
        assertFalse(parachutes.isDeployed());
    }

    @Test
    public void testUseParachute() {
        parachutes.useParachute();
        assertTrue(parachutes.isDeployed(), "Parachute should be deployed after useParachute");
        assertEquals(2, parachutes.getCount(), "Parachute count should decrease by 1 after useParachute");
    }

    @Test
    public void testBuyOneParachute() {
        parachutes.buyOneParachute();
        assertEquals(4, parachutes.getCount(), "Parachute count should increase by 1 after buying one");
    }

    @Test
    public void testNoParachutesLeftToDeploy() {
        parachutes = new Parachutes(0); // Initialise with 0 parachutes
        parachutes.checkAndDeploy();
        assertFalse(parachutes.isDeployed(), "Parachute should not be deployed when none are available");
    }
}
