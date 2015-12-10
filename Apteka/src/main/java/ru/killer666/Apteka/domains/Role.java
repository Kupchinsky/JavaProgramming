package ru.killer666.Apteka.domains;

import lombok.Getter;
import lombok.ToString;
import ru.killer666.trpo.aaa.RoleInterface;

@ToString
public enum Role implements RoleInterface {
    ADMIN("Администратор"), TRADER("Продавец");

    @Getter
    private final String value;

    Role(String value) {
        this.value = value;
    }
}
