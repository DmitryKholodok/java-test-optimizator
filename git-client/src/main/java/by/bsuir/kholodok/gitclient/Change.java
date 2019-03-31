package by.bsuir.kholodok.gitclient;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Change {

    private ChangeType type;
    private List<String> rows = new ArrayList<>();
    private int lineNumberChangeStartsFrom;
}
