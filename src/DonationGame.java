import java.util.random.*;
import java.util.stream.DoubleStream;
import java.util.Arrays;
import java.text.DecimalFormat;

// Implementation of the basic donation game as described by Nowak and Sigmund
// M. A. Nowak and K. Sigmund, ‘Evolution of indirect reciprocity by
// image scoring’, Nature, 393, 573–577, (1998).

public class DonationGame {
    int n; // number of agents
    int m; // number of interactions per generation
    double q; // probability of observing an interaction
    double mr; // mutation rate
    int[] strategies; // array to store the strategies in the population
    double[][] imageScores; // array for storing the current image scores
    double[] rewards; // array for storing the current rewards
    boolean preventNegativePayoffs; // whether to prevent negative rewards
    // NB: Nowak and Sigmund (NS) use the addition of 0.1 to both agents in an interaction to prevent negative payoffs 
    int coop_count;
    protected final static double b = 1; // benefit from receiving a donation
    protected final static double c = 0.1; // cost of donation

    protected static RandomGenerator rand = RandomGenerator.of("Xoroshiro128PlusPlus");
    protected static final DecimalFormat df = new DecimalFormat("0.00");


    public DonationGame(int n, int m, double q, double mr, boolean preventNegativePayoffs) {        
        this.n = n;
        this.m = m;
        this.q = q;
        this.mr = mr;
        this.preventNegativePayoffs = preventNegativePayoffs;
        this.strategies = new int[n];
        for (int i = 0; i < strategies.length; i++) {
            strategies[i] = rand.nextInt(-5, 7);
        }
        this.imageScores = new double[n][n];
        this.rewards = new double[n];
        this.coop_count = 0;
    }

    public int[] getStrategies() {
        return strategies;
    }

    public double[][] getImageScores() {
        return imageScores;
    }

    public double[] getRewards() {
        return rewards;
    }

    public double getAverageReward() {
        return Arrays.stream(rewards).average().orElse(Double.NaN);
    }

    public double getRewardVariance() {
        double mean = getAverageReward();
        double squaredDifferences = 0;

        for (double value : rewards) {
            double diff = value - mean;
            squaredDifferences += diff * diff;
        }

        return squaredDifferences / rewards.length; // population variance
    }

    private double getImageScore(int maintainer, int target) {
        return imageScores[maintainer][target];
    }

    static void printMatrix(double[][] grid) {
        for(int r=0; r<grid.length; r++) {
           for(int c=0; c<grid[r].length; c++)
               System.out.print(df.format(grid[r][c]) + " ");
           System.out.println();
        }
    }
    static void printMatrix(double[] grid) {
        for(int c=0; c<grid.length; c++)
            System.out.print(df.format(grid[c]) + " ");
        System.out.println();
    }
    
    protected void incrementImage(int maintainer, int donor) {
        imageScores[maintainer][donor] += 1.0;
        if (imageScores[maintainer][donor] > 5.0) {
            imageScores[maintainer][donor] = 5.0;    
        }
    }

    protected void incrementImage(int maintainer, int donor, double delta) {
        imageScores[maintainer][donor] += delta;
        if (imageScores[maintainer][donor] > 5.0) {
            imageScores[maintainer][donor] = 5.0;    
        }
    }

    protected void decrementImage(int maintainer, int donor) {
        imageScores[maintainer][donor] -= 1.0;
        if (imageScores[maintainer][donor] < -5.0) {
            imageScores[maintainer][donor] = -5.0;    
        }
    }

    private void cooperateImageUpdate(int donor) {
        if (q < 1.0) {
            for (int i = 0; i < n; i++) {
                if (i != donor && rand.nextDouble() < q) {
                    incrementImage(i, donor);
                }
            }
        }
        else {
            for (int i = 0; i < n; i++) {
                if (i != donor) {
                    incrementImage(i, donor);
                }
            }
        }
    }

    private void defectImageUpdate(int donor) {
        if (q < 1.0) {  
            for (int i = 0; i < n; i++) {
                if (i != donor && rand.nextDouble() < q) {
                    decrementImage(i, donor);
                }
            }
        }
        else {
            for (int i = 0; i < n; i++) {
                if (i != donor) {
                    decrementImage(i, donor);
                }
            }
        }
    }

    public void tick() {
        for (int i = 0; i < m; i++) {
            int donor = rand.nextInt(n);
            int recipient = rand.nextInt(n);
            while (recipient == donor) {
                recipient = rand.nextInt(n);
            }
            double imageScore = getImageScore(donor, recipient);
            if (imageScore >= strategies[donor]) {
                rewards[donor] -= c;
                rewards[recipient] += b;
                cooperateImageUpdate(donor);
                coop_count += 1;
            } else {
                defectImageUpdate(donor);
            }
            if (preventNegativePayoffs) {
                rewards[donor] += 0.1;
                rewards[recipient] += 0.1;
            }
        }
    }

    protected double[] scaleRewards() {
        double[] result = rewards;
        double min = Arrays.stream(rewards).summaryStatistics().getMin();
        if (min < 0.0) {
            double delta = min;
            result = Arrays.stream(rewards).map(i -> i - delta).toArray();
            min = Arrays.stream(result).summaryStatistics().getMin();
        } 
        if (min == 0) {
            double delta = 0.1;
            result = Arrays.stream(result).map(i -> i + delta).toArray();
        }
        return result;
    }

    public void mutation() {
        if (mr == 0.0) return;
        for (int i = 0; i < n; i++) {
            if (rand.nextDouble() < mr) {
                int s = rand.nextInt(-5, 7);
                while (s == strategies[i]) {
                    s = rand.nextInt(-5, 7);
                }
                strategies[i] = s;
            }
        }
    }

    public static int weightedRandomChoice(double[] rewardsScaled, double totalReward) {
        int selected = 0;
        double total = rewardsScaled[0];
        for (int i = 1; i < rewardsScaled.length; i++) {
            total += rewardsScaled[i];            
            if( rand.nextDouble() <= (rewardsScaled[i] / total)) selected = i;
        }
        return selected;    
    }
    
    public void rouletteWheelSelection() {
        double[] rewardsScaled = scaleRewards();
        double totalReward = DoubleStream.of(rewardsScaled).sum();
        if (totalReward == 0.0) {
            System.out.println("Error: zero reward. Something went wrong.");
            System.out.println("rewardScaled: " + Arrays.toString(rewardsScaled));
            System.out.println("strategies: " + Arrays.toString(strategies));
            System.exit(1);
        }
        int[] newStrategies = new int[n];
        for (int i = 0; i < n; i++) {
            newStrategies[i] = strategies[weightedRandomChoice(rewardsScaled, totalReward)];
        }
        strategies = newStrategies;
        Arrays.fill(rewards, 0.0);
        Arrays.stream(imageScores).forEach(a -> Arrays.fill(a, 0));
    }

    // simple main method to test things are working with toy instantiation 
    public static void main(String[] args) throws Exception {
        DonationGame game = new DonationGame(10, 30, 1.0, 0.001, false);
        int generations = 10;
        for (int i = 0; i < generations; i++) {
            System.out.println("g " + i);
            game.tick();
            System.out.println("strategies (before update) " + Arrays.toString(game.strategies));
            System.out.println("rewards (before update) " + Arrays.toString(game.rewards));
            game.rouletteWheelSelection();
            game.mutation();
            System.out.println("strategies (after update) " + Arrays.toString(game.strategies));
            System.out.println("rewards (after update) " + Arrays.toString(game.rewards));
        }
    }
}
