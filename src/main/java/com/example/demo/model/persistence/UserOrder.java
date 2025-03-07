package com.example.demo.model.persistence;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "user_order")
public class UserOrder {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@JsonProperty
	@Column
	private Long id;
	
	@ManyToMany(cascade = CascadeType.ALL)
	@JsonProperty
	@Column
    private List<Item> items;
	
	@ManyToOne
	@JoinColumn(name="user_id", nullable = false, referencedColumnName = "id")
	@JsonProperty
    private User user;
	
	@JsonProperty
	@Column
	private BigDecimal total;

	public static UserOrder createFromCart(Cart cart) {
		UserOrder order = new UserOrder();
		order.setItems(cart.getItems().stream().collect(Collectors.toList()));
		order.setTotal(cart.getTotal());
		order.setUser(cart.getUser());
		return order;
	}
	
}
