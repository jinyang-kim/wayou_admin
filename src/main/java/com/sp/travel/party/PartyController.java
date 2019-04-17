package com.sp.travel.party;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.sp.common.MyUtil;

@Controller("travel.party.partyController")
public class PartyController {
	@Autowired
	private PartyService partyService;
	@Autowired
	private MyUtil myUtil;
	
	@RequestMapping(value="/travel/admin/party/list")
	public String listParty(@RequestParam(value="page", defaultValue="1") int current_page,
			@RequestParam(defaultValue="all") String searchKey,
			@RequestParam(defaultValue="") String searchValue,
			HttpServletRequest req,
			Model model) throws Exception {
		
		if(req.getMethod().equalsIgnoreCase("GET")) {
			searchValue = URLDecoder.decode(searchValue, "UTF-8");
		}
		
		int total_page = 0;
		int dataCount = 0;
		int rows = 10;
		
		Map<String, Object> map = new HashMap<>();
		map.put("searchKey", searchKey);
		map.put("searchValue", searchValue);
		
		dataCount = partyService.dataCount(map);
		
		if(dataCount != 0) {
			total_page = myUtil.pageCount(rows, dataCount);
		}
		
		if(current_page > total_page) {
			current_page = total_page;
		}
		
		int start = (current_page - 1) * rows + 1;
		int end = current_page * rows;
		
		map.put("start", start);
		map.put("end", end);
		
		List<Party> list = partyService.listParty(map);
		
		int listNum, n = 0;
		for(Party dto : list) {
			listNum = dataCount - (start + n-1);
			dto.setListNum(listNum);
			n++;
		}
		
		for(Party dto : list) {
			dto.setContent(myUtil.htmlSymbols(dto.getContent()));
		}
		
		String cp = req.getContextPath();
		String query = "";
		String listUrl = cp + "/travel/admin/party/list";
		String articleUrl = cp + "/travel/admin/party/view?page=" + current_page;
		
		if(searchValue.length()!=0) {
			query = "searchKey=" + searchKey + "&searchValue=" + URLEncoder.encode(searchValue, "UTF-8");
			
			listUrl += "?" + query;
			articleUrl += "&" + query;
		}
		
		String paging = myUtil.paging(current_page, total_page, listUrl);
		
		model.addAttribute("list", list);
		model.addAttribute("dataCount", dataCount);
		model.addAttribute("page", current_page);
		model.addAttribute("total_page", total_page);
		model.addAttribute("paging", paging);
		model.addAttribute("articleUrl", articleUrl);
		model.addAttribute("searchKey", searchKey);
		model.addAttribute("searchValue", searchValue);
		
		return ".travel.party.list";
	}
	
	@RequestMapping(value="/travel/admin/party/view")
	public String readParty(@RequestParam int partyCode,
			@RequestParam(value="page", defaultValue="1") int current_page,
			@RequestParam(defaultValue="all") String searchKey,
			@RequestParam(defaultValue="") String searchValue,
			HttpServletRequest req,
			Model model) throws Exception {
		
		if(req.getMethod().equalsIgnoreCase("GET")) {
			searchValue = URLDecoder.decode(searchValue, "UTF-8");
		}
		
		String query = "page=" + current_page;
		if(searchValue.length() != 0) {
			query += "&searchKey=" + searchKey + "&searchValue=" + URLEncoder.encode(searchValue, "UTF-8");
		}
		
		Party dto = partyService.readParty(partyCode);
		
		if(dto == null) {
			return "redirect:/travel/admin/party/list?" + query;
		}
		
		dto.setContent(myUtil.htmlSymbols(dto.getContent()));
		
		Map<String, Object> map = new HashMap<>();
		map.put("searchKey", searchKey);
		map.put("searchValue", searchValue);
		map.put("partyCode", partyCode);
		
		Party preReadParty = partyService.preReadParty(map);
		Party nextReadParty = partyService.nextReadParty(map);
		
		model.addAttribute("dto", dto);
		model.addAttribute("page", current_page);
		model.addAttribute("preReadParty", preReadParty);
		model.addAttribute("nextReadParty", nextReadParty);
		model.addAttribute("query", query);
		
		return ".travel.party.view";
	}
}