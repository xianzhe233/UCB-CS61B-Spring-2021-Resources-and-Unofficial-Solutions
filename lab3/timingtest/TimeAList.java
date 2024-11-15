package timingtest;
import edu.princeton.cs.algs4.Stopwatch;

/**
 * Created by hug.
 */
public class TimeAList {
    private static void printTimingTable(AList<Integer> Ns, AList<Double> times, AList<Integer> opCounts) {
        System.out.printf("%12s %12s %12s %12s\n", "N", "time (s)", "# ops", "microsec/op");
        System.out.print("------------------------------------------------------------\n");
        for (int i = 0; i < Ns.size(); i += 1) {
            int N = Ns.get(i);
            double time = times.get(i);
            int opCount = opCounts.get(i);
            double timePerOp = time / opCount * 1e6;
            System.out.printf("%12d %12.2f %12d %12.2f\n", N, time, opCount, timePerOp);
        }
    }

    public static void main(String[] args) {
        timeAListConstruction();
    }

    /** Make an N AList including the numbers of N. */
    public static AList<Integer> makeNsAList() {
        int[] opNumbers = new int[]{1000, 2000, 4000, 8000, 16000, 32000, 64000, 128000, 256000};
        AList<Integer> a = new AList<>();
        for (int i = 0; i < opNumbers.length; i++) a.addLast(opNumbers[i]);
        return a;
    }

    /** Test AList.addLast() for length N. Return info of time used and op numbers. */
    public static TestInfo testWithLength(int N) {
        Stopwatch sw = new Stopwatch();
        AList testList = new AList();
        int opCount = 0;
        for (int i = 1; i <= N; i++) {
            testList.addLast(1);
            opCount += 1;
        }
        double timeUsage = sw.elapsedTime();
        TestInfo info = new TestInfo(timeUsage, opCount);
        return info;
    }

    public static void timeAListConstruction() {
        // TODO: YOUR CODE HERE
        AList<Integer> Ns = makeNsAList();
        AList<Double> times = new AList<>();
        AList<Integer> ops = new AList<>();
        for (int i = 0; i < Ns.size(); i++) {
            TestInfo info = testWithLength(Ns.get(i));
            times.addLast(info.timeUsage);
            ops.addLast(info.opCount);
        }

        printTimingTable(Ns, times, ops);
    }
}

/** A class for passing test information. */
class TestInfo {
    double timeUsage;
    int opCount;
    public TestInfo(double timeUsage, int opCount) {
        this.timeUsage = timeUsage;
        this.opCount = opCount;
    }
}