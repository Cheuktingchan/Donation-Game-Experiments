import java.util.random.*;
import java.util.stream.DoubleStream;
import java.util.Arrays;
import java.util.Random;
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
    int[] coop_count; // number of donations used to calculate cooperation rate
    int[] act_count; // number of interations used to calculate cooperation rate
    int numConsecK; // number of consecutive generations that have reached a norm of K
    int network; // 0 = Fully Connected, 1 = Bipartite, 2 = Random, 3 = Community, 4 = Scale-Free, 5 = Small-World
    int[][] outPartShape;
    boolean[][] adjMat; // donor-recipient edges
    protected final static double b = 1; // benefit from receiving a donation
    protected final static double c = 0.1; // cost of donation

    protected static RandomGenerator rand = RandomGenerator.of("Xoroshiro128PlusPlus");
    protected static final DecimalFormat df = new DecimalFormat("0.00");


    public DonationGame(int n, int m, double q, double mr, boolean preventNegativePayoffs, int network, int[][] outPartShape) {        
        this.n = n;
        this.m = m;
        this.q = q;
        this.mr = mr;
        this.network = network;
        this.preventNegativePayoffs = preventNegativePayoffs;
        this.strategies = new int[n];
        for (int i = 0; i < strategies.length; i++) {
            strategies[i] = rand.nextInt(-5, 7);
        }
        this.imageScores = new double[n][n];
        this.rewards = new double[n];
        this.outPartShape = outPartShape;
        this.coop_count = new int [outPartShape.length];
        this.act_count = new int [outPartShape.length];
        this.adjMat = new boolean[n][n];
        if (network == 1){ // bipartite
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    if (i == j){
                        this.adjMat[i][j] = false;
                        this.adjMat[j][i] = false;
                    }else{
                        boolean samePart = (i < n/2 && j < n/2) || (i >= n/2 && j >= n/2);
                        this.adjMat[i][j] = samePart;
                        this.adjMat[j][i] = samePart;
                    }
                }
            }
        }else if (network == 2){ // random
            Random random = new Random();
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    if (i == j){
                        this.adjMat[i][j] = false;
                        this.adjMat[j][i] = false;
                    }else{
                        boolean isConnected = random.nextBoolean();
                        this.adjMat[i][j] = isConnected;
                        this.adjMat[j][i] = isConnected;
                    }
                }
            }
        }else{ // fully connected
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    if (i == j){
                        this.adjMat[i][j] = false;
                        this.adjMat[j][i] = false;
                    }else{
                        this.adjMat[i][j] = true;
                        this.adjMat[j][i] = true;
                    }
                }
            }
        }
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

    public double getAverageReward(int rangeStart, int rangeEnd) {
        return Arrays.stream(Arrays.copyOfRange(rewards, rangeStart, rangeEnd)).average().orElse(Double.NaN);
    }

    public double getRewardVariance(int rangeStart, int rangeEnd) {
        double mean = getAverageReward(rangeStart, rangeEnd);
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
            while (!adjMat[donor][recipient]) { // repick condition
                recipient = rand.nextInt(n);
            }
            int cumInd = 0;
            for (int j = 0; j < outPartShape.length; j++){
                cumInd += outPartShape[j].length;
                if (cumInd - outPartShape[j].length <= donor && donor < cumInd){
                    act_count[j] += 1;
                }
            }
            double imageScore = getImageScore(donor, recipient);
            if (imageScore >= strategies[donor]) {
                rewards[donor] -= c;
                rewards[recipient] += b;
                cooperateImageUpdate(donor);
                cumInd = 0;
                for (int j = 0; j < outPartShape.length; j++){
                    cumInd += outPartShape[j].length;
                    if (cumInd - outPartShape[j].length <= donor && donor < cumInd){
                        coop_count[j] += 1;
                    }
                }
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

    public int getSingleKNormEmerged (int generations) { // -100 means no norm, -5 to 6 means k is a norm
        int norm = -100;
        int[] stratCounts = new int[12];
        for (int i = 0; i < 12; i++) {
            stratCounts[i] = 0;
        }
        for (int i = 0; i < n; i++) {
            stratCounts[strategies[i] + 5] += 1;
        }
        for (int i = 0; i < 12; i++) {
            if (stratCounts[i] > 0.98 * n){
                norm = i - 5;
                numConsecK += 1;
                break;
            }
        }
        if (norm == -100) {
            numConsecK = 0;
        }

        if (numConsecK > 100){
            return norm;
        }
        return -100;
    }

    // simple main method to test things are working with toy instantiation 
    public static void main(String[] args) throws Exception {
        DonationGame game = new DonationGame(10, 30, 1.0, 0.001, false, 0, new int[1][]);
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
