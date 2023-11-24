import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
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
        options.addOption(new Option("net", "network", true, "0 = Fully Connected, 1 = Bipartite"));
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
        int network;
        if (cmd.hasOption("h")) {
            formatter.printHelp("Simulation", options);
            System.out.println();
            System.exit(0);
        }
        if(cmd.hasOption("net")) {
            network = Integer.parseInt(cmd.getOptionValue("net"));
        } else {
            network = 0;
        }
        if(cmd.hasOption("n")) {
            n = Integer.parseInt(cmd.getOptionValue("n"));
        } else {
            n = 100;
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
            + " network=" + network);
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
        sb.append("_net" + network );
        String fileName = sb.toString();
        Path coopRatePath = Paths.get(".", dataDir, fileName + "_coop-rate.csv");
        Path rewardVarPath = Paths.get(".", dataDir, fileName + "_reward-variances.csv");
        Path rewardAvPath = Paths.get(".", dataDir, fileName + "_reward-averages.csv");
        Path kAvFreqPath = Paths.get(".", dataDir, fileName + "_kAvFreq.csv");
        Path kFinFreqPath = Paths.get(".", dataDir, fileName + "_kFinFreq.csv");
        Path fAvFreqPath = Paths.get(".", dataDir, fileName + "_fAvFreq.csv");
        if (!quiet) {
            System.out.println("Output file for cooperation rate: " + coopRatePath);
            System.out.println("Output file for reward variance: " + rewardVarPath);
            System.out.println("Output file for reward averages: " + rewardAvPath);
            System.out.println("Output file for average frequency of donation strategies: " + kAvFreqPath);
            System.out.println("Output file for final frequency of donation strategies: " + kFinFreqPath);
            System.out.println("Output file for frequency of forgiveness strategies: " + fAvFreqPath);
        }

        DonationGame game;
        if (!generosity && !fa && !fr && ea == 0.0 && ep == 0.0) {
            System.out.println("Using DonationGame, i.e., without noise, generosity or forgiveness");
            game = new DonationGame(n, m, q, mr, preventNegativePayoffs, network);
        } else {
            if (generosity && (g1 > 0.0 || g2 > 0.0)) {
                if (fa || fr) {
                    System.out.println("Error: Cannot use generosity alongside forgiveness: disable either generosity or forgiveness.");
                    System.exit(1);
                }
                System.out.println("Using GenerosityDonationGame, i.e., with noise and generosity");
                game = new GenerosityDonationGame(n, m, q, mr, preventNegativePayoffs, ea, ep, g1, g2, network);
            } else if (fa || fr) {
                System.out.println("Using ForgivenessDonationGame, i.e., with noise and forgiveness");
                game = new ForgivenessDonationGame(n, m, q, mr, preventNegativePayoffs, ea, ep, fa, fr, network);                
            } else {
                System.out.println("Using NoisyDonationGame, i.e., with noise");
                game = new NoisyDonationGame(n, m, q, mr, preventNegativePayoffs, ea, ep, network);
            }
        }

        double[] rewardAverages = new double[generations];
        double[] varianceRewards = new double[generations];
        
        Map<Integer,Integer> k_counts = new TreeMap<Integer,Integer>();
        Map<Integer,Integer> k_counts_final = new TreeMap<Integer,Integer>();

        // Note, this is only used for forgiveness games
        // This is an inefficient hack to inspect forgiveness strategy frequencies, and
        // if used for more than occasional evaluation should be refactored. 
        Map<Double,Integer> f_counts = new TreeMap<Double,Integer>();

        for (int i = 0; i < generations; i++) {
            game.tick();
            rewardAverages[i] = game.getAverageReward();
            varianceRewards[i] = game.getRewardVariance();

            for (int k : game.strategies) {
                if (k_counts.containsKey(k)) {
                    k_counts.put(k, k_counts.get(k) + 1);
                } else {
                    k_counts.put(k, 1);
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
        }
        
        for (int k : game.strategies) {
            if (k_counts_final.containsKey(k)) {
                k_counts_final.put(k, k_counts_final.get(k) + 1);
            } else {
                k_counts_final.put(k, 1);
            }
        }

        double averageReward = Arrays.stream(rewardAverages).sum() / (double) generations;
        if (!quiet) {
            System.out.println("Average reward: " + averageReward);
        }
        Files.writeString(rewardAvPath, averageReward + System.lineSeparator(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        
        double rewardVariance = Arrays.stream(varianceRewards).sum() / (double) generations;
        if (!quiet) {
            System.out.println("Reward variance: " + rewardVariance);
        }
        Files.writeString(rewardVarPath, rewardVariance + System.lineSeparator(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        
        Files.writeString(coopRatePath, (float) game.coop_count / (m * generations) + System.lineSeparator(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        if (!quiet) {
            System.out.println("Cooperation rate: " + (float) game.coop_count / (m * generations));
        }
        Map<Integer,Double> av_k_frequency = new TreeMap<Integer,Double>();
        for (int k = -5; k < 7; k++) {
            if (k_counts.containsKey(k)) {
                av_k_frequency.put(k, ((double) k_counts.get(k)) / (n * generations));
            } else {
                av_k_frequency.put(k, 0.0);
            }
        }
        if (!quiet) {
            System.out.println("Average k frequencies:" + av_k_frequency);
        }
        Files.writeString(kAvFreqPath, av_k_frequency.values().toString().replace("[", "").replace("]", "") + System.lineSeparator(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        
        Map<Integer,Double> fin_k_frequency = new TreeMap<Integer,Double>();
        for (int k = -5; k < 7; k++) {
            if (k_counts_final.containsKey(k)) {
                fin_k_frequency.put(k, ((double) k_counts_final.get(k)) / n);
            } else {
                fin_k_frequency.put(k, 0.0);
            }
        }
        if (!quiet) {
            System.out.println("Final k frequencies:" + fin_k_frequency);
        }
        Files.writeString(kFinFreqPath, fin_k_frequency.values().toString().replace("[", "").replace("]", "") + System.lineSeparator(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);

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
    }
}
