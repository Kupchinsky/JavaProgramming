package ru.killer666.Apteka;

import javafx.scene.layout.Pane;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Session;
import ru.killer666.Apteka.domains.Role;
import ru.killer666.trpo.aaa.domains.Resource;

public abstract class ResourceWorkspaceInterface {

    @Getter(AccessLevel.PUBLIC)
    @Setter(AccessLevel.PUBLIC)
    private Resource resource;

    @Getter(AccessLevel.PUBLIC)
    @Setter(AccessLevel.PUBLIC)
    private Role role;

    @Getter(AccessLevel.PUBLIC)
    @Setter(AccessLevel.PUBLIC)
    private Session session;

    public abstract Pane getPane();
}
