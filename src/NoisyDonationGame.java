// Extension to the donation game incorporating action and perception noise
// Perception noise is as described by
// L. Schmid, P. Shati, C. Hilbe, and K. Chatterjee, ‘The evolution of indirect reciprocity under
// action and assessment generosity’, Scientific Reports, 11(17443), (2021)
// Action noise is as introduced by
// N. Griffiths and N. Oren, 'Generosity and the Emergence of Forgiveness in the Donation Game', in
// Proceedings of 26th European Conference on Artificial Intelligence, (2023)

public class NoisyDonationGame extends DonationGame {
    double ea;
    double ep;

    public NoisyDonationGame(int n, int m, double q, double mr, boolean preventNegativePayoffs, 
        double ea, double ep) {        
            super(n, m, q, mr, preventNegativePayoffs);
            this.ea = ea;
            this.ep = ep;
        }

    protected double getImageScore(int maintainer, int target) {
        return imageScores[maintainer][target];
    }
        
    private void cooperateImageUpdate(int donor, int recipient) {
        if (q < 1.0) {
            for (int i = 0; i < n; i++) {
                if (i == recipient) {
                    super.incrementImage(i, donor);
                } else {
                    if (i != donor && rand.nextDouble() < q) {
                        if (ep > 0.0 && DonationGame.rand.nextDouble() < ep) {
                            super.decrementImage(i, donor);
                        } else {
                            super.incrementImage(i, donor);
                        }
                    }
                }
            }
        }
        else {
            for (int i = 0; i < n; i++) {
                if (i == recipient) {
                    super.incrementImage(i, donor);
                } else {
                    if (i != donor) {
                        if (ep > 0.0 && DonationGame.rand.nextDouble() < ep) {
                            super.decrementImage(i, donor);
                        } else {
                            super.incrementImage(i, donor);
                        }
                    }
                }
            }
        }
    }

    private void defectImageUpdate(int donor, int recipient) {
        if (q < 1.0) {
            for (int i = 0; i < n; i++) {
                if (i == recipient) {
                    super.decrementImage(i, donor);
                } else {
                    if (i != donor && rand.nextDouble() < q) {
                        if (ep > 0.0 && DonationGame.rand.nextDouble() < ep) {
                            super.incrementImage(i, donor);
                        } else {
                            super.decrementImage(i, donor);
                        }
                    }
                }
            }
        }
        else {
            for (int i = 0; i < n; i++) {
                if (i == recipient) {
                    super.decrementImage(i, donor);
                } else {
                    if (i != donor) {
                        if (ep > 0.0 && DonationGame.rand.nextDouble() < ep) {
                            super.incrementImage(i, donor);
                        } else {
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

            double imageScore = getImageScore(donor, recipient);

            boolean intendToDonate;
            if (imageScore >= strategies[donor]) {
                intendToDonate = true;
            } else {
                intendToDonate = false;
            }
            // impose action noise
            if (intendToDonate && ea > 0.0 && DonationGame.rand.nextDouble() < ea) {
                intendToDonate = false;
            }
            if (intendToDonate) {
                rewards[donor] -= DonationGame.c;
                rewards[recipient] += DonationGame.b;
                cooperateImageUpdate(donor, recipient);
            } else {
                defectImageUpdate(donor, recipient);
            }
            if (preventNegativePayoffs) {
                rewards[donor] += 0.1;
                rewards[recipient] += 0.1;
            }
        }
    }
    
}
