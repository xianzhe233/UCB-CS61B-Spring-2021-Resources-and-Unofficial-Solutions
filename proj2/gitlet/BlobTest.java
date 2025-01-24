package gitlet;
import org.junit.Test;
import java.io.File;
import static gitlet.Utils.*;

import static org.junit.Assert.*;

public class BlobTest {
    @Test
    public void hashTest() {
        File g = new File("testing/src/notwug.txt");
        File f = new File("testing/src/wug.txt");
        File ff = new File("testing/src/sameAsWug.txt");
        String gHash = Blob.blobHash(g);
        String fHash = Blob.blobHash(f);
        String ffHash = Blob.blobHash(ff);
        System.out.println(gHash);
        System.out.println(fHash);
        System.out.println(ffHash);
        assertEquals(ffHash, fHash);
    }

    @Test
    public void blobCreateTest() {
        File wug = new File("testing/src/wug.txt");
        File anotherWug = new File("testing/src/sameAsWug.txt");
        String wugBlobId = Blob.createBlob(wug);
        String anotherWugBlobId = Blob.createBlob(anotherWug);
        File wugBlob = Blob.get(wugBlobId);
        File anotherWugBlob = Blob.get(anotherWugBlobId);
        System.out.println(readContentsAsString(wugBlob));
        System.out.println(readContentsAsString(anotherWugBlob));
        assertEquals(wugBlobId, anotherWugBlobId);
    }
}
