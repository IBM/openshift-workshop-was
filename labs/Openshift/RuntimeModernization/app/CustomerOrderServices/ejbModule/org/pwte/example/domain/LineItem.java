package org.pwte.example.domain;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.json.bind.annotation.JsonbTransient;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreRemove;
import javax.persistence.PreUpdate;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import javax.persistence.Transient;


@Entity
@Table(name = "LINE_ITEM")
@IdClass(LineItemId.class)
public class LineItem implements Serializable {

	private static final long serialVersionUID = -447939565773238603L;

	@Id
	@Column(name = "ORDER_ID", updatable=false, insertable=false)
	private int orderId;

	@Id
	@Column(name = "PRODUCT_ID", updatable=false, insertable=false)
	private int productId;
	
	protected long quantity;
	protected BigDecimal amount;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "PRODUCT_ID", referencedColumnName = "PRODUCT_ID")
	protected Product product;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "ORDER_ID", referencedColumnName = "ORDER_ID")
	protected Order order;

	@Transient
	private long version;

	public int getOrderId() {
		return orderId;
	}

	public void setOrderId(int orderId) {
		this.orderId = orderId;
	}

	public int getProductId() {
		return productId;
	}

	public void setProductId(int productId) {
		this.productId = productId;
	}

	public long getQuantity() {
		return quantity;
	}

	public void setQuantity(long quantity) {
		this.quantity = quantity;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	@JsonbTransient
	public Order getOrder() {
		return order;
	}

	@JsonbTransient
	public void setOrder(Order order) {
		this.order = order;
	}

	@PrePersist
	void calculateTotalAdd() {
		System.out.println("Adding new LI -> " + product.getName());
		BigDecimal total = new BigDecimal(0);
		if (getOrder() != null)
			total = getOrder().getTotal();
		System.out.println("Old total " + total);
		System.out.println("Adding new -> " + amount);
		total = total.add(amount).setScale(2, BigDecimal.ROUND_HALF_UP);
		System.out.println("Total in CallBack -> " + total);
		order.setTotal(total);
	}

	@PreUpdate
	void recalculateTotal() {
		System.out.println("PreUpdate -> " + product.getName());
		BigDecimal total = new BigDecimal(0);
		if (getOrder().getLineitems().size() <= 0)
			return;
		boolean inOrders = false;
		for (LineItem item : getOrder().getLineitems()) {
			if (item.equals(this))
				inOrders = true;
			//System.out.println("recalc -> " + item.getAmount());
			total = total.add(item.getAmount()).setScale(2, BigDecimal.ROUND_HALF_UP);
		}
		System.out.println("New Total recalc -> " + total);
		if (inOrders)
			order.setTotal(total);
	}

	@PreRemove
	void calculateTotalRemove() {
		System.out.println("Removing LI -> " + product.getName());
		BigDecimal total = getOrder().getTotal();
		total = total.subtract(amount).setScale(2, BigDecimal.ROUND_HALF_UP);
		order.setTotal(total);
	}

	@JsonbTransient
	public void setVersion(long version) {
		this.version = version;
	}

	@JsonbTransient
	public long getVersion() {
		return version;
	}

}
