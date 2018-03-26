package com.jb.couponsystem;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;

import com.jb.couponsystem.dao.*;
import com.jb.couponsystem.dbdao.CompanyDBDAO;
import com.jb.couponsystem.dbdao.CouponDBDAO;
import com.jb.couponsystem.entities.*;
import com.jb.couponsystem.entry.CouponSystem;
import com.jb.couponsystem.enums.ClientType;
import com.jb.couponsystem.enums.CouponType;
//import com.jb.couponsystem.exceptions.CouponAlreadyExistsException;
//import com.jb.couponsystem.exceptions.UserAlreadyExistsException;
//import com.jb.couponsystem.exceptions.UserNotFoundException;
//import com.jb.couponsystem.exceptions.WrongPasswordException;
import com.jb.couponsystem.exceptions.*;
import com.jb.couponsystem.facades.AdminFacade;
import com.jb.couponsystem.facades.CompanyFacade;
import com.jb.couponsystem.facades.CustomerFacade;
import com.jb.couponsystem.repo.CompanyRepo;
import com.jb.couponsystem.repo.CouponRepo;
import com.jb.couponsystem.repo.CustomerRepo;

/**
 * This class contains Spring Boot tests for the Coupon System application.
 * FixMethodOrder: All the tests are running according to the ascending name order.
 * RunWith: tests are run by SpringRunner class of the Spring Framework.
 * 
 * @author Alexander Zablotsky
 *
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(SpringRunner.class)
@SpringBootTest
public class ProjectApplicationTests {

	//Attributes	

	@Autowired
	ApplicationContext ctx;

	@Autowired
	CouponRepo couponRepo;

	@Autowired
	CompanyRepo companyRepo;

	@Autowired
	CustomerRepo customerRepo;

	@Autowired
	CouponDBDAO couponDBDAO;
	
	@Autowired
	CompanyDBDAO companyDBDAO;

	private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

	//Tests

	/**
	 * Test for daily thread. Creates new expired coupon in the database.
	 * Then creates an instance of the CouponSystem class which performs the daily task 
	 * of removing the expired coupon from the database.
	 * 
	 * @throws CouponAlreadyExistsException if the coupon exists in the database
	 * @throws InterruptedException if the thread was interrupted 
	 */
	@Ignore
	@Test
	public void test_000_dailyThread() throws CouponAlreadyExistsException, InterruptedException {
		Company c= new Company();
		companyDBDAO.createCompany(c);
		
		Coupon coupon = new Coupon();
		coupon.setTitle("Empty coupon");
		coupon.setEndDate("2017-01-01");
		coupon.setCompany(c);

		couponDBDAO.createCoupon(coupon);
		Assert.assertTrue(couponRepo.existsByTitle("Empty coupon"));

		CouponSystem couponsystem = new CouponSystem(ctx);

		try {
			Thread.sleep(10000);
		} 
		catch (InterruptedException e) {
			e.printStackTrace();
		}

		Assert.assertFalse(couponRepo.existsByTitle("Empty coupon"));


	}


	/**
	 * Test whether the Context loads.
	 */
	@Test
	public void test_001_contextLoads() {
	}

	//1. Test Admin Facade methods
	// login as admin
	/**
	 * Test for login method of AdminFacade.
	 * When the username and password are correct, the method returns the instance of AdminFacade.
	 */
	@Test
	public void test_002_adminLogin() {

		CouponSystem couponsystem = new CouponSystem(ctx);
		AdminFacade adminFacade = (AdminFacade) couponsystem.login("admin", "1234", ClientType.ADMIN);

		Assert.assertNotNull(adminFacade);
	}

	/**
	 * Test for login method of AdminFacade.
	 * When the username is incorrect, the method throws 
	 * UserNotFoundException and returns null.
	 */
	@Test (expected = UserNotFoundException.class)
	public void test_003_adminLogin() {

		CouponSystem couponsystem = new CouponSystem(ctx);
		AdminFacade adminFacade1 = (AdminFacade) couponsystem.login("admin1", "1234", ClientType.ADMIN);

		Assert.assertNull(adminFacade1);
	}

	//create company
	/**
	 * Test for createCompany method of AdminFacade.
	 * If the company name and ID were not previously used,
	 * the method creates the company in the database. 
	 */
	@Test
	public void test_004_adminCreateCompany() {

		CouponSystem couponsystem = new CouponSystem(ctx);
		AdminFacade adminFacade = (AdminFacade) couponsystem.login("admin", "1234", ClientType.ADMIN);
		Company comp= new Company("TEVA", "123", "teva@gmail.com");
		adminFacade.createCompany(comp);

		Assert.assertTrue(companyRepo.existsByCompanyName("TEVA"));
	}


	/**
	 * Test for createCompany method of AdminFacade.
	 * If the company name and ID were not previously used,
	 * the method creates the company in the database. 
	 */
	@Test
	public void test_005_adminCreateCompany() {

		CouponSystem couponsystem = new CouponSystem(ctx);
		AdminFacade adminFacade = (AdminFacade) couponsystem.login("admin", "1234", ClientType.ADMIN);
		Company comp= new Company("GOOGLE", "234", "google@gmail.com");
		adminFacade.createCompany(comp);

		Assert.assertTrue(companyRepo.existsByCompanyName("GOOGLE"));
	}


	/**
	 * Test for createCompany method of AdminFacade.
	 * If the company name and ID were not previously used,
	 * the method creates the company in the database. 
	 */
	@Test
	public void test_006_adminCreateCompany() {

		CouponSystem couponsystem = new CouponSystem(ctx);
		AdminFacade adminFacade = (AdminFacade) couponsystem.login("admin", "1234", ClientType.ADMIN);
		Company comp= new Company("AMDOCS", "345", "amdocs@gmail.com");
		adminFacade.createCompany(comp);

		Assert.assertTrue(companyRepo.existsByCompanyName("AMDOCS"));
	}

	/**
	 * Test for createCompany method of AdminFacade.
	 * If the company name already exists in the database,
	 * UserAlreadyExistsException is thrown and the company is not created. 
	 */

	@Test (expected = UserAlreadyExistsException.class)
	public void test_007_adminCreateCompany() {

		CouponSystem couponsystem = new CouponSystem(ctx);
		AdminFacade adminFacade = (AdminFacade) couponsystem.login("admin", "1234", ClientType.ADMIN);
		Company comp= new Company("TEVA", "234", "teva@gmail.com");
		adminFacade.createCompany(comp);

		//Cannot create two companies with the same name
		Assert.assertFalse(companyRepo.findCompanyByCompanyName("TEVA").size()>1);
	}

	/**
	 * Test for createCompany method of AdminFacade.
	 * If the company ID already exists in the database,
	 * UserAlreadyExistsException is thrown and the company is not created. 
	 */
	@Test (expected = UserAlreadyExistsException.class)
	public void test_008_adminCreateCompany() {

		CouponSystem couponsystem = new CouponSystem(ctx);
		AdminFacade adminFacade = (AdminFacade) couponsystem.login("admin", "1234", ClientType.ADMIN);
		Company comp= new Company("SONOL", "444", "sonol@gmail.com");
		comp.setId(1);
		adminFacade.createCompany(comp);

		//Cannot create a company with id used by another company
		Assert.assertFalse(companyRepo.existsByCompanyName("SONOL"));

	}

	/**
	 * Test for createCompany method of AdminFacade.
	 * If the company name and ID were not previously used,
	 * the method creates the company in the database. 
	 */
	@Test
	public void test_009_adminCreateCompany() {

		CouponSystem couponsystem = new CouponSystem(ctx);
		AdminFacade adminFacade = (AdminFacade) couponsystem.login("admin", "1234", ClientType.ADMIN);
		Company comp= new Company("SONOL", "444", "sonol@gmail.com");
		adminFacade.createCompany(comp);

		Assert.assertTrue(companyRepo.existsByCompanyName("SONOL"));
	}

	//remove company
	/**
	 * Test for removeCompany method of AdminFacade.
	 * If the company details are correct,
	 * the method removes the company from the database. 
	 */
	@Test
	public void test_010_adminRemoveCompany() {

		CouponSystem couponsystem = new CouponSystem(ctx);
		AdminFacade adminFacade = (AdminFacade) couponsystem.login("admin", "1234", ClientType.ADMIN);
		Company comp= companyRepo.findByName("GOOGLE");
		adminFacade.removeCompany(comp);

		Assert.assertFalse(companyRepo.existsByCompanyName("GOOGLE"));

	}

	/**
	 * Test for removeCompany method of AdminFacade.
	 * If the company does not exist in the database,
	 * the method throws UserNotFoundException and
	 * does not make changes in the database.  
	 */

	@Test (expected = UserNotFoundException.class)
	public void test_011_adminRemoveCompany() {

		CouponSystem couponsystem = new CouponSystem(ctx);
		AdminFacade adminFacade = (AdminFacade) couponsystem.login("admin", "1234", ClientType.ADMIN);
		Company comp= new Company();
		comp.setCompanyName("HASHMAL");

		//cannot remove company that does not exist
		long countBefore = companyRepo.count();

		adminFacade.removeCompany(comp);

		long countAfter = companyRepo.count();
		Assert.assertEquals(countBefore, countAfter);

	}


	//update company
	/**
	 * Test for updateCompany method of AdminFacade.
	 * If the company details are correct,
	 * and no attempt of illegal update is made, 
	 * the method updates the company in the database. 
	 */
	@Test
	public void test_012_adminUpdateCompany() {

		CouponSystem couponsystem = new CouponSystem(ctx);
		AdminFacade adminFacade = (AdminFacade) couponsystem.login("admin", "1234", ClientType.ADMIN);
		Company comp= companyRepo.findByName("AMDOCS"); 
		comp.setPassword("456");
		adminFacade.updateCompany(comp);

		String updated= companyRepo.findByName("AMDOCS").getPassword();
		Assert.assertEquals("456", updated);

	}	

	/**
	 * Test for updateCompany method of AdminFacade.
	 * If the company details are correct,
	 * and no attempt of illegal update is made, 
	 * the method updates the company in the database. 
	 */
	@Test
	public void test_013_adminUpdateCompany() {

		CouponSystem couponsystem = new CouponSystem(ctx);
		AdminFacade adminFacade = (AdminFacade) couponsystem.login("admin", "1234", ClientType.ADMIN);
		Company comp= companyRepo.findByName("AMDOCS"); 
		comp.setEmail("amdocs1@gmail.com");
		adminFacade.updateCompany(comp);

		String updated= companyRepo.findByName("AMDOCS").getEmail();
		Assert.assertEquals("amdocs1@gmail.com", updated);
	}	

	/**
	 * Test for updateCompany method of AdminFacade.
	 * If there is an attempt to change the company name,
	 * the method throws IllegalUpdateException and does not update the company. 
	 */
	@Test (expected =  IllegalUpdateException.class)
	public void test_014_adminUpdateCompany() {

		CouponSystem couponsystem = new CouponSystem(ctx);
		AdminFacade adminFacade = (AdminFacade) couponsystem.login("admin", "1234", ClientType.ADMIN);
		Company comp= companyRepo.findByName("AMDOCS"); 
		comp.setCompanyName("AMDOCS1");
		adminFacade.updateCompany(comp);

		//Cannot update company name
		Assert.assertNull(companyRepo.findByName("AMDOCS1"));
	}	

	/**
	 * Test for updateCompany method of AdminFacade.
	 * If the company does not exist in the database
	 * the method throws UserNotFoundException. 
	 */
	@Test  (expected = UserNotFoundException.class)
	public void test_015_adminUpdateCompany() {

		CouponSystem couponsystem = new CouponSystem(ctx);
		AdminFacade adminFacade = (AdminFacade) couponsystem.login("admin", "1234", ClientType.ADMIN);
		Company comp= new Company();
		comp.setCompanyName("HASHMAL");
		comp.setPassword("123");
		adminFacade.updateCompany(comp);

		//Cannot update company that does not exist in DB
		Assert.assertNull(companyRepo.findByName("HASHMAL"));
	}



	//get company by id

	/**
	 * Test for getCompany method of AdminFacade.
	 * If the company id exists in the database
	 * the method displays the company object 
	 * from the database with the same id. 
	 */
	@Test
	public void test_016_adminGetCompany() {

		CouponSystem couponsystem = new CouponSystem(ctx);
		AdminFacade adminFacade = (AdminFacade) couponsystem.login("admin", "1234", ClientType.ADMIN);
		Company comp= adminFacade.getCompany(1);
		System.out.println("Company id 1: "+ comp);

		Assert.assertNotNull(comp);
		Assert.assertEquals(1, comp.getId());
	}

	/**
	 * Test for getCompany method of AdminFacade.
	 * If the company id does not exist in the database
	 * the method throws UserNotFoundException and no company is displayed.
	 */
	@Test (expected = UserNotFoundException.class)
	public void test_017_adminGetCompany() {

		CouponSystem couponsystem = new CouponSystem(ctx);
		AdminFacade adminFacade = (AdminFacade) couponsystem.login("admin", "1234", ClientType.ADMIN);
		Company comp= adminFacade.getCompany(6);
		System.out.println("Company id 6: "+ comp);

		Assert.assertNull(comp);

	}

	//get all companies
	/**
	 * Test for getAllCompanies method of AdminFacade.
	 * If there are any companies in the database
	 * the method displays all these companies.
	 */
	@Test
	public void test_018_adminGetAllCompanies() {

		CouponSystem couponsystem = new CouponSystem(ctx);
		AdminFacade adminFacade = (AdminFacade) couponsystem.login("admin", "1234", ClientType.ADMIN);
		Collection <Company> comps= adminFacade.getAllCompanies();
		System.out.println("Companies list: \n"+ comps);

		Assert.assertNotNull(comps);
		Assert.assertEquals(companyRepo.count(), comps.size());

	}


	//create customer
	/**
	 * Test for createCustomer method of AdminFacade.
	 * If the customer name and ID were not previously used,
	 * the method creates the customer in the database. 
	 */
	@Test
	public void test_019_adminCreateCustomer() {

		CouponSystem couponsystem = new CouponSystem(ctx);
		AdminFacade adminFacade = (AdminFacade) couponsystem.login("admin", "1234", ClientType.ADMIN);
		Customer cust= new Customer("Avi", "111");
		adminFacade.createCustomer(cust);

		Assert.assertTrue(customerRepo.existsByCustomerName("Avi"));
	}

	/**
	 * Test for createCustomer method of AdminFacade.
	 * If the customer name and ID were not previously used,
	 * the method creates the customer in the database. 
	 */
	@Test
	public void test_020_adminCreateCustomer() {

		CouponSystem couponsystem = new CouponSystem(ctx);
		AdminFacade adminFacade = (AdminFacade) couponsystem.login("admin", "1234", ClientType.ADMIN);
		Customer cust= new Customer("Benny", "222");
		adminFacade.createCustomer(cust);

		Assert.assertTrue(customerRepo.existsByCustomerName("Benny"));
	}

	/**
	 * Test for createCustomer method of AdminFacade.
	 * If the customer name and ID were not previously used,
	 * the method creates the customer in the database. 
	 */
	@Test
	public void test_021_adminCreateCustomer() {

		CouponSystem couponsystem = new CouponSystem(ctx);
		AdminFacade adminFacade = (AdminFacade) couponsystem.login("admin", "1234", ClientType.ADMIN);
		Customer cust= new Customer("Gabi", "333");
		adminFacade.createCustomer(cust);

		Assert.assertTrue(customerRepo.existsByCustomerName("Gabi"));
	}

	/**
	 * Test for createCustomer method of AdminFacade.
	 * If the customer name exists in the database,
	 * the method throws UserAlreadyExistsException and the customer is not created. 
	 */
	@Test (expected = UserAlreadyExistsException.class)
	public void test_022_adminCreateCustomer() {

		CouponSystem couponsystem = new CouponSystem(ctx);
		AdminFacade adminFacade = (AdminFacade) couponsystem.login("admin", "1234", ClientType.ADMIN);
		Customer cust= new Customer("Gabi", "444");
		adminFacade.createCustomer(cust);

		//Cannot create two customers with the same name
		Assert.assertFalse(customerRepo.findCustomerByCustomerName("Gabi").size()>1);
	}

	/**
	 * Test for createCustomer method of AdminFacade.
	 * If the customer name and ID were not previously used,
	 * the method creates the customer in the database. 
	 */
	@Test
	public void test_023_adminCreateCustomer() {

		CouponSystem couponsystem = new CouponSystem(ctx);
		AdminFacade adminFacade = (AdminFacade) couponsystem.login("admin", "1234", ClientType.ADMIN);
		Customer cust= new Customer("Dudi", "555");
		adminFacade.createCustomer(cust);

		Assert.assertTrue(customerRepo.existsByCustomerName("Dudi"));
	}


	//remove customer
	/**
	 * Test for removeCustomer method of AdminFacade.
	 * If the customer details are correct,
	 * the method removes the customer from the database. 
	 */
	@Test
	public void test_024_adminRemoveCustomer() {

		CouponSystem couponsystem = new CouponSystem(ctx);
		AdminFacade adminFacade = (AdminFacade) couponsystem.login("admin", "1234", ClientType.ADMIN);
		Customer cust= customerRepo.findByName("Benny"); 

		adminFacade.removeCustomer(cust);

		Assert.assertFalse(customerRepo.existsByCustomerName("Benny"));
	}			

	/**
	 * Test for removeCustomer method of AdminFacade.
	 * If the customer does not exist in the database,
	 * the method throws UserNotFoundException and 
	 * does not make changes in the database. 
	 */
	@Test (expected = UserNotFoundException.class)
	public void test_025_adminRemoveCustomer() {

		CouponSystem couponsystem = new CouponSystem(ctx);
		AdminFacade adminFacade = (AdminFacade) couponsystem.login("admin", "1234", ClientType.ADMIN);
		Customer cust=  new Customer ();
		cust.setCustomerName("Harel");

		//cannot remove customer that does not exist
		long countBefore = customerRepo.count();
		adminFacade.removeCustomer(cust);

		long countAfter = customerRepo.count();
		Assert.assertEquals(countBefore, countAfter);
	}

	//update customer
	/**
	 * Test for updateCustomer method of AdminFacade.
	 * If the customer details are correct,
	 * and no attempt of illegal update is made, 
	 * the method updates the customer in the database. 
	 */
	@Test
	public void test_026_adminUpdateCustomer() {

		CouponSystem couponsystem = new CouponSystem(ctx);
		AdminFacade adminFacade = (AdminFacade) couponsystem.login("admin", "1234", ClientType.ADMIN);
		Customer cust= customerRepo.findByName("Gabi");
		cust.setPassword("444");
		adminFacade.updateCustomer(cust);

		Assert.assertEquals("444", customerRepo.findByName("Gabi").getPassword());


	}	

	/**
	 * Test for updateCustomer method of AdminFacade.
	 * If there is an attempt to change customer name, 
	 * the method throws IllegalUpdateException,
	 * and the customer is not updated.
	 */
	@Test (expected = IllegalUpdateException.class)
	public void test_027_adminUpdateCustomer() {

		CouponSystem couponsystem = new CouponSystem(ctx);
		AdminFacade adminFacade = (AdminFacade) couponsystem.login("admin", "1234", ClientType.ADMIN);
		Customer cust= customerRepo.findByName("Gabi");
		cust.setCustomerName("Gabriel");
		adminFacade.updateCustomer(cust);

		//cannot update customer name
		Assert.assertNull(customerRepo.findByName("Gabriel"));
	}

	/**
	 * Test for updateCustomer method of AdminFacade.
	 * If the customer with the given name does not exist in the database, 
	 * the method throws UserNotFoundException,
	 * and the customer is not updated.
	 */
	@Test (expected = UserNotFoundException.class)
	public void test_028_adminUpdateCustomer() {

		CouponSystem couponsystem = new CouponSystem(ctx);
		AdminFacade adminFacade = (AdminFacade) couponsystem.login("admin", "1234", ClientType.ADMIN);
		Customer cust= new Customer();
		cust.setCustomerName("Daniel");
		adminFacade.updateCustomer(cust);

		//cannot update customer that does not exist in the database
		Assert.assertNull(customerRepo.findByName("Daniel"));
	}

	//get customer by id
	/**
	 * Test for getCustomer method of AdminFacade.
	 * If the customer id exists in the database,
	 * the method displays the customer object 
	 * from the database with the same id. 
	 */
	@Test
	public void test_029_adminGetCustomer() {

		CouponSystem couponsystem = new CouponSystem(ctx);
		AdminFacade adminFacade = (AdminFacade) couponsystem.login("admin", "1234", ClientType.ADMIN);
		Customer cust= adminFacade.getCustomer(1);
		System.out.println("Customer id 1: "+ cust);

		Assert.assertNotNull(cust);
		Assert.assertEquals(1, cust.getId());
	}

	/**
	 * Test for getCustomer method of AdminFacade.
	 * If the customer id does not exist in the database,
	 * the method throws UserNotFoundException and no customer is displayed.
	 */
	@Test (expected = UserNotFoundException.class)
	public void test_030_adminGetCustomer() {

		CouponSystem couponsystem = new CouponSystem(ctx);
		AdminFacade adminFacade = (AdminFacade) couponsystem.login("admin", "1234", ClientType.ADMIN);
		Customer cust= adminFacade.getCustomer(6);
		System.out.println("Customer id 6: "+ cust);

		Assert.assertNull(cust);
	}

	//get all customers
	/**
	 * Test for getAllCustomers method of AdminFacade.
	 * If there are any customers in the database
	 * the method displays all these customers.
	 */
	@Test
	public void test_031_adminGetAllCustomers() {

		CouponSystem couponsystem = new CouponSystem(ctx);
		AdminFacade adminFacade = (AdminFacade) couponsystem.login("admin", "1234", ClientType.ADMIN);
		Collection <Customer> custs= adminFacade.getAllCustomers();
		System.out.println("Customers list: \n"+ custs);

		Assert.assertNotNull(custs);
		Assert.assertEquals(customerRepo.count(), custs.size());

	}


	//2. Test Company Facade methods

	// login as a company
	/**
	 * Test for login method of CompanyFacade class.
	 * When the username and password are correct, the method returns
	 * the instance of CompanyFacade.
	 */
	@Test
	public void test_032_companyLogin() {

		CouponSystem couponsystem = new CouponSystem(ctx);
		CompanyFacade teva = (CompanyFacade) couponsystem.login("TEVA", "123", ClientType.COMPANY);

		Assert.assertNotNull(teva);
	}

	/**
	 * Test for login method of CompanyFacade class.
	 * When the username is incorrect, the method throws 
	 * UserNotFoundException and returns null.
	 */
	@Test (expected= UserNotFoundException.class )
	public void test_033_companyLogin() { 

		CouponSystem couponsystem = new CouponSystem(ctx);
		CompanyFacade teva1 = (CompanyFacade) couponsystem.login("TEVA1", "123", ClientType.COMPANY);
		Assert.assertNull(teva1);
	}

	/**
	 * Test for login method of CompanyFacade class.
	 * When the password is incorrect, the method throws 
	 * WrongPasswordException and returns null.
	 */
	@Test (expected= WrongPasswordException.class )
	public void test_034_companyLogin() {

		CouponSystem couponsystem = new CouponSystem(ctx);
		CompanyFacade teva2 = (CompanyFacade) couponsystem.login("TEVA", "1234", ClientType.COMPANY);
		Assert.assertNull(teva2);
	}


	// create coupon
	/**
	 * Test for createCoupon method of CompanyFacade.
	 * If the coupon name and ID were not previously used,
	 * the method creates the coupon in the database. 
	 *
	 * @throws CouponAlreadyExistsException if the coupon exists in the database
	 */
	@Test
	public void test_035_companyCreateCoupon() throws CouponAlreadyExistsException 	{

		CouponSystem couponsystem = new CouponSystem(ctx);
		CompanyFacade teva = (CompanyFacade) couponsystem.login("TEVA", "123", ClientType.COMPANY);

		Coupon coupon = new Coupon();
		coupon.setTitle("Free camping");
		coupon.setMessage("You can camp for free...");
		coupon.setAmount(2);
		coupon.setType(CouponType.CAMPING);
		coupon.setPrice(200);
		coupon.setStartDate("2017-06-01");
		coupon.setEndDate("2018-06-01");
		coupon.setImage("My image");

		teva.createCoupon(coupon);

		Assert.assertTrue(couponRepo.existsByTitle("Free camping"));
	}

	/**
	 * Test for createCoupon method of CompanyFacade.
	 * If the coupon name and ID were not previously used,
	 * the method creates the coupon in the database.
	 *
	 * @throws CouponAlreadyExistsException if the coupon exists in the database
	 */
	@Test
	public void test_036_companyCreateCoupon() throws CouponAlreadyExistsException 	{

		CouponSystem couponsystem = new CouponSystem(ctx);
		CompanyFacade teva = (CompanyFacade) couponsystem.login("TEVA", "123", ClientType.COMPANY);

		Coupon coupon = new Coupon();
		coupon.setTitle("Dinner for two");
		coupon.setMessage("You can have a dinner...");
		coupon.setAmount(20);
		coupon.setType(CouponType.RESTAURANTS);
		coupon.setPrice(100);
		coupon.setStartDate("aaa");
		coupon.setStartDate("2017-01-01");
		coupon.setEndDate("2018-02-01");
		coupon.setImage("My image");

		teva.createCoupon(coupon);

		Assert.assertTrue(couponRepo.existsByTitle("Dinner for two"));
	}

	/**
	 * Test for createCoupon method of CompanyFacade.
	 * If the coupon name with the same name exists in the database,
	 * the method throws CouponAlreadyExistsException,
	 * and no coupon is created.
	 *
	 * @throws CouponAlreadyExistsException if the coupon exists in the database 
	 */
	@Test (expected = CouponAlreadyExistsException.class)
	public void test_037_companyCreateCoupon() throws CouponAlreadyExistsException 	{

		CouponSystem couponsystem = new CouponSystem(ctx);
		CompanyFacade teva = (CompanyFacade) couponsystem.login("TEVA", "123", ClientType.COMPANY);

		Coupon coupon = new Coupon();
		coupon.setTitle("Free camping");
		coupon.setMessage("You can camp for free...");
		coupon.setAmount(20);
		coupon.setType(CouponType.TRAVELLING);
		coupon.setPrice(200);
		coupon.setStartDate("2018-06-01");
		coupon.setEndDate("2019-06-01");
		coupon.setImage("My image");

		teva.createCoupon(coupon);

		//Cannot create 2 coupons with the same name
		Assert.assertFalse(couponRepo.findCouponByTitle("Free camping").size()>1);
	}

	/**
	 * Test for createCoupon method of CompanyFacade.
	 * If the coupon name and ID were not previously used,
	 * the method creates the coupon in the database.
	 *
	 * @throws CouponAlreadyExistsException if the coupon exists in the database 
	 */
	@Test
	public void test_038_companyCreateCoupon() throws CouponAlreadyExistsException 	{

		CouponSystem couponsystem = new CouponSystem(ctx);
		CompanyFacade amdocs = (CompanyFacade) couponsystem.login("AMDOCS", "456", ClientType.COMPANY);

		Coupon coupon = new Coupon();
		coupon.setTitle("Flight to Ibiza");
		coupon.setMessage("You can flight to Ibiza...");
		coupon.setAmount(30);
		coupon.setType(CouponType.TRAVELLING);
		coupon.setPrice(150);
		coupon.setStartDate("2017-10-01");
		coupon.setEndDate("2019-10-01");
		coupon.setImage("My image");

		amdocs.createCoupon(coupon);

		Assert.assertTrue(couponRepo.existsByTitle("Flight to Ibiza"));

	}


	/**
	 * Test for createCoupon method of CompanyFacade.
	 * If the coupon name and ID were not previously used,
	 * the method creates the coupon in the database.
	 *
	 * @throws CouponAlreadyExistsException if the coupon exists in the database 
	 */
	@Test
	public void test_039_companyCreateCoupon() throws CouponAlreadyExistsException 	{

		CouponSystem couponsystem = new CouponSystem(ctx);
		CompanyFacade amdocs = (CompanyFacade) couponsystem.login("AMDOCS", "456", ClientType.COMPANY);

		Coupon coupon = new Coupon();
		coupon.setTitle("Gym membership");
		coupon.setMessage("You can go to a gym...");
		coupon.setAmount(30);
		coupon.setType(CouponType.SPORTS);
		coupon.setPrice(150);
		coupon.setStartDate("2017-05-01");
		coupon.setEndDate("2018-05-01");
		coupon.setImage("My image");

		amdocs.createCoupon(coupon);

		Assert.assertTrue(couponRepo.existsByTitle("Gym membership"));

	}

	/**
	 * Test for createCoupon method of CompanyFacade.
	 * If the coupon name and ID were not previously used,
	 * the method creates the coupon in the database.
	 *
	 * @throws CouponAlreadyExistsException if the coupon exists in the database 
	 */
	@Test
	public void test_040_companyCreateCoupon() throws CouponAlreadyExistsException 	{

		CouponSystem couponsystem = new CouponSystem(ctx);
		CompanyFacade amdocs = (CompanyFacade) couponsystem.login("AMDOCS", "456", ClientType.COMPANY);

		Coupon coupon = new Coupon();
		coupon.setTitle("Shopping in the mall");
		coupon.setMessage("You can go shopping...");
		coupon.setAmount(30);
		coupon.setType(CouponType.FOOD);
		coupon.setPrice(500);
		coupon.setStartDate("2017-09-01");
		coupon.setEndDate("2018-09-01");
		coupon.setImage("My image");

		amdocs.createCoupon(coupon);

		Assert.assertTrue(couponRepo.existsByTitle("Shopping in the mall"));

	}

	//Remove coupon
	/**
	 * Test for removeCoupon method of CompanyFacade.
	 * If the coupon details are correct,
	 * the method removes the company's coupon from the database. 
	 */
	@Test
	public void test_041_companyRemoveCoupon() {

		CouponSystem couponsystem = new CouponSystem(ctx);
		Coupon coupon = couponRepo.findByTitle("Gym membership");
		CompanyFacade amdocs = (CompanyFacade) couponsystem.login("AMDOCS", "456", ClientType.COMPANY);
		amdocs.removeCoupon(coupon);

		Assert.assertFalse(couponRepo.existsByTitle("Gym membership"));

	}

	/**
	 * Test for removeCoupon method of CompanyFacade.
	 * If the coupon belongs to another company,
	 * the method throws CouponNotFoundException,
	 * and the coupon is not removed from the database. 
	 */
	@Test (expected = CouponNotFoundException.class )
	public void test_042_companyRemoveCoupon() {

		CouponSystem couponsystem = new CouponSystem(ctx);
		Coupon coupon = couponRepo.findByTitle("Free camping");
		CompanyFacade amdocs = (CompanyFacade) couponsystem.login("AMDOCS", "456", ClientType.COMPANY);
		amdocs.removeCoupon(coupon);

		//Cannot remove coupon of another company
		Assert.assertTrue(couponRepo.existsByTitle("Free camping"));

	}

	//Update coupon

	//can update price and end date only

	/**
	 * Test for updateCoupon method of CompanyFacade.
	 * Only the price and the end date of the coupon can be updated.
	 * If the coupon details are correct,
	 * and the coupon price is updated,  
	 * the method updates the coupon in the database.
	 */

	@Test
	public void test_043_companyUpdateCoupon() {

		CouponSystem couponsystem = new CouponSystem(ctx);
		Coupon coupon = couponRepo.findByTitle("Shopping in the mall");
		coupon.setPrice(550);
		CompanyFacade amdocs = (CompanyFacade) couponsystem.login("AMDOCS", "456", ClientType.COMPANY);
		amdocs.updateCoupon(coupon);

		//can update price
		double updatePrice = couponRepo.findByTitle("Shopping in the mall").getPrice();
		Assert.assertEquals(coupon.getPrice(), updatePrice, 0.01);
	}

	/**
	 * Test for updateCoupon method of CompanyFacade.
	 * Only the price and the end date of the coupon can be updated.
	 * If the coupon details are correct,
	 * and the coupon price is updated,  
	 * the method updates the coupon in the database.
	 */
	@Test
	public void test_044_companyUpdateCoupon() {

		CouponSystem couponsystem = new CouponSystem(ctx);
		Coupon coupon = couponRepo.findByTitle("Shopping in the mall");
		coupon.setEndDate("2018-10-01");
		CompanyFacade amdocs = (CompanyFacade) couponsystem.login("AMDOCS", "456", ClientType.COMPANY);
		amdocs.updateCoupon(coupon);

		//can update end date
		Date updateEndDate = couponRepo.findByTitle("Shopping in the mall").getEndDate();
		Assert.assertEquals(coupon.getEndDate(), updateEndDate);
	}


	/**
	 * Test for updateCoupon method of CompanyFacade.
	 * If the attempt to update the coupon ID is made,
	 * the method throws IllegalUpdateException,
	 * and the coupon is not updated.
	 */
	@Test (expected = IllegalUpdateException.class)
	public void test_045_companyUpdateCoupon() {
		CouponSystem couponsystem = new CouponSystem(ctx);
		Coupon coupon = couponRepo.findByTitle("Shopping in the mall");
		coupon.setId(10);
		CompanyFacade amdocs = (CompanyFacade) couponsystem.login("AMDOCS", "456", ClientType.COMPANY);
		amdocs.updateCoupon(coupon);

		//Cannot update id
		long updateId = couponRepo.findByTitle("Shopping in the mall").getId();
		Assert.assertNotSame(coupon.getId(), updateId);
	}

	/**
	 * Test for updateCoupon method of CompanyFacade.
	 * If the attempt to update the coupon start date is made,
	 * the method throws IllegalUpdateException,
	 * and the coupon is not updated.
	 */
	@Test (expected = IllegalUpdateException.class)
	public void test_046_companyUpdateCoupon() {
		CouponSystem couponsystem = new CouponSystem(ctx);
		Coupon coupon = couponRepo.findByTitle("Shopping in the mall");
		coupon.setStartDate("2019-01-01");
		CompanyFacade amdocs = (CompanyFacade) couponsystem.login("AMDOCS", "456", ClientType.COMPANY);
		amdocs.updateCoupon(coupon);

		//Cannot update Start Date
		Date updateStartDate = couponRepo.findByTitle("Shopping in the mall").getStartDate();
		Assert.assertNotEquals(coupon.getStartDate(), updateStartDate);
	}

	/**
	 * Test for updateCoupon method of CompanyFacade.
	 * If the attempt to update the coupon amount is made,
	 * the method throws IllegalUpdateException,
	 * and the coupon is not updated.
	 */
	@Test (expected = IllegalUpdateException.class)
	public void test_047_companyUpdateCoupon() {
		CouponSystem couponsystem = new CouponSystem(ctx);
		Coupon coupon = couponRepo.findByTitle("Shopping in the mall");
		coupon.setAmount(50);
		CompanyFacade amdocs = (CompanyFacade) couponsystem.login("AMDOCS", "456", ClientType.COMPANY);
		amdocs.updateCoupon(coupon);

		//Cannot update amount
		int updateAmount = couponRepo.findByTitle("Shopping in the mall").getAmount();
		Assert.assertNotSame(coupon.getAmount(), updateAmount);
	}


	/**
	 * Test for updateCoupon method of CompanyFacade.
	 * If the attempt to update the coupon type is made,
	 * the method throws IllegalUpdateException,
	 * and the coupon is not updated.
	 */
	@Test (expected = IllegalUpdateException.class)
	public void test_048_companyUpdateCoupon() {
		CouponSystem couponsystem = new CouponSystem(ctx);
		Coupon coupon = couponRepo.findByTitle("Shopping in the mall");
		coupon.setType(CouponType.ELECTRICITY);
		CompanyFacade amdocs = (CompanyFacade) couponsystem.login("AMDOCS", "456", ClientType.COMPANY);
		amdocs.updateCoupon(coupon);

		//Cannot update type
		CouponType updateType = couponRepo.findByTitle("Shopping in the mall").getType();
		Assert.assertNotEquals(coupon.getType(), updateType);
	}



	/**
	 * Test for updateCoupon method of CompanyFacade.
	 * If the attempt to update the coupon message is made,
	 * the method throws IllegalUpdateException,
	 * and the coupon is not updated.
	 */
	@Test (expected = IllegalUpdateException.class)
	public void test_049_companyUpdateCoupon() {
		CouponSystem couponsystem = new CouponSystem(ctx);
		Coupon coupon = couponRepo.findByTitle("Shopping in the mall");
		coupon.setMessage("Hello");
		CompanyFacade amdocs = (CompanyFacade) couponsystem.login("AMDOCS", "456", ClientType.COMPANY);
		amdocs.updateCoupon(coupon);

		//Cannot update message
		String updateMessage = couponRepo.findByTitle("Shopping in the mall").getMessage();
		Assert.assertNotEquals(coupon.getMessage(), updateMessage);
	}

	/**
	 * Test for updateCoupon method of CompanyFacade.
	 * If the attempt to update the coupon image is made,
	 * the method throws IllegalUpdateException,
	 * and the coupon is not updated.
	 */
	@Test (expected = IllegalUpdateException.class)
	public void test_050_companyUpdateCoupon() {
		CouponSystem couponsystem = new CouponSystem(ctx);
		Coupon coupon = couponRepo.findByTitle("Shopping in the mall");
		coupon.setImage("Other image");
		CompanyFacade amdocs = (CompanyFacade) couponsystem.login("AMDOCS", "456", ClientType.COMPANY);
		amdocs.updateCoupon(coupon);

		//Cannot update image
		String updateImage = couponRepo.findByTitle("Shopping in the mall").getImage();
		Assert.assertNotEquals(coupon.getImage(), updateImage);
	}


	/**
	 * Test for updateCoupon method of CompanyFacade.
	 * If the coupon does not exist in the database ,
	 * the method throws CouponNotFoundException,
	 * and the coupon is not updated.
	 */
	@Test (expected = CouponNotFoundException.class)
	public void test_051_companyUpdateCoupon() {
		CouponSystem couponsystem = new CouponSystem(ctx);

		Coupon coupon = new Coupon();
		coupon.setPrice(100);

		CompanyFacade amdocs = (CompanyFacade) couponsystem.login("AMDOCS", "456", ClientType.COMPANY);
		amdocs.updateCoupon(coupon);

		//Cannot update coupon that does not exist
		Assert.assertFalse(couponRepo.exists(coupon.getId()));
	}

	//Get coupon
	/**
	 * Test for getCoupon method of CompanyFacade.
	 * If the coupon of the logged in company with the given ID exists in the database,
	 * the method displays the coupon object from the database. 
	 */
	@Test
	public void test_052_companyGetCoupon() {

		CouponSystem couponsystem = new CouponSystem(ctx);
		CompanyFacade teva = (CompanyFacade) couponsystem.login("TEVA", "123", ClientType.COMPANY);
		Coupon coupon = teva.getCoupon(1);
		System.out.println(coupon);

		Assert.assertNotNull(coupon);
		Assert.assertEquals(1, coupon.getId());
	}

	/**
	 * Test for getCoupon method of CompanyFacade.
	 * If the coupon  with the given ID does not belong to the logged in company,
	 * the method throws CouponNotFoundException, and no coupon is displayed. 
	 */
	@Test  (expected = CouponNotFoundException.class)
	public void test_053_companyGetCoupon() {

		CouponSystem couponsystem = new CouponSystem(ctx);
		CompanyFacade teva = (CompanyFacade) couponsystem.login("TEVA", "123", ClientType.COMPANY);
		Coupon coupon = teva.getCoupon(3);
		System.out.println(coupon);

		//Cannot get coupon that does not exist in this company		
		Assert.assertNull(coupon);
	}

	//Get all coupons

	/**
	 * Test for getAllCoupons method of CompanyFacade.
	 * If there are any coupons of the logged in company in the database
	 * the method displays all these coupons.
	 */
	@Test
	public void test_054_companyGetAllCoupons() {

		CouponSystem couponsystem = new CouponSystem(ctx);
		CompanyFacade teva = (CompanyFacade) couponsystem.login("TEVA", "123", ClientType.COMPANY);
		Collection <Coupon> coupons = teva.getAllCoupons();
		System.out.println(coupons);

		Assert.assertNotNull(coupons);

		int sizeInDb= couponRepo.findCouponByCompanyId(teva.getLoginCompany().getId()).size();

		Assert.assertEquals(sizeInDb, coupons.size());

	}

	/**
	 * Test for getAllCoupons method of CompanyFacade.
	 * If there are any coupons of the logged in company in the database
	 * the method displays all these coupons.
	 */
	@Test
	public void test_055_companyGetAllCoupons() {

		CouponSystem couponsystem = new CouponSystem(ctx);
		CompanyFacade amdocs = (CompanyFacade) couponsystem.login("AMDOCS", "456", ClientType.COMPANY);
		Collection <Coupon> coupons = amdocs.getAllCoupons();
		System.out.println(coupons);

		Assert.assertNotNull(coupons);

		int sizeInDb= couponRepo.findCouponByCompanyId(amdocs.getLoginCompany().getId()).size();

		Assert.assertEquals(sizeInDb, coupons.size());
	}


	/**
	 * Test for getAllCoupons method of CompanyFacade.
	 * If there are no coupons of the logged in company in the database
	 * the method throws CouponNotFoundException, and no coupons are displayed. 
	 */
	@Test (expected = CouponNotFoundException.class)
	public void test_056_companyGetAllCoupons() {

		CouponSystem couponsystem = new CouponSystem(ctx);
		CompanyFacade sonol = (CompanyFacade) couponsystem.login("SONOL", "444", ClientType.COMPANY);
		Collection <Coupon> coupons = sonol.getAllCoupons();

		//cannot get coupons if the company does not have any coupons
		Assert.assertNull(coupons);
	}

	//Get coupons by type
	/**
	 * Test for getCouponsByType method of CompanyFacade.
	 * If there are coupons of the logged in company of the given type
	 * in the database, the method displays all these coupons. 
	 */
	@Test
	public void test_057_companyGetCouponsByType() {

		CouponSystem couponsystem = new CouponSystem(ctx);
		CompanyFacade teva = (CompanyFacade) couponsystem.login("TEVA", "123", ClientType.COMPANY);
		Collection <Coupon> coupons = teva.getCouponsByType(CouponType.CAMPING);
		System.out.println(coupons);

		Assert.assertNotNull(coupons);

		int sizeInDb= couponRepo.findCouponByTypeAndCompanyId(CouponType.CAMPING, teva.getLoginCompany().getId()).size();

		Assert.assertEquals(sizeInDb, coupons.size());

	}

	/**
	 * Test for getCouponsByType method of CompanyFacade.
	 * If in the database there are no coupons of the logged in company of the given type 
	 * the method throws CouponNotFoundException, and no coupons are displayed. 
	 */
	@Test  (expected = CouponNotFoundException.class)
	public void test_058_companyGetCouponsByType() {

		CouponSystem couponsystem = new CouponSystem(ctx);
		CompanyFacade teva = (CompanyFacade) couponsystem.login("TEVA", "123", ClientType.COMPANY);
		Collection <Coupon> coupons = teva.getCouponsByType(CouponType.FOOD);
		System.out.println(coupons);

		//cannot get coupons if the company does not have any coupons of this type
		Assert.assertNull(coupons);

	}

	//Get coupons by price
	/**
	 * Test for getCouponsByPrice method of CompanyFacade.
	 * If in the database there are coupons of the logged in company the price of which is lower
	 * than the given price, the method displays all these coupons. 
	 */
	@Test
	public void test_059_companyGetCouponsByPrice() {

		CouponSystem couponsystem = new CouponSystem(ctx);
		CompanyFacade teva = (CompanyFacade) couponsystem.login("TEVA", "123", ClientType.COMPANY);
		Collection <Coupon> coupons = teva.getCouponsByPrice(200);
		System.out.println(coupons);

		Assert.assertNotNull(coupons);

		int sizeInDb= couponRepo.findByMaxPriceAndCompanyId(200, teva.getLoginCompany().getId()).size();

		Assert.assertEquals(sizeInDb, coupons.size());

	}

	/**
	 * Test for getCouponsByPrice method of CompanyFacade.
	 * If in the database there are no coupons of the logged in company
	 * the price of which is lower than the given price, 
	 * the method throws CouponNotFoundException, and no coupons are displayed. 
	 */
	@Test  (expected = CouponNotFoundException.class)
	public void test_060_companyGetCouponsByPrice() {

		CouponSystem couponsystem = new CouponSystem(ctx);
		CompanyFacade teva = (CompanyFacade) couponsystem.login("TEVA", "123", ClientType.COMPANY);
		Collection <Coupon> coupons = teva.getCouponsByPrice(50);
		System.out.println(coupons);

		//cannot get coupons if the company does not have any coupons under this price
		Assert.assertNull(coupons);
	}

	//Get coupons by end date
	/**
	 * Test for getCouponsByEndDate method of CompanyFacade.
	 * If in the database there are coupons of the logged in company the end date of which is before
	 * the given end date, the method displays all these coupons. 
	 * 
	 * @throws ParseException if the end date could not be generated properly while parsing
	 */
	@Test
	public void test_061_companyGetCouponsByEndDate() throws ParseException {

		CouponSystem couponsystem = new CouponSystem(ctx);
		CompanyFacade teva = (CompanyFacade) couponsystem.login("TEVA", "123", ClientType.COMPANY);
		Date endDate=this.dateFormat.parse("2018-07-01");
		Collection <Coupon> coupons = 
				teva.getCouponsByEndDate(endDate);
		System.out.println(coupons);

		Assert.assertNotNull(coupons);

		int sizeInDb= couponRepo.findByMaxEndDateAndCompanyId(endDate,
				teva.getLoginCompany().getId()).size();

		Assert.assertEquals(sizeInDb, coupons.size());

	}

	/**
	 * Test for getCouponsByEndDate method of CompanyFacade.
	 * If in the database there are no coupons of the logged in company
	 * the end date of which is before the given end date, 
	 * the method throws CouponNotFoundException, and no coupons are displayed.
	 * 
	 * @throws ParseException if the end date could not be generated properly while parsing
	 */
	 
	@Test  (expected = CouponNotFoundException.class)
	public void test_062_companyGetCouponsByEndDate() throws ParseException {

		CouponSystem couponsystem = new CouponSystem(ctx);
		CompanyFacade teva = (CompanyFacade) couponsystem.login("TEVA", "123", ClientType.COMPANY);
		Date endDate=this.dateFormat.parse("2018-01-01");
		Collection <Coupon> coupons = 
				teva.getCouponsByEndDate(endDate);
		System.out.println(coupons);

		//cannot get coupons if the company does not have any coupons ending after this date
		Assert.assertNull(coupons);
	}


	//3. Test Customer Facade methods

	// login as a customer
	/**
	 * Test for login method of CustomerFacade class.
	 * When the username and password are correct, the method returns
	 * the instance of CustomerFacade.
	 */
	@Test
	public void test_063_customerLogin() {

		CouponSystem couponsystem = new CouponSystem(ctx);
		CustomerFacade avi = (CustomerFacade) couponsystem.login("Avi", "111", ClientType.CUSTOMER);

		Assert.assertNotNull(avi);
	}

	/**
	 * Test for login method of CustomerFacade class.
	 * When the the password is incorrect, the method throws
	 * WrongPasswordException and returns null.
	 */
	@Test (expected = WrongPasswordException.class)
	public void test_064_customerLogin() {

		CouponSystem couponsystem = new CouponSystem(ctx);
		CustomerFacade avi = (CustomerFacade) couponsystem.login("Avi", "123", ClientType.CUSTOMER);

		Assert.assertNull(avi);
	}


	/**
	 * Test for login method of CustomerFacade class.
	 * When the the username is incorrect, the method throws
	 * UserNotFoundException and returns null.
	 */
	@Test (expected = UserNotFoundException.class)
	public void test_065_customerLogin() { 

		CouponSystem couponsystem = new CouponSystem(ctx);
		CustomerFacade harel = (CustomerFacade) couponsystem.login("Harel", "123", ClientType.CUSTOMER);

		Assert.assertNull(harel);
	}


	//Purchase coupon
	/**
	 * Test for purchaseCoupon method of CustomerFacade.
	 * If the coupon details are correct and the logged in customer is allowed to purchase the coupon,
	 * the method adds it to the logged in customer's coupons in the database and updated the amount of available coupons. 
	 */
	@Test
	public void test_066_customerPurchaseCoupon() 	{

		CouponSystem couponsystem = new CouponSystem(ctx);
		CustomerFacade avi = (CustomerFacade) couponsystem.login("Avi", "111", ClientType.CUSTOMER);
		Coupon c = couponRepo.findByTitle("Shopping in the mall");

		int amountBefore= couponRepo.findByTitle("Shopping in the mall").getAmount();
		avi.purchaseCoupon(c);

		int amountAfter= couponRepo.findByTitle("Shopping in the mall").getAmount();

		Assert.assertNotNull(couponRepo.findCustomerCoupon(avi.getLoginCustomer().getId(), c.getId()));
		Assert.assertEquals(amountBefore -1, amountAfter);

	}

	/**
	 * Test for purchaseCoupon method of CustomerFacade.
	 * If the coupon details are correct and the logged in customer is allowed to purchase the coupon,
	 * the method adds it to the logged in customer's coupons in the database and updated the amount of available coupons. 
	 */
	@Test
	public void test_067_customerPurchaseCoupon() 	{

		CouponSystem couponsystem = new CouponSystem(ctx);
		CustomerFacade avi = (CustomerFacade) couponsystem.login("Avi", "111", ClientType.CUSTOMER);
		Coupon c = couponRepo.findByTitle("Free camping");

		int amountBefore= couponRepo.findByTitle("Free camping").getAmount();
		avi.purchaseCoupon(c);

		int amountAfter= couponRepo.findByTitle("Free camping").getAmount();

		Assert.assertNotNull(couponRepo.findCustomerCoupon(avi.getLoginCustomer().getId(), c.getId()));
		Assert.assertEquals(amountBefore -1, amountAfter);

	}

	/**
	 * Test for purchaseCoupon method of CustomerFacade.
	 * If the coupon does not exist in the database, the method throws CouponNotFoundException.
	 * The coupon is not added to the logged in customer's coupons and the amount of available coupons remains the same. 
	 */
	@Test (expected = CouponNotFoundException.class)
	public void test_068_customerPurchaseCoupon() 	{

		CouponSystem couponsystem = new CouponSystem(ctx);
		CustomerFacade avi = (CustomerFacade) couponsystem.login("Avi", "111", ClientType.CUSTOMER);
		Coupon c = new Coupon();
		c.setTitle("Empty coupon");

		avi.purchaseCoupon(c);

		//Cannot purchase coupon that does not exist
		Assert.assertNull(couponRepo.findCustomerCoupon(avi.getLoginCustomer().getId(), c.getId()));

	}	

	/**
	 * Test for purchaseCoupon method of CustomerFacade.
	 * If the coupon has been already purchased by the logged in customer,
	 * the method throws CouponAlreadyPurchasedException. 
	 * No coupon is added to the logged in customer's coupons and the amount of available coupons remains the same. 
	 */
	@Test (expected = CouponAlreadyPurchasedException.class)
	public void test_069_customerPurchaseCoupon() 	{

		CouponSystem couponsystem = new CouponSystem(ctx);
		CustomerFacade avi = (CustomerFacade) couponsystem.login("Avi", "111", ClientType.CUSTOMER);
		Coupon c = couponRepo.findByTitle("Shopping in the mall");

		int sizeBefore = couponRepo.findCustomerCoupons(avi.getLoginCustomer().getId()).size();

		//Cannot purchase coupon that was already purchased by this customer
		avi.purchaseCoupon(c);

		int sizeAfter = couponRepo.findCustomerCoupons(avi.getLoginCustomer().getId()).size();

		Assert.assertEquals(sizeBefore, sizeAfter);

	}

	/**
	 * Test for purchaseCoupon method of CustomerFacade.
	 * If the coupon details are correct and the logged in customer is allowed to purchase the coupon,
	 * the method adds it to the logged in customer's coupons in the database and updated the amount of available coupons. 
	 */
	@Test
	public void test_070_customerPurchaseCoupon() 	{

		CouponSystem couponsystem = new CouponSystem(ctx);
		CustomerFacade gabi = (CustomerFacade) couponsystem.login("Gabi", "444", ClientType.CUSTOMER);
		Coupon c = couponRepo.findByTitle("Free camping");

		int amountBefore= couponRepo.findByTitle("Free camping").getAmount();

		gabi.purchaseCoupon(c);

		int amountAfter= couponRepo.findByTitle("Free camping").getAmount();

		Assert.assertNotNull(couponRepo.findCustomerCoupon(gabi.getLoginCustomer().getId(), c.getId()));
		Assert.assertEquals(amountBefore -1, amountAfter);

	}

	/**
	 * Test for purchaseCoupon method of CustomerFacade.
	 * If the coupon details are correct and the logged in customer is allowed to purchase the coupon,
	 * the method adds it to the logged in customer's coupons in the database and updated the amount of available coupons. 
	 */
	@Test
	public void test_071_customerPurchaseCoupon() 	{

		CouponSystem couponsystem = new CouponSystem(ctx);
		CustomerFacade gabi = (CustomerFacade) couponsystem.login("Gabi", "444", ClientType.CUSTOMER);
		Coupon c = couponRepo.findByTitle("Flight to Ibiza");

		int amountBefore= couponRepo.findByTitle("Flight to Ibiza").getAmount();

		gabi.purchaseCoupon(c);

		int amountAfter= couponRepo.findByTitle("Flight to Ibiza").getAmount();

		Assert.assertNotNull(couponRepo.findCustomerCoupon(gabi.getLoginCustomer().getId(), c.getId()));
		Assert.assertEquals(amountBefore -1, amountAfter);

	}

	/**
	 * Test for purchaseCoupon method of CustomerFacade.
	 * If the coupon is out of stock, the method throws CouponOutOfStockException. 
	 * No coupon is added to the logged in customer's coupons and the amount of available coupons remains the same. 
	 */
	@Test  (expected = CouponOutOfStockException.class)
	public void test_072_customerPurchaseCoupon() 	{

		CouponSystem couponsystem = new CouponSystem(ctx);
		CustomerFacade dudi = (CustomerFacade) couponsystem.login("Dudi", "555", ClientType.CUSTOMER);
		Coupon c = couponRepo.findByTitle("Free camping");

		int amountBefore= couponRepo.findByTitle("Free camping").getAmount();

		dudi.purchaseCoupon(c);

		int amountAfter= couponRepo.findByTitle("Free camping").getAmount();

		//Cannot purchase coupon that is out of stock
		Assert.assertNull(couponRepo.findCustomerCoupon(dudi.getLoginCustomer().getId(), c.getId()));
		Assert.assertEquals(amountBefore, amountAfter);

	}

	/**
	 * Test for purchaseCoupon method of CustomerFacade.
	 * If the coupon has expired, the method throws CouponExpiredException. 
	 * No coupon is added to the logged in customer's coupons and the amount of available coupons remains the same. 
	 */
	@Test (expected = CouponExpiredException.class)
	public void test_073_customerPurchaseCoupon() 	{

		CouponSystem couponsystem = new CouponSystem(ctx);
		CustomerFacade dudi = (CustomerFacade) couponsystem.login("Dudi", "555", ClientType.CUSTOMER);
		Coupon c = couponRepo.findByTitle("Dinner for two");

		int amountBefore= couponRepo.findByTitle("Dinner for two").getAmount();

		dudi.purchaseCoupon(c);

		int amountAfter= couponRepo.findByTitle("Dinner for two").getAmount();


		//Cannot purchase coupon that has expired
		Assert.assertNull(couponRepo.findCustomerCoupon(dudi.getLoginCustomer().getId(), c.getId()));
		Assert.assertEquals(amountBefore, amountAfter);

	}

	//Get all customer's coupons

	/**
	 * Test for getAllPurchasedCoupons method of CustomerFacade.
	 * If there are any coupons purchased by the logged in customer in the database, these coupons are displayed. 
	 */
	@Test
	public void test_074_customerGetCoupons() 	{

		CouponSystem couponsystem = new CouponSystem(ctx);
		CustomerFacade avi = (CustomerFacade) couponsystem.login("Avi", "111", ClientType.CUSTOMER);
		Collection <Coupon> coupons = avi.getAllPurchasedCoupons();
		System.out.println(coupons);

		int sizeInDb= couponRepo.findCustomerCoupons(avi.getLoginCustomer().getId()).size();				

		Assert.assertNotNull(coupons);
		Assert.assertEquals(sizeInDb, coupons.size());

	}

	/**
	 * Test for getAllPurchasedCoupons method of CustomerFacade.
	 * If there are any coupons purchased by the logged in customer in the database, these coupons are displayed. 
	 */
	@Test
	public void test_075_customerGetCoupons() 	{

		CouponSystem couponsystem = new CouponSystem(ctx);
		CustomerFacade gabi = (CustomerFacade) couponsystem.login("Gabi", "444", ClientType.CUSTOMER);
		Collection <Coupon> coupons = gabi.getAllPurchasedCoupons();
		System.out.println(coupons);

		int sizeInDb= couponRepo.findCustomerCoupons(gabi.getLoginCustomer().getId()).size();				

		Assert.assertNotNull(coupons);
		Assert.assertEquals(sizeInDb, coupons.size());

	}

	/**
	 * Test for getAllPurchasedCoupons method of CustomerFacade.
	 * If there are no coupons purchased by the logged in customer in the database
	 * the method throws CouponNotFoundException, and no coupons are displayed. 
	 */
	@Test (expected = CouponNotFoundException.class)
	public void test_076_customerGetCoupons() 	{

		CouponSystem couponsystem = new CouponSystem(ctx);
		CustomerFacade dudi = (CustomerFacade) couponsystem.login("Dudi", "555", ClientType.CUSTOMER);
		Collection <Coupon> coupons = dudi.getAllPurchasedCoupons();
		System.out.println(coupons);

		//Cannot get coupons if the customer does not have any coupons
		Assert.assertNull(coupons);
	}


	//Get customer's coupons by type

	/**
	 * Test for getPurchasedCouponsByType method of CustomerFacade.
	 * If in the database there are any coupons of the given type 
	 * purchased by the logged in customer, these coupons are displayed. 
	 */
	@Test
	public void test_077_customerGetCouponsByType() 	{

		CouponSystem couponsystem = new CouponSystem(ctx);
		CustomerFacade avi = (CustomerFacade) couponsystem.login("Avi", "111", ClientType.CUSTOMER);
		Collection <Coupon> coupons = avi.getAllPurchasedCouponsByType(CouponType.CAMPING);
		System.out.println(coupons);

		int sizeInDb= couponRepo.findCustomerCouponsByType(avi.getLoginCustomer().getId(), CouponType.CAMPING).size();				

		Assert.assertNotNull(coupons);
		Assert.assertEquals(sizeInDb, coupons.size());

	}

	/**
	 * Test for getPurchasedCouponsByType method of CustomerFacade.
	 * If in the database there are any coupons of the given type 
	 * purchased by the logged in customer, these coupons are displayed. 
	 */
	@Test
	public void test_078_customerGetCouponsByType() 	{

		CouponSystem couponsystem = new CouponSystem(ctx);
		CustomerFacade gabi = (CustomerFacade) couponsystem.login("Gabi", "444", ClientType.CUSTOMER);
		Collection <Coupon> coupons = gabi.getAllPurchasedCouponsByType(CouponType.TRAVELLING);
		System.out.println(coupons);

		int sizeInDb= couponRepo.findCustomerCouponsByType(gabi.getLoginCustomer().getId(), CouponType.TRAVELLING).size();				

		Assert.assertNotNull(coupons);
		Assert.assertEquals(sizeInDb, coupons.size());

	}

	/**
	 * Test for getPurchasedCouponsByType method of CustomerFacade.
	 * If in the database there are no coupons of the given type 
	 * purchased by the logged in customer,
	 * the method throws CouponNotFoundException, and no coupons are displayed. 
	 */
	@Test (expected = CouponNotFoundException.class)
	public void test_079_customerGetCouponsByType() 	{

		CouponSystem couponsystem = new CouponSystem(ctx);
		CustomerFacade gabi = (CustomerFacade) couponsystem.login("Gabi", "444", ClientType.CUSTOMER);
		Collection <Coupon> coupons = gabi.getAllPurchasedCouponsByType(CouponType.ELECTRICITY);
		System.out.println(coupons);

		//Cannot get coupons if the customer does not have any coupons of this type
		Assert.assertNull(coupons);


	}

	//Get customer's coupons by price

	/**
	 * Test for getPurchasedCouponsByPrice method of CustomerFacade.
	 * If in the database there are any coupons the price of which is lower than the given price 
	 * purchased by the logged in customer, these coupons are displayed. 
	 */
	@Test
	public void test_080_customerGetCouponsByPrice() 	{

		CouponSystem couponsystem = new CouponSystem(ctx);
		CustomerFacade avi = (CustomerFacade) couponsystem.login("Avi", "111", ClientType.CUSTOMER);
		Collection <Coupon> coupons = avi.getAllPurchasedCouponsByPrice(500);
		System.out.println(coupons);

		int sizeInDb= couponRepo.findCustomerCouponsByMaxPrice(avi.getLoginCustomer().getId(), 500).size();				

		Assert.assertNotNull(coupons);
		Assert.assertEquals(sizeInDb, coupons.size());

	}

	/**
	 * Test for getPurchasedCouponsByPrice method of CustomerFacade.
	 * If in the database there are any coupons the price of which is lower than the given price 
	 * purchased by the logged in customer, these coupons are displayed. 
	 */
	@Test
	public void test_081_customerGetCouponsByPrice() 	{

		CouponSystem couponsystem = new CouponSystem(ctx);
		CustomerFacade gabi = (CustomerFacade) couponsystem.login("Gabi", "444", ClientType.CUSTOMER);
		Collection <Coupon> coupons = gabi.getAllPurchasedCouponsByPrice(400);
		System.out.println(coupons);

		int sizeInDb= couponRepo.findCustomerCouponsByMaxPrice(gabi.getLoginCustomer().getId(), 400).size();				

		Assert.assertNotNull(coupons);
		Assert.assertEquals(sizeInDb, coupons.size());

	}

	/**
	 * Test for getPurchasedCouponsByPrice method of CustomerFacade.
	 * If in the database there are no coupons the price of which is lower than the given price 
	 * purchased by the logged in customer,
	 * the method throws CouponNotFoundException, and no coupons are displayed. 
	 */
	public void test_082_customerGetCouponsByPrice() 	{

		CouponSystem couponsystem = new CouponSystem(ctx);
		CustomerFacade gabi = (CustomerFacade) couponsystem.login("Gabi", "444", ClientType.CUSTOMER);
		Collection <Coupon> coupons = gabi.getAllPurchasedCouponsByPrice(10);
		System.out.println(coupons);

		//Cannot get coupons if the customer does not have any coupons under this price
		Assert.assertNull(coupons);
	}


}
