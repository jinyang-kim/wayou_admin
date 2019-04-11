package com.sp.admin;

import java.io.File;
import java.net.URLDecoder;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.sp.common.FileManager;
import com.sp.common.MyUtil;

@Controller("admin.adminController")
public class AdminController {
	@Autowired
	private FileManager fileManager;
	
	@Autowired
	private MyUtil myUtil;
	
	@Autowired
	private AdminService adminService;
	
	@RequestMapping(value="/admin/login", method=RequestMethod.GET)
	public String login() {
		return "login/login";
	}
	
	@RequestMapping(value="/admin/login", method=RequestMethod.POST)
	@ResponseBody
	public Map<String,Object> loginSubmit(
			Admin admin,
			HttpSession session) {
			Map<String,Object> map = new HashMap<>();
			Admin temp = adminService.readLoginInfo(admin.getAdminId());
			boolean isAdmin = false;
			if(temp != null && temp.getAdminPwd().equals(admin.getAdminPwd())){
				AdminSessionInfo info = new AdminSessionInfo();
				info.setAdminIdx(temp.getAdminIdx());
				info.setAdminId(temp.getAdminId());
				info.setAdminName(temp.getAdminName());
				info.setIdnCode(temp.getIdnCode());
				info.setDepartCode(temp.getDepartCode());
				session.setAttribute("admin", info);
				isAdmin = true;
			}
			String uri = (String)session.getAttribute("preLoginURI");
			session.removeAttribute("preLoginURI");
			
			map.put("isAdmin", isAdmin);
			map.put("uri", uri);
		return map;
	}
	
	@RequestMapping(value="/admin/logout")
	public String logout(HttpSession session){
		session.removeAttribute("admin");
		session.invalidate();
		return "redirect:/main";
	}
	
	@RequestMapping(value="/admin/sabun", method=RequestMethod.GET)
	@ResponseBody
	public Map<String, Object> findSeq(){
		Map<String, Object> model = new HashMap<>();
		int result=adminService.findSeq();
		Calendar cal = Calendar.getInstance();
		int year=cal.get(Calendar.YEAR);
		String answer=String.format("%04d", result);
		answer=year+answer;
		System.out.println(answer);
		
		model.put("adminId", answer);
		return model;
	}
	
	@RequestMapping(value="/admin/bebun", method=RequestMethod.GET)
	@ResponseBody
	public Map<String, Object> findbebun(
			@RequestParam int adminIdx){
		Map<String, Object> model = new HashMap<>();
		String result=adminService.findBebun(adminIdx);
		
		model.put("adminPwd", result);
		return model;
	}
	
	@RequestMapping(value="/admin/created", method=RequestMethod.GET)
	public String adminForm(
			Model model,
			HttpSession session,
			RedirectAttributes ra,
			HttpServletResponse resp){
		AdminSessionInfo info = (AdminSessionInfo)session.getAttribute("admin");
		if(info.getIdnCode() !=2) {
			ra.addFlashAttribute("state","등록 권한이 없습니다.");
			return "redirect:/main";
		}
		
		model.addAttribute("mode", "created");
		return ".insa.created"; 
	}
	
	@RequestMapping(value="/admin/created", method=RequestMethod.POST)
	public String adminSubmit(
			Admin dto, 
			HttpSession session,
			Model model){
		String root = session.getServletContext().getRealPath("/");
		String pathname = root + "uploads" + File.separator + "admin";

		int result= adminService.insertAdmin(dto, pathname);
		if(result==0) {
			return "redirect:/admin/created";
		}else return "redirect:/admin/list";
	}

	@RequestMapping(value="/admin/list")
	public String adminlist(
			@RequestParam(value = "pageNum", defaultValue="1") int current_page,
			@RequestParam(defaultValue="departCode") String condition,
			@RequestParam(defaultValue="") String word,
			HttpServletRequest req,
			Model model) throws Exception{
		
		if(req.getMethod().equalsIgnoreCase("GET"))
			word=URLDecoder.decode(word, "UTF-8");
		
		int rows =5;
		int dataCount =0;
		int total_page=0;
		
		Map<String, Object> map = new HashMap<>();
		map.put("condition", condition);
		map.put("word", word);
		dataCount=adminService.dataCount(map);
		
		if(dataCount !=0)
			total_page=myUtil.pageCount(rows, dataCount);
		
		if(current_page>total_page)
			current_page=total_page;
		
		int start=(current_page-1) * rows +1;
		int end = current_page * rows;
		
		map.put("start", start);
		map.put("end", end);
		List<Admin> listAdmin=adminService.listAdmin(map);
		
		int listNum;
		int n=0;
		
		for(Admin dto:listAdmin) {
			listNum=dataCount -(start+n-1);
			dto.setListNum(listNum);
			n++;
		}
		
		String paging = myUtil.pagingMethod(current_page, total_page, "listPage");
		model.addAttribute("listAdmin", listAdmin);
		model.addAttribute("dataCount", dataCount);
		model.addAttribute("page", current_page);
		model.addAttribute("total_page", total_page);
		model.addAttribute("paging", paging);
		model.addAttribute("condition", condition);
		model.addAttribute("word", word);
		
		return ".insa.list";
	}
	
