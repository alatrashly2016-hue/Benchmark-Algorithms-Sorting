package javaapplication45;
import java.util.Arrays;
import java.util.Random;

public class JavaApplication45 {

    static final int N = 100_000;
    static final String[] ALGOS = {"Selection Sort", "Insertion Sort", "Bubble Sort", "Merge Sort", "Quick Sort"};
    static final String[] CASES = {"Random", "Reversely Sorted", "Partially Sorted (80%)", "Fully Sorted"};

    // ---------------- Data generation ----------------
    static int[] generate(int type, Random rnd) {
        int[] a = new int[N];
        for (int i = 0; i < N; i++) a[i] = rnd.nextInt(1_000_000);
        switch (type) {
            case 1: // reversely sorted
                Arrays.sort(a);
                for (int i = 0; i < N / 2; i++) {
                    int t = a[i]; a[i] = a[N - 1 - i]; a[N - 1 - i] = t;
                }
                break;
            case 2: // 80% sorted: sort, then shuffle ~20% of positions
                Arrays.sort(a);
                for (int k = 0; k < N / 5; k++) {
                    int i = rnd.nextInt(N), j = rnd.nextInt(N);
                    int t = a[i]; a[i] = a[j]; a[j] = t;
                }
                break;
            case 3: // fully sorted
                Arrays.sort(a);
                break;
            default: // random
                break;
        }
        return a;
    }

    // ---------------- Algorithms ----------------
    static void selectionSort(int[] a) {
        for (int i = 0; i < a.length - 1; i++) {
            int min = i;
            for (int j = i + 1; j < a.length; j++)
                if (a[j] < a[min]) min = j;
            int t = a[i]; a[i] = a[min]; a[min] = t;
        }
    }

    static void insertionSort(int[] a) {
        for (int i = 1; i < a.length; i++) {
            int key = a[i], j = i - 1;
            while (j >= 0 && a[j] > key) {
                a[j + 1] = a[j];
                j--;
            }
            a[j + 1] = key;
        }
    }

    static void bubbleSort(int[] a) {
        for (int i = 0; i < a.length - 1; i++) {
            boolean swapped = false;
            for (int j = 0; j < a.length - 1 - i; j++) {
                if (a[j] > a[j + 1]) {
                    int t = a[j]; a[j] = a[j + 1]; a[j + 1] = t;
                    swapped = true;
                }
            }
            if (!swapped) break; // already sorted
        }
    }

    static void mergeSort(int[] a, int left, int right) {
        if (left >= right) return;
        int mid = (left + right) >>> 1;
        mergeSort(a, left, mid);
        mergeSort(a, mid + 1, right);
        merge(a, left, mid, right);
    }

    static void merge(int[] a, int left, int mid, int right) {
        int[] tmp = new int[right - left + 1];
        int i = left, j = mid + 1, k = 0;
        while (i <= mid && j <= right) tmp[k++] = (a[i] <= a[j]) ? a[i++] : a[j++];
        while (i <= mid) tmp[k++] = a[i++];
        while (j <= right) tmp[k++] = a[j++];
        System.arraycopy(tmp, 0, a, left, tmp.length);
    }

    // Quick Sort with middle pivot (avoids StackOverflow on sorted/reversed input)
    static void quickSort(int[] a, int low, int high) {
        if (low >= high) return;
        int p = partition(a, low, high);
        quickSort(a, low, p - 1);
        quickSort(a, p + 1, high);
    }

    static int partition(int[] a, int low, int high) {
        int mid = (low + high) >>> 1;
        int t = a[mid]; a[mid] = a[high]; a[high] = t; // move pivot to end
        int pivot = a[high], i = low - 1;
        for (int j = low; j < high; j++) {
            if (a[j] < pivot) {
                i++;
                t = a[i]; a[i] = a[j]; a[j] = t;
            }
        }
        t = a[i + 1]; a[i + 1] = a[high]; a[high] = t;
        return i + 1;
    }

