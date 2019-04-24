package com.sp.hotel;

import java.io.File;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.sp.admin.AdminSessionInfo;
import com.sp.common.MyUtil;

@Controller("hotel.hotelController")
public class HotelController {
	
	@Autowired
	private HotelService hotelService;
	
	@Autowired
	private MyUtil myUtil;
	
	
	
	@RequestMapping(value= {"/hotel/main"}, method=RequestMethod.GET)
	public String method() {
		return ".hotel.main";
	}
	
	@RequestMapping(value="/hotel/hotelInfo/list")
	public String reserveList(
			@RequestParam(value="pageNo", defaultValue="1") int current_page,
			@RequestParam(defaultValue="hname") String searchKey,
			@RequestParam(defaultValue="") String searchValue,
			HttpServletRequest req,
			Model model) throws Exception {
		
		if(req.getMethod().equalsIgnoreCase("GET")) {
			searchValue=URLDecoder.decode(searchValue, "UTF-8");
		}
		int rows=10;
		int total_page=0;
		int hotelCount=0;
		
		Map<String, Object> map = new HashMap<>();
		map.put("searchKey", searchKey);
		map.put("searchValue", searchValue);
		
		hotelCount=hotelService.hotelCount(map);
		if(hotelCount!=0) {
			total_page=myUtil.pageCount(rows, hotelCount);
		}
		
		if(current_page>total_page) {
			current_page=total_page;
		}
		
		int start = (current_page-1)*rows+1;
		int end = current_page*rows;
		
		map.put("start", start);
		map.put("end", end);
		List<Hotel> list = hotelService.listHotel(map);
		
		String paging = myUtil.pagingMethod(current_page, total_page, "listPage");
		model.addAttribute("list", list);
		model.addAttribute("hotelCount", hotelCount);
		model.addAttribute("page", current_page);
		model.addAttribute("paging", paging);
		model.addAttribute("searchKey", searchKey);
		model.addAttribute("searchValue",searchValue);
		
		return ".hotel.hotelInfo.list";
	}
	
	
	@RequestMapping(value="/hotel/customer/reserveList")
	public String customerList() {
		return ".hotel.customer.reserveList";
	}
	
	@RequestMapping(value="/hotel/hotelInfo/insertHotel", method=RequestMethod.GET)
	public String insertHotelForm(Model model) {
		
		model.addAttribute("mode", "created");
		
		return "hotel/hotelInfo/insertHotel";
	}
	
	@RequestMapping(value="/hotel/hotelInfo/insertHotel", method=RequestMethod.POST)
	public String insertHotelSubmit(Hotel dto, HttpSession session, Model model) {
		
		AdminSessionInfo info = (AdminSessionInfo)session.getAttribute("admin");
		String root = session.getServletContext().getRealPath("/");
		
		String pathname=root+"uploads"+File.separator+"hotel";
	
		dto.setAdminIdx(info.getAdminIdx());
		
		hotelService.insertHotel(dto, pathname);
		
		return "redirect:/hotel/hotelInfo/list";
	}
	
	@RequestMapping(value="/hotel/event/listEvent")
	public String listEvent() {
		return ".hotel.event.listEvent";
	}
	
	@RequestMapping(value="/hotel/hotelInfo/updateHotel", method=RequestMethod.GET)
	public String updateHotelForm(
			@RequestParam int hotelCode,
			Model model) {
	
		Hotel dto = hotelService.readHotel(hotelCode);

		model.addAttribute("mode", "update");
		model.addAttribute("dto", dto);
		return "hotel/hotelInfo/insertHotel";
	}
	

	@RequestMapping(value="/hotel/hotelInfo/updateHotel", method=RequestMethod.POST)
	public String updateHotelSubmit(
			Hotel dto,
			HttpSession session) throws Exception {
		
		String root = session.getServletContext().getRealPath("/");
		String pathname = root+"uploads"+File.separator+"hotel";
		

		hotelService.updateHotel(dto, pathname);

		return "redirect:/hotel/hotelInfo/list";
	}
	
	@RequestMapping(value="/hotel/hotelInfo/roomInfo/list", method=RequestMethod.GET)
	public String listRoom(
			@RequestParam int hotelCode,
			Model model) {
		Hotel hotel = hotelService.readHotel(hotelCode);
		List<Room> list = hotelService.listRoom();
		
		model.addAttribute("list", list);
		model.addAttribute("hotel", hotel);
		
		return ".hotel.hotelInfo.roomInfo.list";
	}
	
	@RequestMapping(value="/hotel/hotelInfo/roomInfo/insertRoom", method=RequestMethod.GET)
	public String insertRoomForm(
			@RequestParam int hotelCode,
			Model model) {
		Hotel hotel = hotelService.readHotel(hotelCode);
		String hname=hotel.getHname();
		
		model.addAttribute("hotel", hotel);
		model.addAttribute("hname", hname);
		model.addAttribute("hotelCode", hotelCode);
		model.addAttribute("mode", "created");
		
		return ".hotel.hotelInfo.roomInfo.insertRoom";
	}
	
	@RequestMapping(value="/hotel/hotelInfo/roomInfo/insertRoom", method=RequestMethod.POST)
	public String insertRoomSubmit(
			Room dto,
			Model model,
			HttpSession session
			) throws Exception{
		
		String root = session.getServletContext().getRealPath("/");
		String pathname = root+"uploads"+File.separator+"hotel";
		
		hotelService.insertRoom(dto, pathname);
		
		return "redirect:/hotel/hotelInfo/roomInfo/list?hotelCode="+dto.getHotelCode();
	}

	@RequestMapping(value="/hotel/hotelInfo/roomInfo/updateRoom", method=RequestMethod.GET)
	public String updateRoomForm(
			@RequestParam int roomCode,
			@RequestParam int hotelCode,
			Model model) {
		
		Hotel hotel = hotelService.readHotel(hotelCode);
		
		Room dto = hotelService.readRoom(roomCode);
		List<Room> listFile = hotelService.listFile(roomCode);
		
		model.addAttribute("hotel", hotel);
		
		model.addAttribute("dto", dto);
		model.addAttribute("listFile", listFile);
		model.addAttribute("mode", "update");
		
		return ".hotel.hotelInfo.roomInfo.insertRoom";
	}
	
	@RequestMapping(value="/hotel/hotelInfo/roomInfo/updateRoom", method=RequestMethod.POST)
	public String updateRoomSubmit(
			Room dto,
			HttpSession session) throws Exception {
		
		String root = session.getServletContext().getRealPath("/");
		String pathname = root+"uploads"+File.separator+"hotel";
		

		hotelService.updateRoom(dto, pathname);

		return "redirect:/hotel/hotelInfo/roomInfo/list?hotelCode="+dto.getHotelCode();
	}

}
