package ru.killer666.Apteka.domains;

import lombok.Getter;
import lombok.ToString;
import ru.killer666.trpo.aaa.domains.RoleInterface;

@ToString
public enum Role implements RoleInterface {
    CEO(1), TRADER(2);

    @Getter
    private final int value;

    Role(int value) {
        this.value = value;
    }
}
