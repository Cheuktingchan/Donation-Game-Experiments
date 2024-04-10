import java.util.random.*;
import java.util.stream.DoubleStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.text.DecimalFormat;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.io.*;

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
    boolean local; // whether to constrain local reproduction
    // NB: Nowak and Sigmund (NS) use the addition of 0.1 to both agents in an interaction to prevent negative payoffs 
    int[] coop_count; // number of donations used to calculate cooperation rate
    int[] act_count; // number of interations used to calculate cooperation rate
    int numConsecK; // number of consecutive generations that have reached a norm of K

    // 0 = Fully Connected, 1 = Bipartite, 2 = Random, 3 = Community, 4 = Scale-Free, 5 = Small-World
    double[] network; // network[0] specifies network. Optional: network[1] specifies n, network[2] specifies p (see desc. for -net option)
    int[][] outPartShape;
    boolean[][] adjMat; // donor-recipient edges
    ArrayList<int[]> edgeList;

    double randomP;
    int numCommunities;
    double communityP;
    int initialNodes;
    int neighborDistance;
    double rewiringP;

    protected final static double b = 1; // benefit from receiving a donation
    protected final static double c = 0.1; // cost of donation

    protected static RandomGenerator rand = RandomGenerator.of("Xoroshiro128PlusPlus");
    protected static final DecimalFormat df = new DecimalFormat("0.00");
    
    private static Set<Integer> preferentialAttachment(Map<Integer, Set<Integer>> adjacencyList, int newNode, Random random) {
        Set<Integer> existingNodes = adjacencyList.keySet();
        double totalDegree = 0;

        for (int existingNode : existingNodes) {
            totalDegree += adjacencyList.get(existingNode).size();
        }

        Set<Integer> newConnections = new HashSet<>();

        while (newConnections.size() == 0) {
            for (int existingNode : existingNodes) {
                double probability = adjacencyList.get(existingNode).size() / totalDegree;
                if (random.nextDouble() < probability) {
                    newConnections.add(existingNode);
                }
            }
        }

        return newConnections;
    }

    private static void printMatrix(boolean[][] matrix) {
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                System.out.print(matrix[i][j] ? "1 " : "0 ");
            }
            System.out.println();
        }
    }

    private static int getNumEdges(boolean[][] matrix) {
        int total = 0;
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                if (matrix[i][j]){
                    total += 1;
                }
            }
        }
        return total/2;
    }

    public DonationGame(int n, int m, double q, double mr, boolean preventNegativePayoffs, double[] network, int[][] outPartShape) {        
        this.n = n;
        this.m = m;
        this.q = q;
        this.mr = mr;
        this.network = network; // network is a array of double params specified below.
        this.preventNegativePayoffs = preventNegativePayoffs;
        this.local = false;
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
        this.edgeList = new ArrayList<int[]>();
        
        // default network parameters: 
        this.randomP = 0.5; // random(p)

        this.numCommunities = 4; // community(n,p)
        this.communityP = 0.5;

        this.initialNodes = 3; // scale-free(n)

        this.neighborDistance = 4; // small-world(n,p)
        this.rewiringP = 0.2;

        if (network[0] == 1){ // bipartite
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    if (i == j){
                        this.adjMat[i][j] = false;
                        this.adjMat[j][i] = false;
                    }else{
                        boolean samePart = (i < n/2 && j >= n/2) || (i >= n/2 && j < n/2);
                        this.adjMat[i][j] = samePart;
                        this.adjMat[j][i] = samePart;
                    }
                }
            }
        }else if (network[0] == 2){ // random
            if (network.length >= 2){
                randomP = network[1];
            }
            Random random = new Random();
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    if (i == j){
                        this.adjMat[i][j] = false;
                        this.adjMat[j][i] = false;
                    }else{
                        boolean isConnected = random.nextFloat() < randomP; // adjust probability of connection
                        this.adjMat[i][j] = isConnected;
                        this.adjMat[j][i] = isConnected;
                    }
                }
            }
        }else if (network[0] == 3){ // community
            if (network.length >= 2){
                numCommunities = (int) network[1];
            }
            if (network.length == 3){
                communityP = network[2];
            }
            Random random = new Random();
    
            // connect nodes within communities
            int nodesPerCommunity = n / numCommunities;
            for (int community = 0; community < numCommunities; community++) {
                for (int i = community * nodesPerCommunity; i < (community + 1) * nodesPerCommunity; i++) {
                    for (int j = i + 1; j < (community + 1) * nodesPerCommunity; j++) {
                        this.adjMat[i][j] = true;
                        this.adjMat[j][i] = true;
                    }
                }
            }

            // connect nodes between communities on a probability
            for (int i = 0; i < n; i++) {
                for (int j = i + 1; j < n; j++) {
                    if (!this.adjMat[i][j]) { // only connect if not already connected within the community
                        boolean isConnected = random.nextFloat() < communityP;
                        if (isConnected) {
                            this.adjMat[i][j] = true;
                            this.adjMat[j][i] = true;
                        }
                    }
                }
            }
        } else if (network[0] == 4){ // scale-free
            if (network.length >= 2){
                initialNodes = (int) network[1];
            }
            Random random = new Random();
            Map<Integer, Set<Integer>> adjList = new HashMap<>();

            // initialize the network with a small number of nodes
            for (int i = 0; i < initialNodes; i++) {
                adjList.put(i, new HashSet<>());
            }
            for (int i = 0; i < initialNodes; i++) {
                for (int j = 0; j < initialNodes; j++) {
                    if (i != j) {
                        adjList.get(i).add(j);
                        adjList.get(j).add(i);
                    }
                }
            }
    
            // grow the network using the Barabási–Albert model
            for (int i = initialNodes; i < n; i++) {
                Set<Integer> newConnections = preferentialAttachment(adjList, i, random);
                adjList.put(i, newConnections);

                // update existing nodes with new connections
                for (int connection : newConnections) {
                    adjList.get(connection).add(i);
                }
            }

            //convert to adjacency matrix
            for (int i = 0; i < n; i++) {
                Set<Integer> neighbors = adjList.get(i);
    
                if (neighbors != null) {
                    for (int neighbor : neighbors) {
                        this.adjMat[i][neighbor] = true;
                        this.adjMat[neighbor][i] = true;
                    }
                }
            }
        }else if (network[0] == 5){ // small-world
            if (network.length >= 2){
                neighborDistance = (int) network[1];
            }
            if (network.length >= 3){
                rewiringP = network[2];
            }
            // initialize the regular ring lattice
            for (int i = 0; i < n; i++) {
                for (int j = i - neighborDistance; j <= i + neighborDistance; j++) {
                    if (j != i && j >= 0 && j < n) {
                        this.adjMat[i][j] = true;
                        this.adjMat[j][i] = true;
                    }
                }
            }

            // rewire edges with a certain probability
            Random random = new Random();
            for (int i = 0; i < n; i++) {
                for (int neighbor = 0; neighbor < n; neighbor++) {
                    if (this.adjMat[i][neighbor] && random.nextDouble() < rewiringP) {
                        int newNeighbor;
                        do {
                            newNeighbor = random.nextInt(n);
                        } while (newNeighbor == i || this.adjMat[i][newNeighbor]);

                        this.adjMat[i][neighbor] = false;
                        this.adjMat[neighbor][i] = false;

                        this.adjMat[i][newNeighbor] = true;
                        this.adjMat[newNeighbor][i] = true;
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

        // convert adjacency matrix to edgeList
        for (int i = 0; i < this.adjMat.length; i++) {
            for (int j = i + 1; j < this.adjMat[i].length; j++) {
                if (this.adjMat[i][j] == true) {
                    this.edgeList.add(new int[]{i, j});
                    this.edgeList.add(new int[]{j, i}); // both edges - directed
                }
            }
        }
        System.out.println("TEXT");
        System.out.println("#edges:");
        System.out.println(getNumEdges(this.adjMat));
        System.out.println();
/*         try{
            PrintWriter pr = new PrintWriter(new FileWriter("edges.txt", true));
            pr.println(getNumEdges(this.adjMat));
            pr.close();
        }catch (IOException e){
            System.err.println("File not found: " + e.getMessage());
        } */
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
            int[] randomEdge = edgeList.get(rand.nextInt(edgeList.size()));
    
            int donor = randomEdge[0];
            int recipient = randomEdge[1];
        
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

    protected double[] scaleRewards(double[] rwrds) {
        double[] result = rwrds;
        double min = Arrays.stream(rwrds).summaryStatistics().getMin();
        if (min < 0.0) {
            double delta = min;
            result = Arrays.stream(rwrds).map(i -> i - delta).toArray();
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
        if (local){
            /*             int cumInd = 0;
                        int[] newStrategies = new int[n];
                        for (int j = 0; j < outPartShape.length; j++){
                            cumInd += outPartShape[j].length;
                            double[] rewardsScaled = scaleRewards(Arrays.copyOfRange(rewards, (cumInd-outPartShape[j].length), cumInd));
                            double totalReward = DoubleStream.of(rewardsScaled).sum();
                            if (totalReward == 0.0) {
                                System.out.println("Error: zero reward. Something went wrong.");
                                System.out.println("rewardScaled: " + Arrays.toString(rewardsScaled));
                                System.out.println("strategies: " + Arrays.toString(strategies));
                                System.exit(1);
                            }
                            //System.out.println("newrewards: " + Arrays.toString(Arrays.copyOfRange(rewards, (cumInd-outPartShape[j].length), cumInd)));
                            for (int i = (cumInd-outPartShape[j].length); i < cumInd; i++) {
                                newStrategies[i] = strategies[(cumInd-outPartShape[j].length) + weightedRandomChoice(rewardsScaled, totalReward)];
                            }
                            //System.out.println("newstrategies: " + Arrays.toString(newStrategies));
                        }
                        strategies = newStrategies;
                        //System.out.println("strategies: " + Arrays.toString(strategies));
                        Arrays.fill(rewards, 0.0);
                        Arrays.stream(imageScores).forEach(a -> Arrays.fill(a, 0)); */
            int[] newStrategies = new int[n];
            for (int i = 0; i < n; i++) {
                ArrayList<Integer> thisRewardsIndices = new ArrayList<Integer>();
                ArrayList<Double> thisRewardsList = new ArrayList<Double>(); // rewards to scale for this agent (if they are connected)

                for (int r=0; r < n; r++){
                    if (adjMat[i][r]){ // if connected
                        thisRewardsList.add(rewards[r]);
                        thisRewardsIndices.add(r);
                    }
                }

                double[] rewardsScaled = scaleRewards(thisRewardsList.stream().mapToDouble(Double::doubleValue).toArray());
                double totalReward = DoubleStream.of(rewardsScaled).sum();
                if (totalReward == 0.0) {
                    System.out.println("Error: zero reward. Something went wrong.");
                    System.out.println("rewardScaled: " + Arrays.toString(rewardsScaled));
                    System.out.println("strategies: " + Arrays.toString(strategies));
                    System.exit(1);
                }
                newStrategies[i] = strategies[thisRewardsIndices.get(weightedRandomChoice(rewardsScaled, totalReward))];
            }
            strategies = newStrategies;
            Arrays.fill(rewards, 0.0);
            Arrays.stream(imageScores).forEach(a -> Arrays.fill(a, 0));
        }else{
            double[] rewardsScaled = scaleRewards(rewards);
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
    }

    public int getSingleKNormEmerged (int generations) { // -100 means no norm emerged, -5 to 6 means k is a norm
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

    public void setLocalTrue(){
        local = true;
    }
    // simple main method to test things are working with toy instantiation 
    public static void main(String[] args) throws Exception {
        DonationGame game = new DonationGame(10, 30, 1.0, 0.001, false, new double[]{0}, new int[1][]);
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
