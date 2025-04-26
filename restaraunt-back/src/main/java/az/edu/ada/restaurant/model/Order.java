package az.edu.ada.restaurant.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "orders")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Order {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer tableNumber;    // ← new field



    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Dish> items = new ArrayList<>();

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @ManyToOne(optional = true)
    @JoinColumn(name="waiter_id", nullable = true)
    private User waiter;



}
