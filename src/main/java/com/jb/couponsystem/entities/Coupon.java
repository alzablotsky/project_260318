package com.jb.couponsystem.entities;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.jb.couponsystem.enums.CouponType;

/**
 * This class represents the coupon entity object.
 * The COUPONS table in the database is generated from this entity, 
 * while its attributes form the columns of the table.
 * 
 * @author Alexander Zablotsky
 * 
 */
@Entity(name="COUPONS")
public class Coupon  implements Serializable {

	//Attributes
	//Primary key of the table, generated automatically
	@Id @GeneratedValue (strategy = GenerationType.IDENTITY)
	private long id;

	@Column
	private String title;

	@Column
	private Date startDate;

	@Column
	private Date endDate;

	@Column
	private int amount;

	@Column
	private CouponType type;

	@Column
	private String message;

	@Column
	private double price;

	@Column
	private String image;

	/*
	 * The Company attribute is connected to the COMPANIES table.
	 * The connection is many-to-one: one company can issue many coupons, but each coupon is associated with only one company.
	 * This connection creates the join column COMPANY_ID in COUPONS table.
	 * On delete action CASCADE: when a company is deleted from the database, all its coupons are also deleted.
	 */
	@ManyToOne
	@JoinColumn(name = "company_id", nullable = false)
	@OnDelete(action = OnDeleteAction.CASCADE)
	private Company company;
	
	/*
	 * The Customers attribute is connected to the CUSTOMERS table.
	 * The connection is many-to-many: one customer can own many coupons, and  one coupon also can be owned by many customers.
	 * This connection creates the join table CUSTOMER_COUPON which contains two columns:
	 * COUPON_ID (join column, the owning side) and CUSTOMER_ID (inverse join column, the other side).
	 * Fetch type EAGER: when a coupon is loaded from the database, all its customers are loaded with it.
	 * Cascade type DETACH, MERGE, REFRESH: only the changes of the detach, merge or refresh type in the coupon entity
	 * are cascaded to the customers entities associated with it. 
	 */
	@ManyToMany(fetch=FetchType.EAGER, cascade = {CascadeType.DETACH , CascadeType.MERGE, CascadeType.REFRESH})
	@JoinTable(name = "customer_coupon",
	joinColumns = @JoinColumn(name = "coupon_id"),
	inverseJoinColumns = @JoinColumn(name = "customer_id"))
	private Collection<Customer> customers;


	//CTORS

	public Coupon() {
		super();
	}

	/**
	 * 
	 * @param title coupon's title
	 * @param startDate coupon's start date
	 * @param endDate coupon's end date
	 * @param amount the amount of available coupons of this title in stock
	 * @param type coupon's type
	 * @param message coupon's message
	 * @param price coupon's price
	 * @param image coupon's image
	 * @param customers coupon's customers
	 */
	public Coupon(String title, Date startDate, Date endDate, int amount, CouponType type, String message,
			double price, String image, Collection<Customer> customers) {
		super();
		this.title = title;
		this.startDate = startDate;
		this.endDate = endDate;
		this.amount = amount;
		this.type = type;
		this.message = message;
		this.price = price;
		this.image = image;
		this.customers = customers;
	}


	//Getters and setters
	/**
	 * @return the id
	 */
	public long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return the startDate
	 */
	public Date getStartDate() {
		return startDate;
	}

	/**
	 * @param startDate the startDate to set
	 */
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	/**
	 * @return the endDate
	 */
	public Date getEndDate() {
		return endDate;
	}

	/**
	 * @param endDate the endDate to set
	 */
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	/**
	 * @return the amount
	 */
	public int getAmount() {
		return amount;
	}

	/**
	 * @param amount the amount to set
	 */
	public void setAmount(int amount) {
		this.amount = amount;
	}

	/**
	 * @return the type
	 */
	public CouponType getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(CouponType type) {
		this.type = type;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * @return the price
	 */
	public double getPrice() {
		return price;
	}

	/**
	 * @param price the price to set
	 */
	public void setPrice(double price) {
		this.price = price;
	}

	/**
	 * @return the image
	 */
	public String getImage() {
		return image;
	}

	/**
	 * @param image the image to set
	 */
	public void setImage(String image) {
		this.image = image;
	}

	/**
	 * @return the company
	 */
	public Company getCompany() {
		return company;
	}

	/**
	 * @param company the company to set
	 */
	public void setCompany(Company company) {
		this.company = company;
	}

	/**
	 * @return the customers
	 */
	public Collection<Customer> getCustomers() {
		return customers;
	}

	/**
	 * @param customers the customers to set
	 */
	public void setCustomers(Collection<Customer> customers) {
		this.customers = customers;
	}

	/**
	 * Sets the start date received as String. 
	 * The method receives the String date from the outside and parses it to Date object using SimpleDateFormat.
	 * Catches ParseException which occurs if the String object cannot be parsed. 
	 * 	
	 * @param stringDate the String Date to set
	 */
	public void setStartDate(String stringDate){

		try{
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			this.startDate  = dateFormat.parse(stringDate);
		}

		catch(ParseException e){
			System.err.println(stringDate +" is not a valid date.");
			//e.printStackTrace();

		}
	}

	/**
	 * Sets the end date received as String. 
	 * The method receives the String date from the outside and parses it to Date object using SimpleDateFormat.
	 * Catches ParseException which occurs if the String object cannot be parsed. 
	 * 	
	 * @param stringDate the String Date to set
	 */
	public void setEndDate(String stringDate){

		try{
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			this.endDate  = dateFormat.parse(stringDate);
		}

		catch(ParseException e){
			System.err.println(stringDate +" is not a valid date.");
			//e.printStackTrace();

		}
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Coupon [id=" + id + ", title=" + title + ", startDate=" + startDate + ", endDate=" + endDate
				+ ", amount=" + amount + ", type=" + type + ", message=" + message + ", price=" + price + ", image="
				+ image + "]";
	}






}