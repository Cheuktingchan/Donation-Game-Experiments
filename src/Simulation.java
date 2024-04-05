import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.text.DecimalFormat;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

// Class for running the simulations described by
// N. Griffiths and N. Oren, 'Generosity and the Emergence of Forgiveness in the Donation Game', in
// Proceedings of 26th European Conference on Artificial Intelligence, (2023)

public class Simulation {

    private static final DecimalFormat df = new DecimalFormat("0.000");

    public static void main(String[] args) throws Exception {
        long startTime = System.currentTimeMillis();
        Options options = new Options();
        options.addOption(new Option("h", "help", false, "Display this message"));
        options.addOption(new Option("n", "size", true, "Population size"));
        options.addOption(new Option("m", "pairs", true, "Number of donor-recipient pairs per generation"));
        options.addOption(new Option("q", "observation", true, "Probability of observing an interaction"));
        options.addOption(new Option("mr", "mutation", true, "Mutation rate"));
        options.addOption(new Option("ea", "actionNoise", true, "Probability of donation action failing (action noise)"));
        options.addOption(new Option("ep", "perceptionNoise", true, "Probability of an observer incorrectly perceiving donor\'s action (perception noise)"));
        options.addOption(new Option("ns", "preventNegativePayoffs", false, "prevent negative payoffs (use base Nowak and Sigmund formulation)"));
        options.addOption(new Option("generosity", false, "Enable generosity [Schmid et al., Scientific Reports, 2021]"));
        options.addOption(new Option("g1", true, "Probability of assessment generosity [Schmid et al.]"));
        options.addOption(new Option("g2", true, "Probability of action generosity [Schmid et al.]"));
        options.addOption(new Option("fa", "forgiveness_action", false, "Enable action forgiveness"));
        options.addOption(new Option("fr", "forgiveness_reputation", false, "Enable reputation (assessment) forgiveness"));
        options.addOption(new Option("g", "generations", true, "Generations to run simulation for"));
        options.addOption(new Option("endN", false, "End simulation when a single strategy norm is reached"));
        options.addOption(new Option("local", false, "Local reproduction based on -outPart partitions"));
        options.addOption(new Option("intervals", true, "Data collected at intervals of generations"));
        Option outPartOption = Option.builder("outPart")
            .hasArgs()
            .desc("Partition indices for output data")
            .build();
        options.addOption(outPartOption);
        Option net = Option.builder("net")
        .hasArgs()
        .desc("<network> \\ <parameter1> \\\n" + //
                        "     <parameter2>: Network parameters.\\\\\n" + //
                        "     Fully connected (network=0): No parameters.\\\\\n" + //
                        "     Bipartite (network=1): No parameters.\\\\\n" + //
                        "     Random (network=2): Parameter1 specifies connection probability (decimal from 0-1).\\\\\n" + //
                        "     Community (network=3): Parameter1 specifies number of communities (integer divisible by n), and parameter2 specifies external link probability (decimal from 0-1).\\\\\n" + //
                        "     Scale-free (network=4):\n" + //
                        "     Parameter1 specifies the number of initial nodes (integer less than n).\\\\\n" + //
                        "     Small-world (network=5):\n" + //
                        "     Parameter1 specifies the neighbour distance (integer less than n), and parameter2 specifies rewiring probability (decimal from 0-1).")
        .build();
        options.addOption(net);

        options.addOption(new Option("quiet", false, "Run with minimal output"));
        CommandLineParser parser = new DefaultParser(false);
        CommandLine cmd = parser.parse(options, args);
        HelpFormatter formatter = new HelpFormatter();
        
        int n;
        int m;
        double q;
        double mr;
        double ea;
        double ep;
        boolean preventNegativePayoffs;
        boolean generosity;
        double g1;
        double g2;
        boolean fa;
        boolean fr;
        int generations;
        boolean quiet;
        double[] network;
        boolean endN;
        boolean local;
        int intervals;
        int[][] outPartShape;
        if (cmd.hasOption("h")) {
            formatter.printHelp("Simulation", options);
            System.out.println();
            System.exit(0);
        }
        if(cmd.hasOption("endN")) {
            endN = true;
        } else {
            endN = false;
        }
        if(cmd.hasOption("local")) {
            local = true;
        } else {
            local = false;
        }
        if(cmd.hasOption("net")) {
            String[] netStrs = cmd.getOptionValues("net");
            network = new double[netStrs.length];
            for (int i = 0; i < netStrs.length; i++){
                network[i] = Double.parseDouble(netStrs[i]);
            }
        } else {
            network = new double[]{0};
        }
        if(cmd.hasOption("intervals")) {
            intervals = Integer.parseInt(cmd.getOptionValue("intervals"));
        } else {
            intervals = 0;
        }
        if(cmd.hasOption("n")) {
            n = Integer.parseInt(cmd.getOptionValue("n"));
        } else {
            n = 100;
        }
        if(cmd.hasOption("outPart")) {
            String[] outPartStrs = cmd.getOptionValues("outPart");
            outPartShape = new int[outPartStrs.length][];
            System.out.print("partstr");
            System.out.print(outPartStrs.length);
            for(int i = 0; i < outPartStrs.length; i++) {
                System.out.print(outPartStrs[i]);
                outPartShape[i] = new int[Integer.parseInt(outPartStrs[i])];
            }
        } else {
            outPartShape = new int[1][n];
        }
        if(cmd.hasOption("m")) {
            m = Integer.parseInt(cmd.getOptionValue("m"));
        } else {
            m = 300;
        }
        if(cmd.hasOption("q")) {
            q = Double.parseDouble(cmd.getOptionValue("q"));
        } else {
            q = 1.0;
        }
        if(cmd.hasOption("mr")) {
            mr = Double.parseDouble(cmd.getOptionValue("mr"));
        } else {
            mr = 0.001;
        }
        if(cmd.hasOption("ea")) {
            ea = Double.parseDouble(cmd.getOptionValue("ea"));
        } else {
            ea = 0.0;
        }
        if(cmd.hasOption("ep")) {
            ep = Double.parseDouble(cmd.getOptionValue("ep"));
        } else {
            ep = 0.0;
        }
        if (cmd.hasOption("ns")) {
            preventNegativePayoffs = true;
        } else {
            preventNegativePayoffs = false;
        }
        if (cmd.hasOption("generosity")) {
            generosity = true;
        } else {
            generosity = false;
        }
        if(cmd.hasOption("g1")) {
            g1 = Double.parseDouble(cmd.getOptionValue("g1"));
            if (!generosity) {
                System.out.println("Error: attempted to set g1 without enabling generosity");
                formatter.printHelp("Simulation", options);
                System.out.println();
                System.exit(1);
            }
        } else {
            g1 = 0.0;
        }
        if(cmd.hasOption("g2")) {
            g2 = Double.parseDouble(cmd.getOptionValue("g2"));
            if (!generosity) {
                System.out.println("Error: attempted to set g2 without enabling generosity");
                formatter.printHelp("Simulation", options);
                System.out.println();
                System.exit(1);
            }
        } else {
            g2 = 0.0;
        }
        if (cmd.hasOption("fa")) {
            fa = true;
        } else {
            fa = false;
        }
        if (cmd.hasOption("fr")) {
            fr = true;
        } else {
            fr = false;
        }
        if(cmd.hasOption("g")) {
            generations = Integer.parseInt(cmd.getOptionValue("g"));
        } else {
            generations = 100;
        }
        if (cmd.hasOption("quiet")) {
            quiet = true;
        } else {
            quiet = false;
        }
        if (!quiet) {
            System.out.println("Configuration: n=" + n + " m=" + m + " q=" + q + " mr=" + df.format(mr) 
            + " ea=" + df.format(ea) + " ep=" + df.format(ep)
            + " ns=" + preventNegativePayoffs
            + " generosity=" + generosity + " g1=" + g1 + " g2=" + g2
            + " fa=" + fa + " fr=" + fr  
            + " generations=" + generations
            + " network=" + network
            + " intervals=" + intervals);
        }

        String dataDir = "data";
        StringBuilder sb = new StringBuilder();
        sb.append("n" + n);
        sb.append("_m" + m);
        sb.append("_q" + q);
        sb.append("_mr" + df.format(mr));
        sb.append("_ea" + df.format(ea));
        sb.append("_ep" + df.format(ep));
        sb.append("_ns" + (preventNegativePayoffs ? "True" : "False"));
        sb.append("_gen" + (generosity ? "True" : "False"));
        if (generosity) {
            sb.append("_g1" + g1);
            sb.append("_g2" + g2);
        }
        sb.append("_fa" + (fa ? "True" : "False"));
        sb.append("_fr" + (fr ? "True" : "False"));
        sb.append("_g" + generations );
        sb.append("_net" + Arrays.toString(network) );
        sb.append("_intervals" + intervals);
        sb.append("_local" + (local ? "True" : "False") );
        String fileName = sb.toString();
        // output paths:
        List<Map<String, Path>> outPartPaths = new ArrayList<>();
        int cumInd = 0;
        for (int i = 0; i < outPartShape.length; i++){
            cumInd += outPartShape[i].length;
            String curPart = (cumInd-outPartShape[i].length) + "-" + cumInd;
            Map<String, Path> pathMap = new HashMap<>();
            pathMap.put("coopRate", Paths.get(".", dataDir, fileName + "_coop-rate" + curPart + ".csv"));
            pathMap.put("rewardVar", Paths.get(".", dataDir, fileName + "_reward-variances" + curPart + ".csv"));
            pathMap.put("rewardAv", Paths.get(".", dataDir, fileName + "_reward-averages" + curPart + ".csv"));
            pathMap.put("rewardFin", Paths.get(".", dataDir, fileName + "_reward-final" + curPart + ".csv"));
            pathMap.put("kAvFreq", Paths.get(".", dataDir, fileName + "_kAvFreq" + curPart + ".csv"));
            pathMap.put("kFinFreq", Paths.get(".", dataDir, fileName + "_kFinFreq" + curPart + ".csv"));
            pathMap.put("fAvFreq", Paths.get(".", dataDir, fileName + "_fAvFreq" + curPart + ".csv"));
            outPartPaths.add(pathMap);
        }
        Path coopRatePath = Paths.get(".", dataDir, fileName + "_coop-rate.csv");
        Path rewardVarPath = Paths.get(".", dataDir, fileName + "_reward-variances.csv");
        Path rewardAvPath = Paths.get(".", dataDir, fileName + "_reward-averages.csv");
        Path rewardFinPath = Paths.get(".", dataDir, fileName + "_reward-final.csv");
        Path kAvFreqPath = Paths.get(".", dataDir, fileName + "_kAvFreq.csv");
        Path kFinFreqPath = Paths.get(".", dataDir, fileName + "_kFinFreq.csv");
        Path fAvFreqPath = Paths.get(".", dataDir, fileName + "_fAvFreq.csv");
        if (!quiet) {
            System.out.println("Output file for cooperation rate: " + coopRatePath);
            System.out.println("Output file for reward variance: " + rewardVarPath);
            System.out.println("Output file for reward averages: " + rewardAvPath);
            System.out.println("Output file for final averages: " + rewardFinPath);
            System.out.println("Output file for average frequency of donation strategies: " + kAvFreqPath);
            System.out.println("Output file for final frequency of donation strategies: " + kFinFreqPath);
            System.out.println("Output file for frequency of forgiveness strategies: " + fAvFreqPath);
        }

        DonationGame game;
        if (!generosity && !fa && !fr && ea == 0.0 && ep == 0.0) {
            System.out.println("Using DonationGame, i.e., without noise, generosity or forgiveness");
            game = new DonationGame(n, m, q, mr, preventNegativePayoffs, network, outPartShape);
            if (local){
                game.setLocalTrue();
            }
        } else {
            if (generosity && (g1 > 0.0 || g2 > 0.0)) {
                if (fa || fr) {
                    System.out.println("Error: Cannot use generosity alongside forgiveness: disable either generosity or forgiveness.");
                    System.exit(1);
                }
                System.out.println("Using GenerosityDonationGame, i.e., with noise and generosity");
                game = new GenerosityDonationGame(n, m, q, mr, preventNegativePayoffs, ea, ep, g1, g2, network, outPartShape);
            } else if (fa || fr) {
                System.out.println("Using ForgivenessDonationGame, i.e., with noise and forgiveness");
                game = new ForgivenessDonationGame(n, m, q, mr, preventNegativePayoffs, ea, ep, fa, fr, network, outPartShape);                
            } else {
                System.out.println("Using NoisyDonationGame, i.e., with noise");
                game = new NoisyDonationGame(n, m, q, mr, preventNegativePayoffs, ea, ep, network, outPartShape);
            }
        }

        double[][] rewardAverages = new double [outPartShape.length][generations];
        double[][] varianceRewards = new double [outPartShape.length][generations];

        ArrayList<TreeMap<Integer,Integer>> k_counts = new ArrayList<TreeMap<Integer,Integer>>();
        ArrayList<TreeMap<Integer,Integer>> k_counts_final = new ArrayList<TreeMap<Integer,Integer>>();
        ArrayList<TreeMap<Integer,Double>> av_k_frequency = new ArrayList<TreeMap<Integer,Double>>();
        ArrayList<TreeMap<Integer,Double>> fin_k_frequency = new ArrayList<TreeMap<Integer,Double>>();

        // Note, this is only used for forgiveness games
        // This is an inefficient hack to inspect forgiveness strategy frequencies, and
        // if used for more than occasional evaluation should be refactored. 
        Map<Double,Integer> f_counts = new TreeMap<Double,Integer>();

        cumInd = 0;
        for (int i = 0; i < outPartShape.length; i++){
            cumInd += outPartShape[i].length;
            TreeMap<Integer,Integer> cur_counts = new TreeMap<Integer,Integer>();
            for (int k : Arrays.copyOfRange(game.strategies, (cumInd-outPartShape[i].length), cumInd)) {
                if (cur_counts.containsKey(k)) {
                    cur_counts.put(k, cur_counts.get(k) + 1);
                } else {
                    cur_counts.put(k, 1);
                }
            }
            k_counts.add(cur_counts);
        }

        for (int i = 0; i < generations; i++) {
            game.tick();
            if (endN){
                int kEmerged = game.getSingleKNormEmerged(generations);
                if (kEmerged != -100){
                    System.out.println("Result: Norm " + kEmerged + " emerged in " + i + " generations");
                    Path singleNormPath = Paths.get(".", dataDir, fileName + "_single-norm.csv");
                    int[] singleNormResult = {kEmerged, i}; // k norm, generations taken
                    Files.writeString(singleNormPath, Arrays.toString(singleNormResult).replace("[", "").replace("]", "") + System.lineSeparator(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
                    break;
                }
            }
            
            cumInd = 0;
            for (int j = 0; j < outPartShape.length; j++){
                cumInd += outPartShape[j].length;
                
                // reward outputs for each partition
                rewardAverages[j][i] = game.getAverageReward((cumInd-outPartShape[j].length), cumInd);
                varianceRewards[j][i] = game.getRewardVariance((cumInd-outPartShape[j].length), cumInd);

                for (int k : Arrays.copyOfRange(game.strategies, (cumInd-outPartShape[j].length), cumInd)) {
                    if (k_counts.get(j).containsKey(k)) {
                        k_counts.get(j).put(k, k_counts.get(j).get(k) + 1);
                    } else {
                        k_counts.get(j).put(k, 1);
                    }
                }
            }

            if (game instanceof ForgivenessDonationGame) {
                for (double f : ((ForgivenessDonationGame) game).forgivenessStrategies) {
                    if (f_counts.containsKey(f)) {
                        f_counts.put(f, f_counts.get(f) + 1);
                    } else {
                        f_counts.put(f, 1);
                    }
                }
            }
            game.rouletteWheelSelection();
            game.mutation();
            
            // for intervals: the reward variance is final, usually it is cumulative (average of all gens)

            if (intervals != 0 && i % intervals == 0) {

                cumInd = 0;
                for (int j = 0; j < outPartShape.length; j++){
                    cumInd += outPartShape[j].length;
                    TreeMap<Integer,Integer> cur_counts = new TreeMap<Integer,Integer>();
                    for (int k : Arrays.copyOfRange(game.strategies, (cumInd-outPartShape[j].length), cumInd)) {
                        if (cur_counts.containsKey(k)) {
                            cur_counts.put(k, cur_counts.get(k) + 1);
                        } else {
                            cur_counts.put(k, 1);
                        }
                    }
                    k_counts_final.add(cur_counts);
                }
                
                for (int j = 0; j < outPartShape.length; j++){
                    // average reward up until now
                    double averageReward = Arrays.stream(rewardAverages[j]).sum() / (double) (i+1);
                    double finReward = rewardAverages[j][i];
                    if (!quiet) {
                        System.out.println("Average reward: "  + j + ": " + averageReward);
                    }
                    Files.writeString(outPartPaths.get(j).get("rewardAv"), averageReward + ";", StandardOpenOption.CREATE, StandardOpenOption.APPEND);
                    
                    if (!quiet) {
                        System.out.println("Final reward: "  + j + ": " + finReward);
                    }
                    Files.writeString(outPartPaths.get(j).get("rewardFin"), finReward + ";", StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        
                    double rewardVariance = varianceRewards[j][i];
                    if (!quiet) {
                        System.out.println("Reward variance: "  + j + ": " + rewardVariance);
                    }
                    Files.writeString(outPartPaths.get(j).get("rewardVar"), rewardVariance + ";", StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        
                    Files.writeString(outPartPaths.get(j).get("coopRate"), (float) game.coop_count[j] / game.act_count[j] + ";", StandardOpenOption.CREATE, StandardOpenOption.APPEND);
                    if (!quiet) {
                        System.out.println("Cooperation rate " + j + ": " + (float) game.coop_count[j] / game.act_count[j]);
                    }
        
                    //av_k frequencys
                    TreeMap<Integer,Double> cur_av_frequency = new TreeMap<Integer,Double>();
                    for (int k = -5; k < 7; k++) {
                        if (k_counts.get(j).containsKey(k)) {
                            cur_av_frequency.put(k, ((double) k_counts.get(j).get(k)) / (outPartShape[j].length * (i+1)));
                        } else {
                            cur_av_frequency.put(k, 0.0);
                        }
                    }
                    av_k_frequency.add(cur_av_frequency);
                    if (!quiet) {
                        System.out.println("Average k frequencies " + j + ": " + av_k_frequency.get(j));
                    }
                    Files.writeString(outPartPaths.get(j).get("kAvFreq"), av_k_frequency.get(j).values().toString().replace("[", "").replace("]", "") + ";", StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        
                    //fin_k frequency
                    TreeMap<Integer,Double> cur_fin_frequency = new TreeMap<Integer,Double>();
                    for (int k = -5; k < 7; k++) {
                        if (k_counts_final.get(j).containsKey(k)) {
                            cur_fin_frequency.put(k, ((double) k_counts_final.get(j).get(k)) / outPartShape[j].length);
                        } else {
                            cur_fin_frequency.put(k, 0.0);
                        }
                    }
                    fin_k_frequency.add(cur_fin_frequency);
                    if (!quiet) {
                        System.out.println("Final k frequencies " + j + ": " + fin_k_frequency.get(j));
                    }
                    Files.writeString(outPartPaths.get(j).get("kFinFreq"), fin_k_frequency.get(j).values().toString().replace("[", "").replace("]", "") + ";", StandardOpenOption.CREATE, StandardOpenOption.APPEND);
                }
                k_counts_final.clear();
                fin_k_frequency.clear();
                av_k_frequency.clear();
            }
        }
        
        cumInd = 0;
        for (int i = 0; i < outPartShape.length; i++){
            cumInd += outPartShape[i].length;
            TreeMap<Integer,Integer> cur_counts = new TreeMap<Integer,Integer>();
            for (int k : Arrays.copyOfRange(game.strategies, (cumInd-outPartShape[i].length), cumInd)) {
                if (cur_counts.containsKey(k)) {
                    cur_counts.put(k, cur_counts.get(k) + 1);
                } else {
                    cur_counts.put(k, 1);
                }
            }
            k_counts_final.add(cur_counts);
        }

        if (intervals == 0){
            for (int i = 0; i < outPartShape.length; i++){
                double averageReward = Arrays.stream(rewardAverages[i]).sum() / (double) generations;
                double finReward = rewardAverages[i][generations-1];
                if (!quiet) {
                    System.out.println("Average reward: "  + i + ": " + averageReward);
                }
                Files.writeString(outPartPaths.get(i).get("rewardAv"), averageReward + System.lineSeparator(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
                
                if (!quiet) {
                    System.out.println("Final reward: "  + i + ": " + finReward);
                }
                Files.writeString(outPartPaths.get(i).get("rewardFin"), finReward + System.lineSeparator(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    
                double rewardVariance = Arrays.stream(varianceRewards[i]).sum() / (double) generations;
                if (!quiet) {
                    System.out.println("Reward variance: "  + i + ": " + rewardVariance);
                }
                Files.writeString(outPartPaths.get(i).get("rewardVar"), rewardVariance + System.lineSeparator(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    
                Files.writeString(outPartPaths.get(i).get("coopRate"), (float) game.coop_count[i] / game.act_count[i] + System.lineSeparator(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
                if (!quiet) {
                    System.out.println("Cooperation rate " + i + ": " + (float) game.coop_count[i] / game.act_count[i]);
                }
    
                //av_k frequencys
                TreeMap<Integer,Double> cur_av_frequency = new TreeMap<Integer,Double>();
                for (int k = -5; k < 7; k++) {
                    if (k_counts.get(i).containsKey(k)) {
                        cur_av_frequency.put(k, ((double) k_counts.get(i).get(k)) / (outPartShape[i].length * generations));
                    } else {
                        cur_av_frequency.put(k, 0.0);
                    }
                }
                av_k_frequency.add(cur_av_frequency);
                if (!quiet) {
                    System.out.println("Average k frequencies " + i + ": " + av_k_frequency.get(i));
                }
                Files.writeString(outPartPaths.get(i).get("kAvFreq"), av_k_frequency.get(i).values().toString().replace("[", "").replace("]", "") + System.lineSeparator(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    
                //fin_k frequency
                TreeMap<Integer,Double> cur_fin_frequency = new TreeMap<Integer,Double>();
                for (int k = -5; k < 7; k++) {
                    if (k_counts_final.get(i).containsKey(k)) {
                        cur_fin_frequency.put(k, ((double) k_counts_final.get(i).get(k)) / outPartShape[i].length);
                    } else {
                        cur_fin_frequency.put(k, 0.0);
                    }
                }
                fin_k_frequency.add(cur_fin_frequency);
                if (!quiet) {
                    System.out.println("Final k frequencies " + i + ": " + fin_k_frequency.get(i));
                }
                Files.writeString(outPartPaths.get(i).get("kFinFreq"), fin_k_frequency.get(i).values().toString().replace("[", "").replace("]", "") + System.lineSeparator(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            }
        }else{
            for (int i = 0; i < outPartShape.length; i++){
                Files.writeString(outPartPaths.get(i).get("rewardFin"), System.lineSeparator(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
                Files.writeString(outPartPaths.get(i).get("rewardAv"), System.lineSeparator(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
                Files.writeString(outPartPaths.get(i).get("rewardVar"), System.lineSeparator(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
                Files.writeString(outPartPaths.get(i).get("coopRate"), System.lineSeparator(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
                Files.writeString(outPartPaths.get(i).get("kAvFreq"), System.lineSeparator(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
                Files.writeString(outPartPaths.get(i).get("kFinFreq"), System.lineSeparator(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            }
        }

        if (game instanceof ForgivenessDonationGame) {
            Map<Double,Double> av_f_frequency = new TreeMap<Double,Double>();
            for (double fs : ForgivenessDonationGame.forgivenessStrategySpace) {
                if (f_counts.containsKey(fs)) {
                    av_f_frequency.put(fs, ((double) f_counts.get(fs)) / (n * generations));
                } else {
                    av_f_frequency.put(fs, 0.0);
                }
            }
            if (!quiet) {
                System.out.println("Average f frequencies:" + av_f_frequency);
            }
            Files.writeString(fAvFreqPath, av_f_frequency.values().toString().replace("[", "").replace("]", "") + System.lineSeparator(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } 

        // Your existing code goes here

        long endTime = System.currentTimeMillis();
        long executionTime = endTime - startTime;

        System.out.println("Execution time: " + executionTime + " milliseconds");
    }
}
