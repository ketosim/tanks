package Tanks;

/**
 * The Parachutes class represents a parachute system for a tank, managing the
 * deployment and usage of parachutes, and calculating descent rates and damage.
 */
public class Parachutes {
    private int count;
    private boolean isDeployed;

    public static final int SLOW_DESCENT_RATE = 60;  // pixels per second with parachute
    public static final int FAST_DESCENT_RATE = 120; // pixels per second without parachute
    public static final int DAMAGE_PER_PIXEL = 1;    // damage per pixel when no parachutes

    /**
     * Constructs a Parachutes object with an initial number of parachutes.
     * @param initialCount the initial number of parachutes.
     */
    public Parachutes(int initialCount) {
        this.count = initialCount;
        this.isDeployed = false;
    }

    /**
     * Returns the descent rate and damage information based on parachute availability.
     * @return an array where the first element is the descent rate (pixels per second)
     *         and the second element is the damage per pixel.
     */
    public int[] getDescentInfo() {
        if (hasParachutes()) {
            return new int[]{SLOW_DESCENT_RATE, 0};
        } else {
            return new int[]{FAST_DESCENT_RATE, DAMAGE_PER_PIXEL};
        }
    }

   /**
     * Checks if there are any parachutes available.
     * @return true if there are parachutes available, false otherwise.
     */
    public boolean hasParachutes() {
        return count > 0;
    }

    /**
     * Resets the deployment status of the parachute.
     */
    public void resetDeployment() {
        if (isDeployed) {
            // System.out.println("Parachute deployment reset.");
            isDeployed = false;
        }
    }

    /**
     * Checks and deploys a parachute if available.
     */
    public void checkAndDeploy() {
        if (!isDeployed && hasParachutes()) {
            useParachute();
        }
    }

    /**
     * Uses a parachute if available and marks it as deployed.
     */
    public void useParachute() {
        if (count > 0 && !isDeployed) {
            // System.out.println("Parachute deployed.");
            isDeployed = true;
            count--;
            System.out.println("Remaining parachutes: " + count);
        } else {
            System.out.println("No parachutes left to deploy.");
        }
    }

    /**
     * Checks if a parachute is currently deployed.
     * @return true if a parachute is deployed, false otherwise.
     */
    public boolean isDeployed() {
        return isDeployed;
    }

    /**
     * Adds one parachute to the count.
     */
    public void buyOneParachute() {
        count++;
    }

    /**
     * Returns the current count of available parachutes.
     * @return the number of available parachutes.
     */
    public int getCount() {
        return count;
    }
}
