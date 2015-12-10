package ru.killer666.Apteka.domains;

import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.util.Date;

@Data
@ToString
@Entity
@Table(name = "drug_orders")
public class Order {
    @Id
    @Column(name = "id")
    @GeneratedValue
    private int databaseId;

    @ManyToOne
    @JoinColumn(name = "drug_id", referencedColumnName = "id", nullable = false)
    private Drug drug;

    @ManyToOne
    @JoinColumn(name = "recipe_id", referencedColumnName = "id")
    private Recipe recipe;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Column(name = "total_price", nullable = false)
    private int totalPrice;

    @Column(name = "date_sell", nullable = false)
    private Date dateSell;
}
