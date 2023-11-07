import java.util.Arrays;
import java.util.stream.DoubleStream;

// Implementation of action and assessment forgiveness as presented by
// N. Griffiths and N. Oren, 'Generosity and the Emergence of Forgiveness in the Donation Game', in
// Proceedings of 26th European Conference on Artificial Intelligence, (2023)

public class ForgivenessDonationGame extends NoisyDonationGame {
    boolean fa;
    boolean fr;
    double[] forgivenessStrategies;
    // see ECAI paper for explanation of values used for forgiveness strategy space
    static double[] forgivenessStrategySpace = {0.001, 0.5, 1.0, 1.355, 1.67};

    public ForgivenessDonationGame(int n, int m, double q, double mr, boolean preventNegativePayoffs, 
        double er, double ea, boolean fa, boolean fr, int network) {
            super(n, m, q, mr, preventNegativePayoffs, er, ea, network);
            this.fa = fa;
            this.fr = fr;
            this.forgivenessStrategies = new double[n];
            for (int i = 0; i < forgivenessStrategies.length; i++) {
                forgivenessStrategies[i] = forgivenessStrategySpace[rand.nextInt(forgivenessStrategySpace.length)];
            }
        }       
        
    private void cooperateImageUpdate(int donor, int recipient, boolean actionForgiveness) {
        if (q < 1.0) {
            for (int i = 0; i < n; i++) {
                // the recipient and donor always observe interaction with no misperception
                // donor maintains image score of self for use in forgiveness
                if (i == recipient || i == donor) {
                    super.incrementImage(i, donor);
                } else {
                    // check whether interaction observed
                    if (rand.nextDouble() < q) {
                        // if misperceived as defection
                        if (ep > 0.0 && DonationGame.rand.nextDouble() < ep) {
                            // check whether potential for reputation (assessment) forgiveness 
                            if (fr) {
                                double imageScore = super.getImageScore(i, donor);
                                // if not forgiving reduce image score
                                if (DonationGame.rand.nextDouble() >= 
                                    Math.exp(-((-imageScore + 5) / forgivenessStrategies[i]))) {
                                        super.decrementImage(i, donor);
                                }
                                // otherwise forgive and do nothing
                            } else {
                                // else not forgiving so reduce image score
                                super.decrementImage(i, donor);
                            }
                        } else {
                            // otherwise perceived correctly as cooperation
                            super.incrementImage(i, donor);
                        }
                    }
                }
            }
        }
        else {
            for (int i = 0; i < n; i++) {
                // the recipient and donor always observe interaction with no misperception
                // donor maintains image score of self for use in forgiveness
                if (i == recipient || i == donor) {
                    super.incrementImage(i, donor);
                } else {
                    // if misperceived as defection
                    if (ep > 0.0 && DonationGame.rand.nextDouble() < ep) {
                        // check whether potential for reputation (assessment) forgiveness 
                        if (fr) {
                            double imageScore = super.getImageScore(i, donor);
                            // if not forgiving reduce image score
                            if (DonationGame.rand.nextDouble() >= 
                                Math.exp(-((-imageScore + 5) / forgivenessStrategies[i]))) {
                                    super.decrementImage(i, donor);
                            }
                            // otherwise forgive and do nothing
                        } else {
                            // else not forgiving so reduce image score
                            super.decrementImage(i, donor);
                        }
                    } else {
                        // otherwise perceived correctly as cooperation
                        super.incrementImage(i, donor);
                    }
                }
                
            }
        }
    }

