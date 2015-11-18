package ru.killer666.Apteka;

import javafx.scene.layout.Pane;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import ru.killer666.trpo.aaa.domains.Resource;

public abstract class ResourceWorkspaceInterface {

    @Getter(AccessLevel.PUBLIC)
    @Setter(AccessLevel.PUBLIC)
    private Resource resource;

    abstract Pane getPane();
}
