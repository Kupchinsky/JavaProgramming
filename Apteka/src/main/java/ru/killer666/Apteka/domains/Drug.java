package ru.killer666.Apteka.domains;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "drugs")
public class Drug {
    @Id
    @Column(name = "id")
    @GeneratedValue
    private int databaseId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "type", nullable = false)
    private String type;

    @Column(name = "apply_type", nullable = false)
    private String applyType;

    @Column(name = "price", nullable = false)
    private int price;

    @Column(name = "storage_quantity", nullable = false)
    private int storageQuantity;

    @Override
    public String toString() {
        return this.name + " (" + this.storageQuantity + " единиц на складе)";
    }
}
