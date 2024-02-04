package com.driver.controllers;

import com.driver.model.Airport;
import com.driver.model.City;
import com.driver.model.Flight;
import com.driver.model.Passenger;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;

@Repository
public class AirportRepository {
    HashMap<String,Airport> airportDb;
    HashMap<Integer,Flight> flightDb;
    HashMap<Integer,Passenger> passengerDb;
    HashMap<Integer,List<Integer>> fpDb;
    HashMap<Integer,List<Integer>> pfDb;

    AirportRepository(){
        this.airportDb = new HashMap<>();
        this.flightDb = new HashMap<>();
        this.passengerDb = new HashMap<>();
        this.fpDb = new HashMap<>();
        this.pfDb = new HashMap<>();
    }
    public void addAirport(Airport airport){
        airportDb.put(airport.getAirportName(),airport);
    }

    public List<String> getLargestAirportName(){
        List<String> largeAir = new ArrayList<>();
        int large = 0;
        for(String name : airportDb.keySet()){
            int term = airportDb.get(name).getNoOfTerminals();
            if(term>large){
                large = term;
                largeAir.clear();
            }
            if(term==large) largeAir.add(name);
        }
        return largeAir;
    }

    public double getShortestDurationOfPossibleBetweenTwoCities(City fromCity, City toCity){
        double duration = Double.MAX_VALUE;
        boolean anyflight = false;
        for (int flId : flightDb.keySet()){
            Flight fli = flightDb.get(flId);
            if (fli.getFromCity().equals(fromCity) && fli.getToCity().equals(toCity) && fli.getDuration()<duration){
                duration = fli.getDuration();
                anyflight = true;
            }
        }
        if(!anyflight) return -1;
        return duration;
    }

    public int getNumberOfPeopleOn(Date date, String airportName){
        int count = 0;
        City city = airportDb.get(airportName).getCity();
        for(int id : flightDb.keySet()){
            Flight flight = flightDb.get(id);
            if(flight.getFlightDate().equals(date)){
                if(flight.getToCity().equals(city) || flight.getFromCity().equals(city)){
                    count+=fpDb.get(id).size();
                }
            }
        }
        return count;
    }

    public int calculateFlightFare(Integer flightId){
        if(!fpDb.containsKey(flightId)) return 0;
        return 3000+(fpDb.get(flightId).size()*50);
    }


    public String bookATicket(Integer flightId,Integer passengerId){
        if(!flightDb.containsKey(flightId) || !passengerDb.containsKey(passengerId)) return "FAILURE";
        Flight flight = flightDb.get(flightId);
        if(fpDb.get(flightId).size()==flight.getMaxCapacity() || pfDb.containsKey(passengerId)) return "FAILURE";
        if(fpDb.get(flightId).contains(passengerId)) return "FAILURE";
        fpDb.get(flightId).add(passengerId);
        pfDb.get(passengerId).add(flightId);
        return "SUCCESS";
    }

    public String cancelATicket(Integer flightId,Integer passengerId){
        if(!flightDb.containsKey(flightId) || !passengerDb.containsKey(passengerId) || !fpDb.containsKey(flightId) || !pfDb.containsKey(passengerId)) return "FAILURE";
        int pass = pfDb.get(passengerId).indexOf(flightId);
        pfDb.get(passengerId).remove(pass);
        int fli = fpDb.get(flightId).indexOf(passengerId);
        fpDb.get(flightId).remove(fli);

        return "SUCCESS";
    }


    public int countOfBookingsDoneByPassengerAllCombined(Integer passengerId){
        if(!pfDb.containsKey(passengerId)) return 0;
        return pfDb.get(passengerId).size();
    }

    public String addFlight(Flight flight){
        if(!flightDb.containsKey(flight.getFlightId())){
            int id = flight.getFlightId();
            flightDb.put(id,flight);
            fpDb.put(id,new ArrayList<>());
            return "SUCCESS";
        }
        return null;
    }


    public String getAirportNameFromFlightId(Integer flightId){
        if(flightDb.containsKey(flightId)){
            City city = flightDb.get(flightId).getFromCity();
            for(String name : airportDb.keySet()){
                if(airportDb.get(name).getCity().equals(city)) return name;
            }
        }
        return null;
    }


    public int calculateRevenueOfAFlight(Integer flightId){
        int totalRev = 0;
        if(fpDb.containsKey(flightId)){
            for(int i=0;i<fpDb.get(flightId).size();i++){
                totalRev+=3000+(i*50);
            }
        }
        return totalRev;
    }


    public String addPassenger(Passenger passenger){
        if(!passengerDb.containsKey(passenger)){
            int id = passenger.getPassengerId();
            passengerDb.put(id,passenger);
            pfDb.put(id,new ArrayList<>());
            return "SUCCESS";
        }
        return null;
    }
}
