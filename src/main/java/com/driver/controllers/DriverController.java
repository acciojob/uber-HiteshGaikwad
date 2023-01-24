package com.driver.controllers;

import com.driver.services.DriverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping(value = "/driver")
public class DriverController {

	@Autowired
	DriverService driverService;
	
	@PostMapping(value = "/register")
	public ResponseEntity<Void> registerDriver(@RequestParam("mobile") String mobile, @RequestParam("password") String password){
		driverService.register(mobile,password);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@DeleteMapping(value = "/delete")
	public void deleteDriver(@RequestParam("driverId") Integer driverId){
		driverService.removeDriver(driverId);
	}

	@PutMapping("/status")
	public void updateStatus(@RequestParam("driverId") Integer driverId){
		driverService.updateStatus(driverId);
	}
}
