package com.sp.ticket;

import java.io.File;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.sp.common.MyUtil;

@Controller("ticket.TicketController")
public class TicketController {
	
	@Autowired
	private TicketService ticketService;
	
	@Autowired
	private MyUtil myUtil;
	
	
	
	@RequestMapping(value= {"/ticket/main"}, method=RequestMethod.GET)
	public String method() {
		return ".ticket.main";
	}
	
	@RequestMapping(value="/ticket/ticketInfo/list", method=RequestMethod.GET)
	public String listTicket(
			Model model) {
		
		
		return ".ticket.list";
	}
	
	@RequestMapping(value="/ticket/ticketInfo/insertTicket", method=RequestMethod.GET)
	public String insertTicketForm(
			Model model) {

		model.addAttribute("mode", "created");
		
		return ".ticket.ticketInfo.insertTicket";
	}
	
	@RequestMapping(value="/ticket/ticketInfo/insertTicket", method=RequestMethod.POST)
	public String insertRoomSubmit(
			Ticket dto,
			Model model,
			HttpSession session
			) throws Exception{
		
		String root = session.getServletContext().getRealPath("/");
		String pathname = root+"uploads"+File.separator+"hotel";
		
		//ticketService.insertRoom(dto, pathname);
		
		return "redirect:/ticket/ticketInfo/list";
	}



}
