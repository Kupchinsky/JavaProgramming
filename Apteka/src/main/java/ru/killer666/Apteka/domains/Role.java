package ru.killer666.Apteka.domains;

import lombok.Getter;
import ru.killer666.trpo.aaa.RoleInterface;

public enum Role implements RoleInterface {
    ADMIN("Администратор"), TRADER("Продавец");

    @Getter
    private final String value;

    Role(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return this.value;
    }
}
