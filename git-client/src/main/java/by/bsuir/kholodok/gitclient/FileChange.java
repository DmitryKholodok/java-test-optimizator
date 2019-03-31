package by.bsuir.kholodok.gitclient;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class FileChange {

    private String oldFilename;
    private String newFilename;
    private List<Change> changes = new ArrayList<>();
}