    // ---------------- Runner ----------------
    static void runAlgo(int algo, int[] a) {
        switch (algo) {
            case 0: selectionSort(a); break;
            case 1: insertionSort(a); break;
            case 2: bubbleSort(a); break;
            case 3: mergeSort(a, 0, a.length - 1); break;
            default: quickSort(a, 0, a.length - 1);
        }
    }

    // returns the names of the algorithms with the smallest value (ties are all listed)
    static String bestOf(long[] values) {
        long min = Long.MAX_VALUE;
        for (long v : values) if (v < min) min = v;
        StringBuilder sb = new StringBuilder();
        for (int a = 0; a < values.length; a++) {
            if (values[a] == min) {
                if (sb.length() > 0) sb.append(" / ");
                sb.append(ALGOS[a]);
            }
        }
        return sb.toString();
    }
    public static void main(String[] args) {
          Random rnd = new Random(42);
        int[][] originals = new int[4][];
        for (int c = 0; c < 4; c++) originals[c] = generate(c, rnd);
 
        long[][] nanos = new long[ALGOS.length][CASES.length]; // time in nanoseconds
 
        for (int a = 0; a < ALGOS.length; a++) {
            for (int c = 0; c < CASES.length; c++) {
                int[] testArray = Arrays.copyOf(originals[c], originals[c].length); // independent copy
                long start = System.nanoTime();
                runAlgo(a, testArray);
                long end = System.nanoTime();
                nanos[a][c] = end - start;
 
                // sanity check
                for (int i = 1; i < testArray.length; i++)
                    if (testArray[i - 1] > testArray[i]) throw new IllegalStateException(ALGOS[a] + " failed!");
            }
        }
 
        System.out.println("Array size N = " + N);
        String[][] best = new String[CASES.length][3]; // [case][0=ns, 1=us, 2=s]
 
        for (int c = 0; c < CASES.length; c++) {
            System.out.println();
            System.out.println("=== Case: " + CASES[c] + " ===");
            System.out.printf("%-16s | %-18s | %-18s | %-16s%n", "Algorithm", "Nanoseconds (ns)", "Microseconds (us)", "Seconds (s)");
            System.out.println("-".repeat(78));
 
            long[] ns = new long[ALGOS.length];
            long[] us = new long[ALGOS.length];
            // seconds are compared at nanosecond precision (9 decimals), so values are exact
            for (int a = 0; a < ALGOS.length; a++) {
                ns[a] = nanos[a][c];
                us[a] = ns[a] / 1000;
                System.out.printf("%-16s | %-18d | %-18d | %-16.9f%n", ALGOS[a], ns[a], us[a], ns[a] / 1_000_000_000.0);
            }
 
            best[c][0] = bestOf(ns);
            best[c][1] = bestOf(us);
            best[c][2] = bestOf(ns); // seconds with 9 decimals = same resolution as nanoseconds
 
            System.out.println("-".repeat(78));
            System.out.println("Best in nanoseconds  : " + best[c][0]);
            System.out.println("Best in microseconds : " + best[c][1]);
            System.out.println("Best in seconds      : " + best[c][2]);
        }
 
        System.out.println();
        System.out.println("=== Final Summary: best algorithm and its time for every case ===");
        System.out.printf("%-24s | %-16s | %-18s | %-18s | %-14s%n",
                "Case", "Best Algorithm", "Nanoseconds (ns)", "Microseconds (us)", "Seconds (s)");
        System.out.println("-".repeat(103));
        for (int c = 0; c < CASES.length; c++) {
            int b = 0;
            for (int a = 1; a < ALGOS.length; a++)
                if (nanos[a][c] < nanos[b][c]) b = a;
            long t = nanos[b][c];
            System.out.printf("%-24s | %-16s | %-18d | %-18d | %-14.9f%n",
                    CASES[c], ALGOS[b], t, t / 1000, t / 1_000_000_000.0);
        }
    }
}
 
