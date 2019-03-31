package by.bsuir.kholodok.gitclient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

@SpringBootApplication
public class GitClientImpl implements ApplicationRunner {

    final static String PATH = "/Users/dmitrykholodok/java/diploma-test-git/.git";
    private final static String PATH_TO_SCANNED_DIRECTORY_ARG_NAME = "path";

    @Autowired
    private StagedChangeLoader loader;

    public static void main(String[] args) {
        SpringApplication.run(GitClientImpl.class, args);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        String load = loader.load(PATH);
        List<FileChange> fileChanges = new ArrayList<>();
        Stream.of(load.split("diff --git"))
                .filter(this::isNotBlank)
                .map(this::splitStrByEnter)
                .forEach(gitRows -> {
                    FileChange fileChange = new FileChange();
                    Iterator<String> iterator = gitRows.iterator();

                    List<String> twoParts = List.of(iterator.next().trim().split(" ")); // a/a.txt b/1/a_renamed_and_changed.txt
                    fileChange.setOldFilename(twoParts.get(0).substring(2));
                    fileChange.setNewFilename(twoParts.get(1).substring(2));

                    while(iterator.hasNext()) {
                        String trimRow = iterator.next().trim();
                        if (trimRow.matches("[@]{2}[^@]+[@]{2}")) { // header
//                            @@ -6,0 +7 @@
//                            @@ -14 +15,2 @@
//                            @@ -8,0 +9 @@
//                            @@ -19,0 +22,5 @@
                            String substring = trimRow.substring(3, trimRow.length() - 3);
                            List<String> split = List.of(substring.split(" "));

                            String[] minus = split.get(0).substring(1).split(",");
                            int count = 1;
                            if (minus.length == 2) {
                                count = Integer.parseInt(minus[1]);
                            }
                            Change change = new Change();
                            for(int i = 0; i < count; i++) {
                                String next = iterator.next();
                                change.getRows().add(next.substring(1));
                            }
                            change.setType(ChangeType.REMOVED);
                            change.setLineNumberChangeStartsFrom(Integer.parseInt(minus[0]));
                            if (!change.getRows().isEmpty()) {fileChange.getChanges().add(change);}

                            String[] plus = split.get(1).substring(1).split(",");
                            count = 1;
                            if (plus.length == 2) {
                                count = Integer.parseInt(plus[1]);
                            }
                            Change change1 = new Change();
                            for(int i = 0; i < count; i++) {
                                change1.getRows().add(iterator.next().substring(1));
                            }
                            change1.setType(ChangeType.ADDED);
                            change1.setLineNumberChangeStartsFrom(Integer.parseInt(plus[0]));
                            if (!change1.getRows().isEmpty()) {fileChange.getChanges().add(change1);}
                        }
                    }
                    fileChanges.add(fileChange);
                });
        System.out.println(fileChanges);
    }

    private boolean isNotBlank(String str) {
        return !str.isBlank();
    }

    private List<String> splitStrByEnter(String str) {
        return List.of(str.split("\n"));
    }

}