    private void defectImageUpdate(int donor, int recipient) {
        if (q < 1.0) {
            for (int i = 0; i < n; i++) {
                // the recipient always observes interaction with no misperception
                if (i == recipient) {
                    super.decrementImage(i, donor);
                } else if (i == donor) {
                    // maintain image score of self for use in ascribed forgiveness
                    super.decrementImage(i, donor);
                } else {
                    // check whether interaction observed
                    if (rand.nextDouble() < q) {
                        // if misperceived as cooperation
                        if (ep > 0.0 && DonationGame.rand.nextDouble() < ep) {
                            super.incrementImage(i, donor);
                        } else {
                            // check whether potential for reputation (assessment) forgiveness 
                            if (fr) {
                                double imageScore = super.getImageScore(i, donor);
                                // if not forgiving reduce image score
                                if (DonationGame.rand.nextDouble() >= 
                                    Math.exp(-((-imageScore + 5) / forgivenessStrategies[i]))) {
                                        super.decrementImage(i, donor);
                                }
                                // otherwise forgive and do nothing
                            } else {
                                // else not forgiving so reduce image score
                                super.decrementImage(i, donor);
                            }
                        }
                    }
                }
            }
        }
        else {
            for (int i = 0; i < n; i++) {
                // the recipient always observes interaction with no misperception
                if (i == recipient) {
                    super.decrementImage(i, donor);
                } else if (i == donor) {
                    // maintain image score of self for use in ascribed forgiveness
                    super.decrementImage(i, donor);
                } else {
                    // if misperceived as cooperation
                    if (ep > 0.0 && DonationGame.rand.nextDouble() < ep) {
                        super.incrementImage(i, donor);
                    } else {
                        // check whether potential for reputation (assessment) forgiveness 
                        if (fr) {
                            double imageScore = super.getImageScore(i, donor);
                            // if not forgiving reduce image score
                            if (DonationGame.rand.nextDouble() >= 
                                Math.exp(-((-imageScore + 5) / forgivenessStrategies[i]))) {
                                    super.decrementImage(i, donor);
                            }
                            // otherwise forgive and do nothing
                        } else {
                            // else not forgiving so reduce image score
                            super.decrementImage(i, donor);
                        }
                    }
                }
            }
        }
    }
            
    public void tick() {
        for (int i = 0; i < m; i++) {
            int donor = DonationGame.rand.nextInt(n);
            int recipient = DonationGame.rand.nextInt(n);
            while (recipient == donor) {
                recipient = DonationGame.rand.nextInt(n);
            }
            double imageScore = super.getImageScore(donor, recipient);
            boolean intendToDonate;
            boolean forgiving = false;
            if (imageScore >= strategies[donor]) {
                intendToDonate = true;
            } else {
                intendToDonate = false;
            }
            // impose action noise
            if (intendToDonate && ea > 0.0 && DonationGame.rand.nextDouble() < ea) {
                intendToDonate = false;
            }
            // potential to donate through action forgiveness
            if (!intendToDonate && (fa) && DonationGame.rand.nextDouble() < 
                Math.exp(-((-imageScore + 5) / forgivenessStrategies[donor]))) {
                    intendToDonate = true;
                    forgiving = true;
                }
            if (intendToDonate) {
                rewards[donor] -= DonationGame.c;
                rewards[recipient] += DonationGame.b;
                cooperateImageUpdate(donor, recipient, forgiving);
            } else {
                defectImageUpdate(donor, recipient);
            }
            if (preventNegativePayoffs) {
                rewards[donor] += 0.1;
                rewards[recipient] += 0.1;
            }
        }
    }

    public void mutation() {
        if (mr == 0.0) return;
        for (int i = 0; i < n; i++) {
            if (rand.nextDouble() < mr) {
                int s = rand.nextInt(-5, 7);
                while (s == strategies[i]) {
                    s = rand.nextInt(-5, 7);
                }
                int index = rand.nextInt(forgivenessStrategySpace.length);
                while (forgivenessStrategySpace[index] == forgivenessStrategies[i]) {
                    index = rand.nextInt(forgivenessStrategySpace.length);
                }
                strategies[i] = s;
                forgivenessStrategies[i] = forgivenessStrategySpace[index];
            }
        }
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
        int[] newDonationStrategies = new int[n];
        double[] newForgivenessStrategies = new double[n];
        int index;
        for (int i = 0; i < n; i++) {
            index = weightedRandomChoice(rewardsScaled, totalReward);
            newDonationStrategies[i] = strategies[index];
            newForgivenessStrategies[i] = forgivenessStrategies[index];
        }
        strategies = newDonationStrategies;
        forgivenessStrategies = newForgivenessStrategies;
        Arrays.fill(rewards, 0.0);
        Arrays.stream(imageScores).forEach(a -> Arrays.fill(a, 0));
    }
}
