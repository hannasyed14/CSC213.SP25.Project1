package edu.canisius.csc213.project1;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;

public class UniqueHands {
    private static final int NUM_THREADS = Runtime.getRuntime().availableProcessors(); 

    public static void main(String[] args) {
        int[] deckSizes = {24, 28}; // Deck sizes to test
        int[] handSizes = {6, 7}; // Hand sizes to test
        int trials = 5; // Number of trials per deck-hand combination

        // CSV file try catch using BufferWriter and FileWriter
        String fileName = "unique_hands_results.csv";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            // CSV header
            writer.write("Deck Size,Hand Size,Trial,Attempts,Time (sec)\n");

            System.out.println("🃏 Deck Simulation: How long to see every possible hand?");
            System.out.println("------------------------------------------------------");

            ExecutorService executor = Executors.newFixedThreadPool(NUM_THREADS); // Multi-threaded execution

            for (int deckSize : deckSizes) {
                for (int handSize : handSizes) {
                    for (int trial = 1; trial <= trials; trial++) {
                        long startTime = System.nanoTime();
                        int attempts = countAttemptsToSeeAllHands(deckSize, handSize, startTime);

                        long endTime = System.nanoTime();
                        long trialTime = endTime - startTime;
                        double trialTimeSec = trialTime / 1_000_000_000.0; // Convert to seconds

                        // Write the results to the CSV file
                        writer.write(String.format("%d,%d,%d,%d,%.3f%n",
                                deckSize, handSize, trial, attempts, trialTimeSec));
                    }
                }
            }

            executor.shutdown(); 
            System.out.println("Results written to " + fileName);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Computes the total number of unique hands possible using combinations formula (nCr).
     * Uses an optimized iterative approach to avoid overflow.
     * @param deckSize The number of cards in the deck.
     * @param handSize The number of cards in a hand.
     * @return The total number of unique hands.
     */
    public static int calculateTotalUniqueHands(int deckSize, int handSize) {
        if (handSize > deckSize) return -1; // Ensure valid input
        return (int) combination(deckSize, handSize);
    }

    /**
     * Simulates drawing random hands from a deck until every possible hand has been seen.
     * Uses a HashSet of **integer hashes** instead of full sets for speed.
     * @param deckSize The number of cards in the deck.
     * @param handSize The number of cards in a hand.
     * @return The exact number of attempts required to see all unique hands.
     */
    public static int countAttemptsToSeeAllHands(int deckSize, int handSize, long startTime) {
        Set<Integer> seenHashes = new HashSet<>();
        int totalUniqueHands = calculateTotalUniqueHands(deckSize, handSize);
        if (totalUniqueHands == -1) return -1;

        int attempts = 0;
        Random random = new Random();
        final int progressInterval = 100000; // progress is executed every 100000 attempts

        while (seenHashes.size() < totalUniqueHands) {
            int handHash = generateHandHash(deckSize, handSize, random);
            seenHashes.add(handHash);
            attempts++;

            // Report progress every 100,000 attempts
            if (attempts % progressInterval == 0) {
                int uniqueSeen = seenHashes.size();
                int remaining = totalUniqueHands - uniqueSeen;
                double progress = (uniqueSeen / (double) totalUniqueHands) * 100;
                System.out.printf("Progress: %.2f%% coverage after %,d attempts (Unique Hands: %,d / %,d | Needed: %,d)%n",
                        progress, attempts, uniqueSeen, totalUniqueHands, remaining);
            }
        }

        long endTime = System.nanoTime();
        long trialTime = endTime - startTime;
        double trialTimeSec = trialTime / 1_000_000_000.0; // Convert to seconds
        System.out.printf("100.00%% coverage reached after %,d attempts (Unique Hands: %,d / %,d | Needed: 0)%n",
                attempts, totalUniqueHands, totalUniqueHands);
        System.out.printf("Deck Size: %d | Hand Size: %d | Trial %d | Attempts: %,d | Time: %.3f sec%n",
                deckSize, handSize, 1, attempts, trialTimeSec);

        return attempts;
    }

    /**
     * Generates a unique random hand and converts it to an integer hash.
     * This significantly speeds up lookup compared to storing full sets.
     * @param deckSize Total cards in the deck.
     * @param handSize Cards in the hand.
     * @return An integer hash representing the randomly drawn hand.
     */
    private static int generateHandHash(int deckSize, int handSize, Random random) {
        int[] hand = new int[handSize];
        Set<Integer> uniqueCards = new HashSet<>();

        while (uniqueCards.size() < handSize) {
            uniqueCards.add(random.nextInt(deckSize));
        }

        int index = 0;
        for (int card : uniqueCards) {
            hand[index++] = card;
        }

        Arrays.sort(hand); 

        return Arrays.hashCode(hand); // Get a unique integer hash
    }

    /**
     * Computes nCr (combinations) using an efficient iterative method.
     * This prevents overflow by computing values step-by-step.
     * @param n Total elements.
     * @param r Chosen elements.
     * @return nCr as a double (cast to int when used).
     */
    private static double combination(int n, int r) {
        if (r > n - r) r = n - r; // C(n, r) == C(n, n-r)
        double result = 1;
        for (int i = 0; i < r; i++) {
            result *= (n - i);
            result /= (i + 1);
        }
        return result;
    }
}
