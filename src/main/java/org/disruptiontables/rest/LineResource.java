package org.disruptiontables.rest;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.disruptiontables.dao.Branch;
import org.disruptiontables.dao.Line;
import org.disruptiontables.service.LineService;

@Path("lines")
public class LineResource {

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<Line> getAllLines(){
		LineService ls = new LineService();
		return ls.getAllLines();
	}
	
	
	@GET
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Line getLine(@PathParam("id") int id){
		LineService ls = new LineService();
		return ls.getLine(id);
	}
	
	
	@GET
	@Path("/{id}/branches")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Branch> getSortedLineBranches(@PathParam("id") int id, @Context UriInfo info){
		
		String from = info.getQueryParameters().getFirst("from");
		String to = info.getQueryParameters().getFirst("to");
		System.out.println("to: "+to+" from: "+from);
		LineService ls = new LineService();
		
		if(from==null || to==null)
			return ls.getAllLineBranches(id);
		else
			return ls.getSortedLineBranches(id, Integer.parseInt(from), Integer.parseInt(to));
	}
	
	
}
