package org.disruptiontables.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.disruptiontables.dao.Branch;
import org.disruptiontables.dao.Station;
import org.disruptiontables.dao.WriterDao;
import org.disruptiontables.util.ParseData;

public class StationService {

	/**
	 * Method returns list of all stations
	 * */
	public List<Station> getAllStations(){
		WriterDao dao = new WriterDao();
		List<Station> stationList = new ArrayList<>();
		
		try{
			dao.setDBConnection();
			stationList = dao.getAllStations();
		}
		catch(Exception e){
			System.out.println("StationService error: "+e.toString());
		}
		return stationList;
	}
	
	
	/**
	 * Method returns list of all stations for a line
	 * */
	public List<Station> getAllStations(int line){
		WriterDao dao = new WriterDao();
		List<Station> stationList = new ArrayList<>();
		
		try{
			dao.setDBConnection();
			stationList = dao.getAllStations();
		}
		catch(Exception e){
			System.out.println("StationService error: "+e.toString());
		}
		return stationList;
	}
	
	/**
	 * Method returns a sorted list of stations
	 * */
	public List<Station> getSortedStations(String fromStr, String toStr, String lineStr){
		
		System.out.println("sorted stations");
		ParseData parse = new ParseData();
		int to, from, line;
		boolean directionFlag=false;
		
		//try to parse input String to integer, otherwise lookup DB to see if the name matches any station
		to = (parse.parseStringToInt(toStr)) ? Integer.parseInt(toStr) : getStationId(toStr);
		from = (parse.parseStringToInt(fromStr)) ? Integer.parseInt(fromStr) : getStationId(fromStr);
		line = (parse.parseStringToInt(lineStr)) ? Integer.parseInt(lineStr) : -1;
		
		//if going form east to west we replace values and switch the list at the end
		if(from>to){
			directionFlag=true;
			int temp =from;
			from = to;
			to=temp;
		}
		
		System.out.println("Value of to "+String.valueOf(to));
		
		List<Station> stationList = new ArrayList<Station>();
		List<Branch> branchList = new ArrayList<Branch>();
		
		System.out.println("sorted stations");
		try{
			WriterDao dao = new WriterDao();
			dao.setDBConnection();
		
			//get line branches form the DB
			branchList = dao.getAllLineBranches(line);
			
			//lookup on which branch the station in located
			dao = new WriterDao();
			dao.setDBConnection();
			int branchFrom = dao.getBranchByStationId(from);
			
			dao = new WriterDao();
			dao.setDBConnection();
			int branchTo = dao.getBranchByStationId(to);
			
			
			//sort branches of the line from start to end
			LineService ls = new LineService();
			branchList = ls.sortBranches(branchList, branchFrom, branchTo);
			
			//create new connection for new DB query
			dao = new WriterDao();
			dao.setDBConnection();
			
			//get list of stations based start and end location
			stationList = dao.getSortedStations(branchList);
			//TODO: get sorting done for 1 branch only (exmpl from 2 to 5); remove previous branches from the tree
			
			//dao.setDBConnection();
			//stationList = dao.getAllStations();
		}
		catch(Exception e){
			System.out.println("StationService error: "+e.toString());
		}
		
		//trimming the list of stations to be exactly form and to right station
		stationList = parse.sortStations(from, to, stationList);
		
		if(directionFlag)
			Collections.reverse(stationList);
		
		return stationList;
	}
	
	
	public int getStationId(String stationName){
		WriterDao dao = new WriterDao();
		Station station = new Station();
		
		try{
			dao.setDBConnection();
			station = dao.getStationId(stationName);
		}
		catch(Exception e){
			System.out.println("getStationId error: "+e.toString());
		}
		return station.getStationId();
	}
	
	
}
