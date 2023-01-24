package com.driver.services.impl;

import com.driver.model.*;
import com.driver.services.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.driver.repository.CustomerRepository;
import com.driver.repository.DriverRepository;
import com.driver.repository.TripBookingRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class CustomerServiceImpl implements CustomerService {

	@Autowired
	CustomerRepository customerRepository2;

	@Autowired
	DriverRepository driverRepository2;

	@Autowired
	TripBookingRepository tripBookingRepository2;

	@Override
	public void register(Customer customer) {
		//Save the customer in database
		Customer customer1= customerRepository2.save(customer);
	}

	@Override
	public void deleteCustomer(Integer customerId) {
		// Delete customer without using deleteById function
		Customer customer= customerRepository2.findById(customerId).get();
		customerRepository2.delete(customer);

	}

	@Override
	public TripBooking bookTrip(int customerId, String fromLocation, String toLocation, int distanceInKm) throws Exception{
		//Book the driver with lowest driverId who is free (cab available variable is Boolean.TRUE). If no driver is available, throw "No cab available!" exception
		//Avoid using SQL query

		Customer customer= customerRepository2.findById(customerId).get();
		List<Driver> driverList= driverRepository2.findAll();

		Collections.sort(driverList,(s1,s2) -> s1.getDriverId()- s2.getDriverId());
		Driver driver=null;

		for(Driver driver1: driverList){
			if(driver1.getCab().getAvailable()){
				driver=driver1;
				break;
			}
		}
		if(driver==null){
			throw new Exception("No cab available!");
		}

		TripBooking tripBooking=new TripBooking();
		tripBooking.setDistanceInKm(distanceInKm);
		tripBooking.setToLocation(toLocation);
		tripBooking.setFromLocation(fromLocation);
		tripBooking.setCustomer(customer);
		tripBooking.setStatus(TripStatus.CONFIRMED);
		tripBooking.setBill(driver.getCab().getPerKmRate()*distanceInKm);
		tripBooking.setDriver(driver);

		List<TripBooking> tripBookingList= driver.getTripBookingList();

		if(tripBookingList==null){
			tripBookingList=new ArrayList<>();
		}

		driver.setTripBookingList(tripBookingList);

		List<TripBooking> customerTripBooking= customer.getTripBookingList();
		if(customerTripBooking==null){
			customerTripBooking=new ArrayList<>();
		}

		customer.setTripBookingList(customerTripBooking);

		customerRepository2.save(customer);
		tripBookingRepository2.save(tripBooking);
		driverRepository2.save(driver);

		return tripBooking;
	}

	@Override
	public void cancelTrip(Integer tripId){
		//Cancel the trip having given trip Id and update TripBooking attributes accordingly
		TripBooking tripBooking= tripBookingRepository2.findById(tripId).get();
		tripBooking.getDriver().getCab().setAvailable(true);
		tripBooking.setStatus(TripStatus.CANCELED);
		tripBooking.setBill(0);

		tripBookingRepository2.save(tripBooking);
	}

	@Override
	public void completeTrip(Integer tripId){
		//Complete the trip having given trip Id and update TripBooking attributes accordingly
		TripBooking tripBooking= tripBookingRepository2.findById(tripId).get();
		tripBooking.getDriver().getCab().setAvailable(true);
		tripBooking.setStatus(TripStatus.COMPLETED);

		tripBookingRepository2.save(tripBooking);
	}
}
