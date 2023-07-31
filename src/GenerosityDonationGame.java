// Implementation of generosity in the donation game as described by 
// L. Schmid, P. Shati, C. Hilbe, and K. Chatterjee, ‘The evolution of indirect reciprocity under
// action and assessment generosity’, Scientific Reports, 11(17443), (2021)

public class GenerosityDonationGame extends NoisyDonationGame {
    boolean generosity;
    double g1;
    double g2;

    public GenerosityDonationGame(int n, int m, double q, double mr, boolean preventNegativePayoffs, 
        double ea, double ep, double g1, double g2) {
            super(n, m, q, mr, preventNegativePayoffs, ea, ep);
            this.g1 = g1;
            this.g2 = g2;
        }       
        
    private void cooperateImageUpdate(int donor, int recipient) {
        if (q < 1.0) {
            for (int i = 0; i < n; i++) {
                // the recipient always observes interaction with no misperception
                if (i == recipient) {
                    super.incrementImage(i, donor);
                } else {
                    // check whether interaction observed
                    if (i != donor && rand.nextDouble() < q) {
                        // if misperceived as defection
                        if (ep > 0.0 && DonationGame.rand.nextDouble() < ep) {
                            // assessment generosity check
                            if (DonationGame.rand.nextDouble() < g1) {
                                super.incrementImage(i, donor);
                            } else {
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
                // the recipient always observes interaction with no misperception
                if (i == recipient) {
                    super.incrementImage(i, donor);
                } else {
                    if (i != donor) {
                        // if misperceived as defection
                        if (ep > 0.0 && DonationGame.rand.nextDouble() < ep) {
                            // assessment generosity check
                            if (DonationGame.rand.nextDouble() < g1) {
                                super.incrementImage(i, donor);
                            } else {
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
    }

    private void defectImageUpdate(int donor, int recipient) {
        if (q < 1.0) {
            for (int i = 0; i < n; i++) {
                // the recipient always observes interaction with no misperception
                if (i == recipient) {
                    super.decrementImage(i, donor);
                } else {
                    // check whether interaction observed
                    if (i != donor && rand.nextDouble() < q) {
                        // if misperceived as cooperation
                        if (ep > 0.0 && DonationGame.rand.nextDouble() < ep) {
                            super.incrementImage(i, donor);
                        } else {
                            // assessment generosity check
                            if (DonationGame.rand.nextDouble() < g1) {
                                super.incrementImage(i, donor);
                            } else {
                                super.decrementImage(i, donor);
                            }
                            super.decrementImage(i, donor);
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
                } else {
                    if (i != donor) {
                        // if misperceived as cooperation
                        if (ep > 0.0 && DonationGame.rand.nextDouble() < ep) {
                            super.incrementImage(i, donor);
                        } else {
                            // assessment generosity check
                            if (DonationGame.rand.nextDouble() < g1) {
                                super.incrementImage(i, donor);
                            } else {
                                super.decrementImage(i, donor);
                            }
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
            if (imageScore >= strategies[donor]) {
                intendToDonate = true;
            } else {
                intendToDonate = false;
            }
            // impose action noise
            if (intendToDonate && ea > 0.0 && DonationGame.rand.nextDouble() < ea) {
                intendToDonate = false;
            }
            // generosity check
            if (!intendToDonate && DonationGame.rand.nextDouble() < g2) {
                intendToDonate = true;
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
