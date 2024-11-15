package timingtest;
import edu.princeton.cs.algs4.Stopwatch;

/**
 * Created by hug.
 */
public class TimeSLList {
    static int M = 10000; //Op numbers is fixed.

    private static void printTimingTable(AList<Integer> Ns, AList<Double> times, AList<Integer> opCounts) {
        System.out.printf("%12s %12s %12s %12s\n", "N", "time (s)", "# ops", "microsec/op");
        System.out.printf("------------------------------------------------------------\n");
        for (int i = 0; i < Ns.size(); i += 1) {
            int N = Ns.get(i);
            double time = times.get(i);
            int opCount = opCounts.get(i);
            double timePerOp = time / opCount * 1e6;
            System.out.printf("%12d %12.2f %12d %12.2f\n", N, time, opCount, timePerOp);
        }
    }

    public static void main(String[] args) {
        timeGetLast();
    }

    public static SLList<Integer> makeSLListWithLength(int N){
        SLList<Integer> testList = new SLList<>();
        for (int i = 1; i <= N; i++) testList.addLast(i);
        return testList;
    }

    /** Using prepared testList, addLast for M times, return test info. */
    public static TestInfo addTest(SLList<Integer> testList, int M){
        Stopwatch sw = new Stopwatch();
        for (int i = 1; i <= M; i++) testList.addLast(i);
        double timeUsage = sw.elapsedTime();
        int opCount = M;
        return new TestInfo(timeUsage, opCount);
    }

    /** Test SLList.addLast() with length N for M times. */
    public static TestInfo TestWithLength(int N, int M){
        SLList<Integer> testList = makeSLListWithLength(N);
        TestInfo info = addTest(testList, M);
        return info;
    }

    public static void timeGetLast() {
        // TODO: YOUR CODE HERE
        AList<Integer> Ns = TimeAList.makeNsAList();
        AList<Double> times = new AList<>();
        AList<Integer> opCounts = new AList<>();

        for (int i = 0; i < Ns.size(); i += 1) {
            TestInfo info = TestWithLength(Ns.get(i), M);
            times.addLast(info.timeUsage);
            opCounts.addLast(info.opCount);
//            System.out.println(Ns.get(i) + "finished.");
            //I was hurrying waiting the result.
        }

        printTimingTable(Ns, times, opCounts);
    }

}

/*
It took so long so I just save it here.
           N     time (s)        # ops  microsec/op
------------------------------------------------------------
        1000         0.10        10000        10.40
        2000         0.12        10000        12.10
        4000         0.17        10000        16.90
        8000         0.25        10000        25.10
       16000         0.36        10000        35.80
       32000         0.49        10000        49.30
       64000         1.24        10000       123.70
      128000         2.07        10000       207.20
      256000         5.03        10000       502.70
 */