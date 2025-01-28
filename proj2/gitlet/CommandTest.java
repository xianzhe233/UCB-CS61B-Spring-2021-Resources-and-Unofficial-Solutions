package gitlet;
import static org.junit.Assert.*;
import org.junit.Test;
import java.io.File;
import static gitlet.Main.*;
import static gitlet.Utils.*;
import static gitlet.Repository.*;

public class CommandTest {
    static void input(String... args) {
        main(args);
    }

    static void file(String name, String content) {
        File file = join(CWD, name);
        createFile(file);
        writeContents(file, content);
    }

    @Test
    public void addTest() {
        initRepository();
        File f = join(CWD, "f1.txt");
        createFile(f);
        writeContents(f, "Hello");
        System.out.println(getAddition());
        input("add", "f1.txt");
        System.out.println(getAddition());
        input("commit", "add f1.txt");

        input("add", "f1.txt");
        System.out.println(getAddition());
        writeContents(f, "Hello, world");
        System.out.println(getAddition());
        input("add", "f1.txt");
        System.out.println(getAddition());
        input("commit", "change f1.txt");
        f.delete();
    }

    @Test
    public void removeTest() {
        initRepository();
        File f1 = join(CWD, "f1.txt");
        File f2 = join(CWD, "f2.txt");
        createFile(f1);
        createFile(f2);
        writeContents(f1, "Hello");
        writeContents(f2, "World");
        input("add", "f1.txt");
        input("commit", "add f1.txt");
        System.out.println(getAddition());
        System.out.println(getRemoval());
        input("add", "f1.txt");
        input("add", "f2.txt");
        System.out.println(getAddition());
        System.out.println(getRemoval());
        input("rm", "f1.txt");
        System.out.println(getAddition());
        System.out.println(getRemoval());
        input("rm", "f2.txt");
        System.out.println(getAddition());
        System.out.println(getRemoval());
        input("commit", "remove all");
    }

    @Test
    public void logTest() {
        initRepository();
        File f1 = join(CWD, "f1.txt");
        createFile(f1);
        writeContents(f1, "Hello");
        input("add", "f1.txt");
        input("commit", "add f1.txt");
        writeContents(f1, "Hello, world");
        input("add", "f1.txt");
        input("commit", "change f1.txt to Hello world");
        writeContents(f1, "Hello, world, abc");
        input("add", "f1.txt");
        input("commit", "add abc to f1.txt");
         input("log");
        input("global-log");
    }

    @Test
    public void findTest() {
        initRepository();
        File f1 = join(CWD, "f1.txt");
        createFile(f1);
        writeContents(f1, "Hello");
        input("add", "f1.txt");
        input("commit", "message 1");
        writeContents(f1, "Hello, world");
        input("add", "f1.txt");
        input("commit", "message 2");
        writeContents(f1, "Hello, world!");
        input("add", "f1.txt");
        input("commit", "message 1");
        input("log");
        input("find", "message 1");
    }

    @Test
    public void statusTest() {
        initRepository();
        file("add1", "file for test add");
        file("add2", "file for test add");
//        input("status");
        input("add", "add1");
        input("add", "add2");
//        input("status");
        input("commit", "commit 1");

        input("rm", "add2");
        file("m1", "m1");
        file("m4", "m4");
        file("u1", "u1");
        file("u2", "u2");
//        input("status");
        input("add", "m1");
        input("add", "m4");
        input("add", "u2");
//        input("status");
        input("commit", "commit 2");

        Branch.set("b2", getHead());
        Branch.set("b3", getHead());
//        input("status");

        file("m1", "changed m1");
        file("m2", "m2");
        input("add", "m2");
        file("m2", "changed m2");
        file("m3", "m3");
        input("add", "m3");
        fileOf("m3").delete();
        fileOf("m4").delete();

        input("rm", "u2");
        file("u2", "u2");

        input("status");

    }
}
