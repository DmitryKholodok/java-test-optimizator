package by.bsuir.kholodok.gitclient;

import lombok.SneakyThrows;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.RenameDetector;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.List;

@Component
public class StagedChangeLoader {

    @SneakyThrows
    public String load(String pathToDir) {
        Repository repository = createRepository(pathToDir);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try (DiffFormatter formatter = new DiffFormatter(os)) {
            formatter.setRepository(repository);
            formatter.setContext(0);
            try (Git git = new Git(repository)) {
                List<DiffEntry> diffEntries = git.diff().setCached(true).call();
                RenameDetector rd = new RenameDetector(repository);
                rd.addAll(diffEntries);
                formatter.format(rd.compute());
            }
        }
        return os.toString();
    }

    @SneakyThrows
    private Repository createRepository(String pathToDir) {
        return new FileRepositoryBuilder()
                .setGitDir(new File(pathToDir))
                .build();
    }

}