	@RequestMapping(value="/admin/articleAdmin")
	public String adminArticle (
			@RequestParam int adminIdx,
			@RequestParam(defaultValue="1") int pageNum,
			@RequestParam(defaultValue="departCode") String condition,
			@RequestParam(defaultValue="") String word,
			HttpSession session,
			HttpServletRequest req,
			Model model) throws Exception{		
		Admin dto = adminService.articleAdmin(adminIdx);
		if(dto==null) {
			return "redirect:/admin/list?page="+pageNum;
		}
		word=URLDecoder.decode(word, "UTF-8");
		
		model.addAttribute("dto",dto);
		model.addAttribute("comdition",condition);
		model.addAttribute("word",word);
		model.addAttribute("pageNum",pageNum);

		
		return ".insa.article";
	}
	
	@RequestMapping(value="/admin/updateAdmin", method=RequestMethod.GET)
	public String adminUpdateForm(
			@RequestParam int adminIdx,
			@RequestParam(defaultValue="1") int pageNum,
			@RequestParam(defaultValue="departCode") String condition,
			@RequestParam(defaultValue="") String word,
			HttpSession session,
			RedirectAttributes ra,
			Model model
			) throws Exception{
		System.out.println(adminIdx);
		Admin dto = adminService.articleAdmin(adminIdx);
		AdminSessionInfo info = (AdminSessionInfo)session.getAttribute("admin");
		
		String em[]=dto.getEmail().split("@");
		dto.setEmail1(em[0]);
		dto.setEmail2(em[1]);
		
		String tt[]=dto.getTel().split("-");
		dto.setTel1(tt[0]);
		dto.setTel2(tt[1]);
		dto.setTel3(tt[2]);
		
		if(dto == null) {
			return "redirect:/admin/list";
		}
		
		if(info.getIdnCode() !=2) {
			ra.addFlashAttribute("state","수정 권한이 없습니다.");
			return "redirect:/admin/list";
		}
		model.addAttribute("dto",dto);
		model.addAttribute("mode","update");
		model.addAttribute("pageNum",pageNum);
		
		return ".insa.created";
	}
	
	@RequestMapping(value="/admin/updateAdmin", method=RequestMethod.POST)
	public String adminUpdateSubmit(
			Admin dto,
			@RequestParam(defaultValue="1") int pageNum,
			@RequestParam(defaultValue="departCode") String condition,
			@RequestParam(defaultValue="") String word,
			HttpSession session
			) throws Exception{
		
		String root= session.getServletContext().getRealPath("/");
		String pathname = root + "uploads" + File.separator+"admin";
		
		adminService.updateAdmin(dto, pathname);
		
		return "redirect:/admin/list?pageNum="+pageNum;
	}
	
	@RequestMapping(value="/amdin/deleteFile")
	public String deleteFile(
			@RequestParam int adminIdx,
			@RequestParam int pageNum,
			HttpSession session
			) throws Exception{
		AdminSessionInfo info = (AdminSessionInfo)session.getAttribute("admin");
		String root=session.getServletContext().getRealPath("/");
		String pathname=root+"uploads"+File.separator+"amdin";
		
		Admin dto = adminService.articleAdmin(adminIdx);
		if(dto==null) {
			return "redirect:/admin/list?pageNum="+pageNum;
		}
		
		if(dto.getSaveFilename()!=null) {
			fileManager.doFileDelete(dto.getSaveFilename(), pathname);
			dto.setSaveFilename("");
			adminService.updateAdmin(dto, pathname);
		}
		
		return "redirect:/admin/update?adminIdx="+adminIdx+"&pageNum="+pageNum;
	}
}
